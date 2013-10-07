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
import java.util.Properties;
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

    private final Pattern showingPattern = Pattern.compile("Showing [0-9]* - [0-9]* of ([0-9]*)");

    private WebClient webClient;
    private HtmlForm dwellingSearchForm;
    private HtmlSelect authoritiesSelect;
    private HtmlButton searchButton;
    private HtmlSelect councilTaxBandSelect;
    private TreeMap<String, String> localAuthorityCodes;
    private ArrayList<String> bands;

    private String advancedSearchFormId;
    private String localAuthoritySelectId;
    private String councilTaxBandsSelectId;
    private String paginationSelectId;
    private String saveLocationRoot;
    private String voaUri;
    private int paginate;
    private HtmlPage resultsHtmlPage;
    private int seen;
    private int total;
    private String council;
    private String band;

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
        return s.setSelectedAttribute(attribute, true);
    }

    public HtmlPage click(HtmlButton button)
    {
        try {
            return button.click();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void init()
    {
        initiateWebClient();
        findDwellingSearchForm();
        loadAuthorityCodes();
        loadCouncilTaxBands();
        findSearchButton();
    }

    private void initiateWebClient() {
        this.webClient = new WebClient(BrowserVersion.FIREFOX_17);
        this.webClient.setThrowExceptionOnScriptError(false);
        this.webClient.setThrowExceptionOnFailingStatusCode(false);
    }

    private void loadCouncilTaxBands() {
        councilTaxBandSelect = dwellingSearchForm.getElementById(councilTaxBandsSelectId);
        this.bands = new ArrayList();
        for (HtmlOption o : councilTaxBandSelect.getOptions())
            this.bands.add(o.getText());
        this.bands.remove(0);
    }

    private void loadAuthorityCodes() {
        authoritiesSelect = dwellingSearchForm.getElementById(localAuthoritySelectId);
        localAuthorityCodes = new TreeMap();
        for (HtmlOption o : this.authoritiesSelect.getOptions()) {
            this.localAuthorityCodes.put(o.getText().toUpperCase(), o.getAttribute("value"));
        }
        this.localAuthorityCodes.remove("");
    }

    private void findDwellingSearchForm() {
        HtmlPage page = getPage(voaUri);
        List<HtmlForm> forms = page.getForms();
        dwellingSearchForm = null;
        for (HtmlForm f : forms)
            if (forms.get(0).getId().equals(advancedSearchFormId)) {
                dwellingSearchForm = f;
                break;
            }
        if (dwellingSearchForm == null) {
            throw new RuntimeException(new StringBuilder().append("Form with id \"").append(advancedSearchFormId).append("\" can not be found").toString());
        }
    }

    private void findSearchButton() {
        for (HtmlElement e : dwellingSearchForm.getElementsByTagName("button"))
            if ((e.getAttribute("value").equals("Search")) && (e.getAttribute("type").equals("submit")))
                searchButton = ((HtmlButton)e);
    }

    public void download(String council, String band) {
        this.council = council;
        this.band = band;
        selectTargetCouncilAndBand(council, band);
        loadPageOfDesiredSize();
        findTotal();
        seen = 0;
        while (seen < total) {
            loadNextPage();
        }
    }

    private boolean loadNextPage() {
        LOG.info("{} - {} loaded: {} of {}", new Object[]{council, band, seen, total});
        ArrayList searchResults = getResultsTables();
        else if (searchResults.size() == 1) {
            int counted = save((HtmlTable)searchResults.get(0), council, band, new StringBuilder().append(seen + 1).append("_").append(Math.min(seen + paginate, total)).toString(), seen == 0);
            seen += counted;
            if (counted != paginate && seen != total) {
                LOG.error("Not enough resultsHtmlPage, only: " + counted);
                LOG.debug(resultsHtmlPage.asXml());
                return true;
            }
        }

        HtmlAnchor nextPage = null;
        for (DomElement e : resultsHtmlPage.getElementsByTagName("a")) {
            if ((e.getTextContent().trim().equals("Next page")) && (e.getAttribute("href").equals("Javascript:Next()")))
            {
                nextPage = (HtmlAnchor)e;
                break;
            }
        }
        if (nextPage == null) return true;
        try {
            resultsHtmlPage = nextPage.click();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private ArrayList<HtmlTable> getResultsTables() {
        ArrayList<HtmlTable> resultsTables = new ArrayList<HtmlTable>();
        for (DomElement e : resultsHtmlPage.getElementsByTagName("table")) {
            if (e.getAttribute("title").equals("Search resultsHtmlPage"))
                resultsTables.add((HtmlTable)e);
        }
        if (searchResults.isEmpty()) {
            LOG.error("No search resultsHtmlPage found");
            LOG.debug(resultsHtmlPage.asXml());
        }
        else if (searchResults.size() > 1) {
            LOG.error("Multiple resultsHtmlPage found");
            return true;
        }

    }

    private void loadPageOfDesiredSize() {
        resultsHtmlPage = click(searchButton);
        HtmlSelect s = (HtmlSelect)resultsHtmlPage.getElementById(paginationSelectId);
        resultsHtmlPage = setSelectedAttribute(s, paginate + "");
    }

    private void findTotal() {
        Matcher showingMatcher = showingPattern.matcher(resultsHtmlPage.asText());
        if (showingMatcher.find()) {
            total = Integer.parseInt(showingMatcher.group(1));
        }
        throw new IllegalArgumentException("Provided page does not contain a match for the Showing regex");
    }

    private void selectTargetCouncilAndBand(String council, String band) {
        String value = this.localAuthorityCodes.get(council.toUpperCase());
        if (!bands.contains(band)) {
            throw new IllegalArgumentException(String.format("Unknown band: %s", band));
        }
        if (value == null) {
            throw new IllegalArgumentException(String.format("Unknown council: %s", council));
        }
        authoritiesSelect.setSelectedAttribute(value, true);
        councilTaxBandSelect.setSelectedAttribute(band, true);
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
        this.saveLocationRoot = saveLocationRoot.replaceAll("^\\~", System.getProperty("user.home"));
    }

    public void setPaginate(int paginate) {
        this.paginate = paginate;
    }

    public void setPaginationSelectId(String paginationSelectId) {
        this.paginationSelectId = paginationSelectId;
    }
}
