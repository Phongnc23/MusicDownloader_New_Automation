package pages;

import base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object cho man SEARCH ONLINE (Download) cua app Music Downloader (Flutter).
 *
 * Mo tu Home bang home.tapSearchBar() (thanh "Search music online..."). Man co 2 trang thai
 * danh sach (cung la ImageView long-clickable, phan biet bang content-desc):
 *
 *  1) GOI Y (autocomplete) - hien khi DANG GO tu khoa:
 *     - Row: content-desc = chu goi y THUAN (vd "xao xuyen", "xanh navy") -> KHONG co " • ".
 *     - Moi row co icon "dien vao o" (↖) la ImageView con ben phai (x 1594..1702, tam 1648).
 *     - Tap CHU goi y -> trigger search ra ket qua. Tap icon ↖ -> chi dien o tim, khong search.
 *
 *  2) KET QUA (sau khi submit / chon goi y):
 *     - Row: content-desc = "Title\nChannel • duration" -> CO " • ".
 *     - Moi row co nut download la ImageView con ben phai (x 1612..1720, tam 1666).
 *
 * Toa do suy tu DOM 1720x2408.
 */
public class SearchOnlinePage extends BasePage {

    // O nhap: chi co 1 EditText tren man Search
    private final By searchInput = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");

    // Row (goi y HOAC ket qua) = ImageView long-clickable. Icon con (push/download) KHONG long-clickable.
    private final By rowItems = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.ImageView\").longClickable(true)");

    // Dem so bai o man thu vien (Tracks/Downloaded) - dung cho E2E.
    // Man Tracks co CA "Tracks" (tab/header) lan "368 tracks"; descriptionContains("tracks")
    // cua UiAutomator2 CASE-INSENSITIVE -> match ca "Tracks" (khong so). Regex trong selector
    // (descriptionMatches) bi vuong escaping backslash. -> lay TAT CA match roi loc bang
    // regex JAVA o getLibraryTrackCount (chi nhan element dang "<so> tracks").
    private final By libraryCountCandidates = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"tracks\")");
    private static final Pattern TRACK_COUNT_PATTERN = Pattern.compile("(\\d+)\\s*tracks");

    // Marker Home de xac nhan da roi/ve Home
    private final By homeSearchBar = AppiumBy.accessibilityId("Search music online...");

    // x-center cac icon ben phai row
    private static final int SUGGEST_PUSH_CX = 1648; // icon ↖ dien o tim (1594..1702)
    private static final int DOWNLOAD_BTN_CX = 1666; // nut download (1612..1720)

    // Dau hieu phan biet KET QUA voi GOI Y
    private static final String RESULT_MARK = " • ";

    // =========================================================
    //  Trang thai man
    // =========================================================
    /** Dang o man Search (co o nhap EditText, khong con thanh search Home). */
    public boolean isSearchScreenDisplayed() {
        return existsImmediately(searchInput) && !existsImmediately(homeSearchBar);
    }

    public boolean isKeyboardShown() {
        try {
            return driver.isKeyboardShown();
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================
    //  Nhap & tim kiem
    // =========================================================
    public void typeQuery(String query) {
        WebElement in = findElement(searchInput);
        in.click();
        in.clear();
        in.sendKeys(query);
        log.info("Nhap query: '{}'", query);
    }

    public void clearInput() {
        try {
            findElement(searchInput).clear();
            log.info("Da clear o tim kiem");
        } catch (Exception e) {
            log.warn("Clear o tim kiem loi: {}", e.getMessage());
        }
    }

    public String getSearchInputText() {
        try {
            String t = findElement(searchInput).getText();
            return t != null ? t : "";
        } catch (Exception e) {
            return "";
        }
    }

    /** Trigger search bang phim ENTER. */
    public void submitSearch() {
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
        log.info("Submit search (ENTER)");
    }

    /**
     * Go query + trigger search + cho ket qua. Uu tien ENTER; neu ENTER khong ra ket qua
     * thi tap goi y dau de trigger (mot so build chi search khi chon goi y).
     */
    public void searchFor(String query) {
        typeQuery(query);
        submitSearch();
        if (waitForResults(5000)) return;
        if (getSuggestionCount() > 0) {
            tapSuggestion(0);
            waitForResults(5000);
        }
    }

    public boolean waitForResults(long ms) {
        return waitCount(true, ms);
    }

    public boolean waitForSuggestions(long ms) {
        return waitCount(false, ms);
    }

    private boolean waitCount(boolean wantResults, long ms) {
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end) {
            int n = wantResults ? getResultCount() : getSuggestionCount();
            if (n > 0) return true;
            sleep(300);
        }
        return false;
    }

    // =========================================================
    //  Phan loai row: goi y vs ket qua (theo content-desc)
    // =========================================================
    private String descOf(WebElement el) {
        try {
            String d = el.getDomAttribute("content-desc");
            if (d == null || d.isEmpty()) d = el.getAttribute("content-desc");
            if (d == null || d.isEmpty()) d = el.getAttribute("name");
            return d != null ? d : "";
        } catch (Exception e) {
            return "";
        }
    }

    /** Row GOI Y = long-clickable ImageView, content-desc khong rong va KHONG chua " • ". */
    private List<WebElement> suggestionElements() {
        List<WebElement> out = new ArrayList<>();
        for (WebElement el : driver.findElements(rowItems)) {
            String d = descOf(el);
            if (!d.isEmpty() && !d.contains(RESULT_MARK)) out.add(el);
        }
        return out;
    }

    /** Row KET QUA = long-clickable ImageView, content-desc chua " • ". */
    private List<WebElement> resultElements() {
        List<WebElement> out = new ArrayList<>();
        for (WebElement el : driver.findElements(rowItems)) {
            if (descOf(el).contains(RESULT_MARK)) out.add(el);
        }
        return out;
    }

    // =========================================================
    //  GOI Y
    // =========================================================
    public int getSuggestionCount() {
        return suggestionElements().size();
    }

    public boolean isSuggestionsDisplayed() {
        return getSuggestionCount() > 0;
    }

    public String getSuggestionText(int index) {
        List<WebElement> s = suggestionElements();
        return index < s.size() ? descOf(s.get(index)).trim() : "";
    }

    /** Tap CHU goi y (vung text, tam row) -> trigger search. */
    public void tapSuggestion(int index) {
        List<WebElement> s = suggestionElements();
        if (index >= s.size()) {
            log.warn("Khong co goi y index {}", index);
            return;
        }
        s.get(index).click(); // tam row ~ x860, xa icon ↖ (x1648) -> trigger search
        log.info("Tap chu goi y index {}", index);
    }

    /** Tap icon ↖ (ben phai) cua goi y -> chi dien o tim, KHONG search. */
    public void tapSuggestionPushIcon(int index) {
        List<WebElement> s = suggestionElements();
        if (index >= s.size()) {
            log.warn("Khong co goi y index {} de tap icon push", index);
            return;
        }
        Rectangle r = s.get(index).getRect();
        int cy = r.getY() + r.getHeight() / 2;
        GestureUtils.tap(driver, SUGGEST_PUSH_CX, cy);
        log.info("Tap icon push goi y index {} (y={})", index, cy);
    }

    // =========================================================
    //  KET QUA
    // =========================================================
    public int getResultCount() {
        return resultElements().size();
    }

    public boolean isResultsDisplayed() {
        return getResultCount() > 0;
    }

    /** Tieu de (dong dau content-desc) cua ket qua thu index. */
    public String getResultTitle(int index) {
        List<WebElement> r = resultElements();
        if (index >= r.size()) return "";
        String d = descOf(r.get(index));
        int nl = d.indexOf('\n');
        return nl >= 0 ? d.substring(0, nl).trim() : d.trim();
    }

    /** Ket qua dau co du title + thong tin (content-desc chua " • "). */
    public boolean firstResultHasInfo() {
        List<WebElement> r = resultElements();
        return !r.isEmpty() && descOf(r.get(0)).contains(RESULT_MARK);
    }

    /** Tap nut download (ben phai) cua ket qua thu index. */
    public void tapDownloadOnResult(int index) {
        List<WebElement> r = resultElements();
        if (index >= r.size()) {
            log.warn("Khong co ket qua index {} de download", index);
            return;
        }
        Rectangle rect = r.get(index).getRect();
        int cy = rect.getY() + rect.getHeight() / 2;
        GestureUtils.tap(driver, DOWNLOAD_BTN_CX, cy);
        log.info("Tap download ket qua index {} (y={})", index, cy);
    }

    // =========================================================
    //  Thu vien (dung khi dang o man Tracks/Downloaded) - cho E2E
    // =========================================================
    /** So bai trong thu vien doc tu "N tracks". -1 neu khong doc duoc. */
    public int getLibraryTrackCount() {
        for (WebElement el : driver.findElements(libraryCountCandidates)) {
            Matcher m = TRACK_COUNT_PATTERN.matcher(descOf(el));
            if (m.find()) return Integer.parseInt(m.group(1));
        }
        return -1;
    }
}