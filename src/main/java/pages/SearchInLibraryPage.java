package pages;

import base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object cho man SEARCH IN LIBRARY (mo bang search icon goc phai tren Home).
 *
 * Cau truc (DOM 1720x2408):
 *  - Header: Back Button [0,82][108,190]; EditText (hint "Search in library") [144,114][1558,159];
 *    nut Clear/X [1558,91][1666,181] (hien khi co text).
 *  - 5 tab loc: All / Tracks / Albums / Artists / Playlists (content-desc), container [336,226][1384,294].
 *  - Vung ket qua [0,330][1720,2408]:
 *      + Tab "All": co section header ("Tracks", ...) + cac row.
 *      + Tab cu the: chi cac row cua loai do, khong section header.
 *      + Track row: content-desc "Title\n<unknown> • m:ss" (co " • "), long-clickable, clickable -> phat/roi search.
 *      + Album/Artist/Playlist row: content-desc "<name>\nN tracks" (KHONG co " • "), clickable -> mo Detail.
 *  - Rong (khong nhap): khong hien row, khong "Nothing found".
 *  - 1 dau cach: hien toan bo (search theo substring).
 *  - Khong khop: "Nothing found!" + "No results found in the library.".
 *
 * Luu y: trang thai tab dang chon (xanh) KHONG doc duoc qua accessibility (selected=false).
 * -> verify loc bang HIEU UNG (doi tab -> tap ket qua thay doi loai).
 */
public class SearchInLibraryPage extends BasePage {

    // ===================== LOCATORS =====================
    private final By searchField = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");
    private final By tabAll      = AppiumBy.accessibilityId("All");
    private final By tabTracks   = AppiumBy.accessibilityId("Tracks");
    private final By tabAlbums   = AppiumBy.accessibilityId("Albums");
    private final By tabArtists  = AppiumBy.accessibilityId("Artists");
    private final By tabPlaylists = AppiumBy.accessibilityId("Playlists");

