package uk.co.epii.conservatives.henryaddington.voa;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 07/10/2013
 * Time: 16:59
 */
public class VOADownloader {

    private static final Logger LOG = LoggerFactory.getLogger(VOADownloader.class);

    private WebClient webClient;
    private HtmlForm htmlForm;
    private HtmlSelect authoritiesSelect;
    private HtmlButton searchButton;
    private HtmlSelect councilTaxBandSelect;
    private TreeMap<String, String> localAuthorityCodes;
    private ArrayList<String> bands;

    private String advancedSearchFormId = null;
    private String localAuthoritySelectId = null;
    private String councilTaxBandsSelectId = null;
    private String saveLocationRoot = null;
    private String voaUri = null;

    public VOADownloader()
    {
        init();
    }

    public HtmlPage getPage(String uri) {
        try {
            return webClient.getPage(uri);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HtmlPage setSelectedAttribute(HtmlSelect s, String attribute)
    {
        int fails = 0;
        while (true)
            try {
                return (HtmlPage)s.setSelectedAttribute(attribute, true);
            }
            catch (Throwable ioe) {
                if (fails < 10)
                    fails++;
                long t = 1000L * (long)Math.pow(2.0D, fails);
                try {
                    Thread.sleep(t);
                }
                catch (InterruptedException ie) {
                    LOG.error(ie.getMessage(), ie);
                }
            }
    }

    public void init()
    {
        LOG.info("Initializing VOAProcessor");
        this.webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        this.webClient.setThrowExceptionOnScriptError(false);
        this.webClient.setThrowExceptionOnFailingStatusCode(false);
        LOG.info("Downloading page ...");
        HtmlPage page = getPage(voaUri);
        List<HtmlForm> forms = page.getForms();
        htmlForm = null;
        for (HtmlForm f : forms)
            if (forms.get(0).getId().equals(advancedSearchFormId)) {
                htmlForm = f;
                break;
            }
        if (htmlForm == null) {
            throw new RuntimeException(new StringBuilder().append("Form with id \"").append(advancedSearchFormId).append("\" can not be found").toString());
        }

        LOG.info("Found form");
        authoritiesSelect = htmlForm.getElementById(localAuthoritySelectId);
        localAuthorityCodes = new TreeMap();
        for (HtmlOption o : this.authoritiesSelect.getOptions()) {
            this.localAuthorityCodes.put(o.getText().toUpperCase(), o.getAttribute("value"));
        }
        this.localAuthorityCodes.remove("");
        LOG.info("Got all council codes");
        councilTaxBandSelect = htmlForm.getElementById(councilTaxBandsSelectId);
        this.bands = new ArrayList();
        for (HtmlOption o : councilTaxBandSelect.getOptions())
            this.bands.add(o.getText());
        this.bands.remove(0);
        LOG.info("Got all council bands");
        for (HtmlElement e : htmlForm.getElementsByTagName("button"))
            if ((e.getAttribute("value").equals("Search")) && (e.getAttribute("type").equals("submit")))
                searchButton = ((HtmlButton)e);
    }

    public void download(String council, String band) {
        while (true) {
            String value = this.localAuthorityCodes.get(council.toUpperCase());
            if (!bands.contains(band)) throw new IllegalArgumentException(new StringBuilder().append("Unknown band: ").append(band).toString());
            if (value == null) throw new IllegalArgumentException(new StringBuilder().append("Unknown council: ").append(council).toString());
            authoritiesSelect.setSelectedAttribute(value, true);
            councilTaxBandSelect.setSelectedAttribute(band, true);
            LOG.info("Loading first page");
            HtmlPage results = null;
            try {
                results = this.searchButton.click();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String pattern = "Showing [0-9]* - [0-9]* of ([0-9]*)";
            Pattern p = Pattern.compile(pattern);
            LOG.info("Converting page to String");
            String S = results.asText();
            Matcher m = p.matcher(S);
            int seen = 0;
            int paginate = 50;
            int total = 2147483647;
            if (m.find()) {
                total = Integer.parseInt(m.group(1));
            }
            HtmlSelect s = (HtmlSelect)results.getElementById("lstPageSize");
            s.getOptionByValue("50").setValueAttribute(new StringBuilder().append(paginate).append("").toString());
            results = setSelectedAttribute(s, new StringBuilder().append(paginate).append("").toString());
            while (seen < total) {
                LOG.info(new StringBuilder().append("seen: ").append(seen).append(" of ").append(total).toString());
                ArrayList searchResults = new ArrayList();
                for (DomElement e : results.getElementsByTagName("table")) {
                    if (e.getAttribute("title").equals("Search results"))
                        searchResults.add(e);
                }
                if (searchResults.isEmpty()) {
                    LOG.error("No search results found");
                    LOG.debug(results.asXml());
                    break;
                }
                else if (searchResults.size() > 1) {
                    LOG.error("Multiple results found");
                    break;
                }
                else if (searchResults.size() == 1) {
                    int counted = save((HtmlTable)searchResults.get(0), council, band, new StringBuilder().append(seen + 1).append("_").append(Math.min(seen + paginate, total)).toString(), seen == 0);
                    seen += counted;
                    if (counted != paginate && seen != total) {
                        LOG.error("Not enough results, only: " + counted);
                        LOG.debug(results.asXml());
                        break;
                    }
                }

                HtmlAnchor nextPage = null;
                for (DomElement e : results.getElementsByTagName("a")) {
                    if ((e.getTextContent().trim().equals("Next page")) && (e.getAttribute("href").equals("Javascript:Next()")))
                    {
                        nextPage = (HtmlAnchor)e;
                        break;
                    }
                }
                if (nextPage == null) break;
                try {
                    results = nextPage.click();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (seen >= total) {
                LOG.info(String.format("COMPLETED: %s %s", council, band));
                return;
            }
            else {
                LOG.info(String.format("FAILED: %s %s Sleeping for 5 seconds", council, band));
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public int save(HtmlTable t, String council, String band, String page, boolean firstPage) {
        int seen = 0;
        LOG.debug(new StringBuilder().append("Table: ").append(council).append("-").append(band).append("-").append(page).toString());
        try {
            StringBuilder fileName = new StringBuilder(saveLocationRoot);
            fileName.append(council);
            fileName.append("-");
            fileName.append(band);
            fileName.append(".txt");
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(fileName.toString(), !firstPage);
                pw = new PrintWriter(fw, true);
                for (HtmlTableBody b : t.getBodies()) {
                    List rows = b.getRows();
                    for (HtmlTableRow r : b.getRows()) {
                        StringBuilder sb = new StringBuilder();
                        boolean first = true;
                        List cells = r.getCells();
                        if (cells.size() == 4) {
                            for (int cell = 0; cell < 4; cell++) {
                                HtmlTableCell c = (HtmlTableCell)cells.get(cell);
                                if (first)
                                    first = false;
                                else
                                    sb.append("~");
                                String text = c.getTextContent().trim();
                                if (cell == 2) {
                                    if (text.length() == 0)
                                        sb.append("YES");
                                    else
                                        sb.append("NO");
                                }
                                else
                                    sb.append(text);
                            }
                            pw.println(sb);
                            seen++;
                        }
                    }
                }
            } finally {
                if (pw != null) {
                    pw.flush();
                    pw.close();
                }
                if (fw != null)
                    fw.close();
            }
        }
        catch (IOException ioe)
        {
            FileWriter fw;
            PrintWriter pw;
            LOG.error(ioe.getMessage(), ioe);
        }
        return seen;
    }

    public void setVoaUri(String voaUri) {
        this.voaUri = voaUri;
    }

    public void setAdvancedSearchFormId(String advancedSearchFormId) {
        this.advancedSearchFormId = advancedSearchFormId;
    }

    public void setLocalAuthoritySelectId(String localAuthoritySelectId) {
        this.localAuthoritySelectId = localAuthoritySelectId;
    }

    public void setCouncilTaxBandsSelectId(String councilTaxBandsSelectId) {
        this.councilTaxBandsSelectId = councilTaxBandsSelectId;
    }

    public void setSaveLocationRoot(String saveLocationRoot) {
        this.saveLocationRoot = saveLocationRoot;
    }
}