    private static final String DUR_MARK = " • ";
    private final By trackRows = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"" + DUR_MARK + "\")");
    private final By collectionRows = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\" tracks\")");

    private final By nothingFound = AppiumBy.accessibilityId("Nothing found!");
    private final By noResultsMsg = AppiumBy.accessibilityId("No results found in the library.");

    // ===================== COORDS (DOM 1720x2408) =====================
    private static final int BACK_X = 54,  BACK_Y = 136;     // Back button
    private static final int CLEAR_X = 1612, CLEAR_Y = 136;  // nut X xoa query
    private static final int TAB_Y = 260;
    private static final int TAB_ALL_X = 462, TAB_TRACKS_X = 660, TAB_ALBUMS_X = 858,
            TAB_ARTISTS_X = 1056, TAB_PLAYLISTS_X = 1256;

    // ===================== HELPERS =====================
    private String descOf(WebElement el) {
        try {
            String d = el.getDomAttribute("content-desc");
            if (d == null || d.isEmpty()) d = el.getAttribute("content-desc");
            if (d == null || d.isEmpty()) d = el.getAttribute("name");
            return d != null ? d : "";
        } catch (Exception e) { return ""; }
    }
    private String nameBeforeNewline(String d) {
        int nl = d.indexOf('\n');
        return nl >= 0 ? d.substring(0, nl).trim() : d.trim();
    }
    private By byDescContains(String s) {
        return AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + s + "\")");
    }
    private void pressBack() {
        try { driver.pressKey(new KeyEvent(AndroidKey.BACK)); } catch (Exception ignored) {}
    }

    // ===================== SCREEN / HEADER =====================
    /**
     * Mo man Search In Library CHAC CHAN. Man nay co tab loc "Tracks" (clickable) trung voi cach
     * BaseTest.waitAppReady nhan biet bottom nav -> app co the ket lai o man search/detail tu test
     * truoc. BACK ve man co Home search icon (Home/tab) roi tap icon mo lai (reset query/tab).
     */
    public SearchInLibraryPage openSearch(HomePage home) {
        // EP VE HOME truoc khi mo search: search icon co the xuat hien tren NHIEU tab, ma nut Back
        // cua man Search tra ve dung man da mo no -> phai mo tu HOME thi Back moi ve Home (TC_SL_003).
        for (int i = 0; i < 6 && !home.isHomeDisplayed(); i++) {
            if (home.isDrawerOpen())            { home.closeMenuDrawer(); sleep(500); }
            else if (home.isBottomNavDisplayed()) { home.tapNavHome();     sleep(700); }
            else                                { home.hideKeyboardSafe(); pressBack(); sleep(600); }
        }
        home.tapSearchIcon();
        sleep(1200);
        return this;
    }
    public boolean isSearchScreenDisplayed() {
        return existsImmediately(searchField) && existsImmediately(tabAll) && existsImmediately(tabAlbums);
    }
    public boolean isSearchFieldDisplayed() { return existsImmediately(searchField); }
    public boolean areFilterTabsDisplayed() {
        return existsImmediately(tabAll) && existsImmediately(tabTracks) && existsImmediately(tabAlbums)
                && existsImmediately(tabArtists) && existsImmediately(tabPlaylists);
    }
    public void typeQuery(String q) { sendKeys(searchField, q); log.info("Nhap query: '{}'", q); }
    public String getQueryText() {
        try { String t = getText(searchField); return t == null ? "" : t; } catch (Exception e) { return ""; }
    }
    public boolean isQueryEmpty() { return getQueryText().trim().isEmpty(); }
    public void clearQueryViaX() { GestureUtils.tap(driver, CLEAR_X, CLEAR_Y); log.info("Tap X xoa query"); }
    public void clearQueryViaField() {
        try { driver.findElement(searchField).clear(); } catch (Exception ignored) {}
    }
    public void tapBack() { GestureUtils.tap(driver, BACK_X, BACK_Y); log.info("Tap Back tren Search"); }

    // ===================== TABS =====================
    public void tapTabAll()       { GestureUtils.tap(driver, TAB_ALL_X, TAB_Y); }
    public void tapTabTracks()    { GestureUtils.tap(driver, TAB_TRACKS_X, TAB_Y); }
    public void tapTabAlbums()    { GestureUtils.tap(driver, TAB_ALBUMS_X, TAB_Y); }
    public void tapTabArtists()   { GestureUtils.tap(driver, TAB_ARTISTS_X, TAB_Y); }
    public void tapTabPlaylists() { GestureUtils.tap(driver, TAB_PLAYLISTS_X, TAB_Y); }

    // ===================== RESULTS =====================
    public boolean hasTrackResults() { return existsImmediately(trackRows); }
    public boolean hasCollectionResults() { return existsImmediately(collectionRows); }
    public boolean hasAnyResult() { return hasTrackResults() || hasCollectionResults(); }
    public int getTrackResultCount() { return driver.findElements(trackRows).size(); }
    public int getCollectionResultCount() { return driver.findElements(collectionRows).size(); }
    public boolean isNothingFound() { return existsImmediately(nothingFound); }
    public boolean isNoResultsMsgShown() { return existsImmediately(noResultsMsg); }
    /** Rong: khong row + khong "Nothing found". */
    public boolean isResultAreaEmpty() { return !hasAnyResult() && !isNothingFound(); }
    public boolean resultContains(String name) { return existsImmediately(byDescContains(name)); }
    public String getFirstTrackResultTitle() {
        List<WebElement> r = driver.findElements(trackRows);
        return r.isEmpty() ? "" : nameBeforeNewline(descOf(r.get(0)));
    }
    public void tapFirstTrackResult() {
        List<WebElement> r = driver.findElements(trackRows);
        if (!r.isEmpty()) r.get(0).click();
    }
    public void tapResultByName(String name) { click(byDescContains(name)); log.info("Tap ket qua: {}", name); }
}