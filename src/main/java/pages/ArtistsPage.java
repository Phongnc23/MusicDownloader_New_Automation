package pages;

import base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import utils.GestureUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object cho module ARTISTS cua app Music Downloader (Flutter).
 *
 * Bao gom 3 man + 2 sheet + select mode:
 *  - Artists LIST: header (drawer/search), title "Artists", count "N artists",
 *    nut sort (Title-only), artist card content-desc "<name>\nN tracks" + 3 cham con.
 *  - Artist DETAIL: Back + 3 cham, hero "<name>\nN songs", section "Albums" (carousel folder),
 *    Play all/Shuffle, section "Tracks" + sort, danh sach track (giong man Tracks).
 *  - Folder/Album DETAIL (bam folder trong carousel): Back + 3 cham, hero "FolderName\nN songs",
 *    Play all/Shuffle, "Tracks" + sort, danh sach track. KHONG co section Albums.
 *  - Edit sheet 4 action (artist & folder): Play / Add to playing queue / Add to playlist / Share track
 *    (KHONG co Rename/File info/Delete).  [Mo bang tap 3 cham]
 *  - Select mode (NHAN GIU artist): title "N item selected", X dong, chon-tat-ca,
 *    bottom bar Add to queue / Add to list / Share file.
 *  - Sort dialog (list): "Sort by" + chi 1 option "Title".
 *
 * Menu 3 cham tren TUNG track (trong detail/folder) = sheet 7 action GIONG man Tracks
 * -> dung lai locator cua TracksPage de assert.
 *
 * Mini player + bottom nav dung lai HomePage.
 */
public class ArtistsPage extends BasePage {

    // ===================== LOCATORS =====================
    private final By artistsTitle = AppiumBy.accessibilityId("Artists");
    // Count "N artists". descriptionMatches khong on dinh tren Flutter app nay -> dung
    // descriptionContains("artists") (case-insensitive, bat ca tab "Artists" lan "N artists")
    // roi loc regex Java o getArtistsCount (chi nhan element co dang "<so> artists").
    private final By artistsCountCandidates = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"artists\")");
    private static final Pattern ARTISTS_COUNT_PATTERN = Pattern.compile("(\\d+)\\s*artists");
    // Artist card tren LIST: content-desc "<name>\nN tracks"
    private final By artistCard = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\" tracks\")");

    // Row (track) trong detail/folder = content-desc chua " • "
    private static final String ROW_MARK = " • ";
    private final By rowItems = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"" + ROW_MARK + "\")");

    // Detail/folder controls
    private final By playAll = AppiumBy.accessibilityId("Play all");
    private final By shuffle = AppiumBy.accessibilityId("Shuffle");
    private final By albumsLabel = AppiumBy.accessibilityId("Albums");
    private final By backBtn = AppiumBy.accessibilityId("Back");
    // Hero / sheet header: content-desc "<name>\nN songs"
    private final By heroSongs = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\" songs\")");

    // Sort dialog (list)
    private final By sortTitle = AppiumBy.accessibilityId("Sort by");
    private final By sortOptTitle = AppiumBy.accessibilityId("Title");
    private final By scrim = AppiumBy.accessibilityId("Scrim");

    // Edit sheet 4 action
    private final By sPlay     = AppiumBy.accessibilityId("Play");
    private final By sAddQueue = AppiumBy.accessibilityId("Add to playing queue");
    private final By sAddList  = AppiumBy.accessibilityId("Add to playlist");
    private final By sShare    = AppiumBy.accessibilityId("Share track");
    private final By sRename   = AppiumBy.accessibilityId("Rename");
    private final By sDelete   = AppiumBy.accessibilityId("Delete from device");
    private final By sFileInfo = AppiumBy.accessibilityId("File information");

    // Add-to-playlist sheet
    private final By addPlaylistTitle = AppiumBy.accessibilityId("Add to playlist");
    private final By createNewPlaylist = AppiumBy.accessibilityId("Create new playlist");

    // Share resolver he thong
    private final By shareSheet = AppiumBy.androidUIAutomator(
            "new UiSelector().packageName(\"com.android.intentresolver\")");

    // ===== SELECT MODE (nhan giu artist) =====
    private final By selectTitle  = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"item selected\")");
    private final By selAddQueue  = AppiumBy.accessibilityId("Add to queue");
    private final By selAddList   = AppiumBy.accessibilityId("Add to list");
    private final By selShareFile = AppiumBy.accessibilityId("Share file");

    // ===================== COORDS (DOM 1720x2408) =====================
    private static final int LIST_SORT_X = 1657, LIST_SORT_Y = 276;       // nut sort tren Artists list
    private static final int ARTIST_CARD_MENU_X = 770, ARTIST_CARD_MENU_Y = 1035; // 3 cham card (1 artist)
    private static final int SORT_CLOSE_X = 1499, SORT_CLOSE_Y = 2171;    // X dong sort dialog [1463,2135][1535,2207]
    private static final int DETAIL_MENU_X = 1666, DETAIL_MENU_Y = 109;   // 3 cham tren detail/folder
    private static final int ARTIST_TRACKS_SORT_X = 1657, ARTIST_TRACKS_SORT_Y = 926; // sort section Tracks (artist detail)
    private static final int FOLDER_TRACKS_SORT_X = 1657, FOLDER_TRACKS_SORT_Y = 597; // sort section Tracks (folder detail)
    private static final int ROW_MENU_X = 1666;                           // 3 cham tren tung track

    // Select mode coords (DOM 1720x2408)
    private static final int SEL_CLOSE_X = 72,   SEL_CLOSE_Y = 127;       // X dong select mode
    private static final int SEL_ALL_X   = 1648, SEL_ALL_Y   = 127;       // chon tat ca (goc phai)
    private static final int SEL_BAR_Y   = 2311;                          // hang ICON bottom bar [.,2275][.,2347] (nhan clickable=false)
    private static final int SEL_QUEUE_X = 302;                           // icon Add to queue [266,2275][338,2347]
    private static final int SEL_LIST_X  = 860;                           // icon Add to list [824,2275][896,2347]
    private static final int SEL_SHARE_X = 1418;                          // icon Share file [1382,2275][1454,2347]

    // ===================== HELPERS =====================
    private String descOf(WebElement el) {
        try {
            // getDomAttribute doi luc tra null tren UiAutomator2 -> fallback getAttribute.
            String d = el.getDomAttribute("content-desc");
            if (d == null || d.isEmpty()) d = el.getAttribute("content-desc");
            if (d == null || d.isEmpty()) d = el.getAttribute("name");
            return d != null ? d : "";
        } catch (Exception e) { return ""; }
    }
    private int parseIntBefore(String s, String unit) {
        if (s == null) return -1;
        Matcher m = Pattern.compile("(\\d+)\\s*" + unit).matcher(s);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }
    private List<WebElement> rows() { return driver.findElements(rowItems); }
    private int rowCenterY(int index) {
        Rectangle r = rows().get(index).getRect();
        return r.getY() + r.getHeight() / 2;
    }
    private void pressBack() {
        try { driver.pressKey(new KeyEvent(AndroidKey.BACK)); } catch (Exception ignored) {}
    }
    private String nameBeforeNewline(String d) {
        int nl = d.indexOf('\n');
        return nl >= 0 ? d.substring(0, nl).trim() : d.trim();
    }
    /** Long-press toa do (x,y) trong ms mili-giay (W3C PointerInput). */
    private void longPress(int x, int y, long ms) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(finger.createPointerMove(Duration.ofMillis(ms), PointerInput.Origin.viewport(), x, y));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(seq));
    }

    // ===================== ARTISTS LIST =====================
    public boolean isArtistsScreenDisplayed() {
        return getArtistsCount() >= 1 && existsImmediately(artistCard);
    }
    public int getArtistsCount() {
        for (WebElement el : driver.findElements(artistsCountCandidates)) {
            Matcher m = ARTISTS_COUNT_PATTERN.matcher(descOf(el));
            if (m.find()) return Integer.parseInt(m.group(1));
        }
        return -1;
    }
    public int getArtistCardCount() { return driver.findElements(artistCard).size(); }
    public String getFirstArtistName() {
        List<WebElement> c = driver.findElements(artistCard);
        return c.isEmpty() ? "" : nameBeforeNewline(descOf(c.get(0)));
    }
    public int getFirstArtistTrackCount() {
        List<WebElement> c = driver.findElements(artistCard);
        return c.isEmpty() ? -1 : parseIntBefore(descOf(c.get(0)), "tracks");
    }
    public void tapArtistCard(int index) {
        driver.findElements(artistCard).get(index).click();
        log.info("Mo Artist Detail (card index {})", index);
    }
    public void openArtistMenuFromList() {
        GestureUtils.tap(driver, ARTIST_CARD_MENU_X, ARTIST_CARD_MENU_Y);
        log.info("Tap 3 cham artist card");
    }
    public void tapListSort() { GestureUtils.tap(driver, LIST_SORT_X, LIST_SORT_Y); log.info("Tap sort artist list"); }

    // ===================== SORT DIALOG (LIST) =====================
    public boolean isSortDialogOpen() { return existsImmediately(sortTitle); }
    public boolean isSortTitleOptionDisplayed() { return existsImmediately(sortOptTitle); }
    public boolean isSortTitleActive() {
        return existsImmediately(AppiumBy.xpath("//*[@content-desc='Title']/*"));
    }
    public void tapSortTitle() { click(sortOptTitle); log.info("Chon sort Title"); }
    public void closeSortViaX() { GestureUtils.tap(driver, SORT_CLOSE_X, SORT_CLOSE_Y); }
    public void closeSortViaScrim() { if (existsImmediately(scrim)) click(scrim); }
    public void closeSortViaBack() { pressBack(); }

    // ===================== EDIT SHEET 4 ACTION (artist & folder) =====================
    public boolean isFourActionSheetOpen() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue)
                && existsImmediately(sAddList) && existsImmediately(sShare)
                && !existsImmediately(sRename) && !existsImmediately(sDelete);
    }
    public boolean areFourActionsDisplayed() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue)
                && existsImmediately(sAddList) && existsImmediately(sShare);
    }
    public boolean sheetHasNoRenameDelete() {
        return !existsImmediately(sRename) && !existsImmediately(sDelete);
    }
    /** Sheet artist/folder KHONG duoc co Rename/Delete/File information (chi 4 action). */
    public boolean sheetHasNoExtraActions() {
        return !existsImmediately(sRename) && !existsImmediately(sDelete) && !existsImmediately(sFileInfo);
    }
    public boolean sheetShowsName(String name) {
        return existsImmediately(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"" + name + "\")"));
    }
    /** So bai doc tu header sheet/hero "Name\nN songs". -1 neu loi. */
    public int getSheetSongCount() { return parseIntBefore(getContentDesc(heroSongs), "songs"); }
    public void tapSheetPlay()     { click(sPlay); }
    public void tapSheetAddQueue() { click(sAddQueue); }
    public void tapSheetAddList()  { click(sAddList); }
    public void tapSheetShare()    { click(sShare); }
    public void closeSheetViaScrim() { if (existsImmediately(scrim)) click(scrim); }
    public void closeSheetViaBack()  { pressBack(); }

    // ===================== DETAIL (artist & folder) =====================
    public boolean isDetailWithTracksOpen() {
        return existsImmediately(playAll) && existsImmediately(shuffle);
    }
    public boolean isArtistDetailOpen() {
        return existsImmediately(albumsLabel) && existsImmediately(playAll);
    }
    public boolean isFolderDetailOpen(String name) {
        return existsImmediately(playAll) && !existsImmediately(albumsLabel) && sheetShowsName(name);
    }
    public String getDetailHeroName() { return nameBeforeNewline(getContentDesc(heroSongs)); }
    public int getDetailHeroSongCount() { return parseIntBefore(getContentDesc(heroSongs), "songs"); }
    public void tapDetailBack()  { if (existsImmediately(backBtn)) click(backBtn); else pressBack(); }
    public void tapDetailMenu()  { GestureUtils.tap(driver, DETAIL_MENU_X, DETAIL_MENU_Y); }
    public void tapDetailPlayAll() { click(playAll); }
    public void tapDetailShuffle() { click(shuffle); }
    public int getDetailRowCount() { return rows().size(); }
    public String getFirstDetailTrackTitle() {
        List<WebElement> r = rows();
        return r.isEmpty() ? "" : nameBeforeNewline(descOf(r.get(0)));
    }
    public boolean firstDetailRowHasTitleAndDuration() {
        List<WebElement> r = rows();
        if (r.isEmpty()) return false;
        String d = descOf(r.get(0));
        return d.contains("\n") && d.contains(ROW_MARK);
    }
    public void playDetailTrack(int index) { rows().get(index).click(); }
    /** Menu 7-action cua track DANG mo: co Share track + Delete from device. */
    public boolean isTrackMenuOpen() {
        return existsImmediately(sShare) && existsImmediately(sDelete);
    }
    public void openDetailTrackMenu(int index) {
        int cy = rowCenterY(index);
        // Tap 3 cham track + VERIFY menu mo (toi 3 lan). Truoc day tap 1 lan: neu truot (row dich do
        // churn) -> menu khong mo -> tapSheetShare bam trong -> waitShareSheetOpen fail -> retry.
        for (int i = 0; i < 3; i++) {
            GestureUtils.tap(driver, ROW_MENU_X, cy);
            log.info("Mo menu 3 cham track index {} (y={}) - tap {}", index, cy, i + 1);
            if (waitUntil(this::isTrackMenuOpen, 1500)) return;
        }
        log.info("Menu track index {} CHUA mo sau 3 tap", index);
    }

    // ===================== ALBUMS CAROUSEL (artist detail) =====================
    public boolean isAlbumsSectionDisplayed() { return existsImmediately(albumsLabel); }
    public boolean isFolderListed(String name) {
        return existsImmediately(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"" + name + "\")"));
    }
    public void tapAlbumFolder(String name) {
        click(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + name + "\")"));
        log.info("Mo folder: {}", name);
    }
    /**
     * Tim TEN folder trong carousel "Albums" thoa rule so bai (over10 = >10, else 1..10).
     * Folder card content-desc "<name>\nN songs" (giong hero) -> element " songs" dau tien la HERO
     * (artist), bo qua; con lai la folder. Rong neu khong tim thay. Dung THAY ten cung khi data doi.
     */
    public String findFolderNameByRule(boolean over10) {
        List<WebElement> els = driver.findElements(heroSongs);
        for (int i = 1; i < els.size(); i++) { // i=0 la hero artist
            String d = descOf(els.get(i));
            String nm = nameBeforeNewline(d);
            if (nm.isEmpty()) continue;
            int cnt = parseIntBefore(d, "songs");
            if (over10 && cnt > 10) return nm;
            if (!over10 && cnt >= 1 && cnt <= 10) return nm;
        }
        return "";
    }

    // ===================== DETAIL TRACKS SORT =====================
    public void tapArtistDetailTracksSort() { GestureUtils.tap(driver, ARTIST_TRACKS_SORT_X, ARTIST_TRACKS_SORT_Y); }
    public void tapFolderTracksSort()       { GestureUtils.tap(driver, FOLDER_TRACKS_SORT_X, FOLDER_TRACKS_SORT_Y); }

    // ===================== ADD TO PLAYLIST =====================
    public boolean isAddToPlaylistOpen() {
        return existsImmediately(addPlaylistTitle) && existsImmediately(createNewPlaylist);
    }
    public void tapPlaylistByName(String name) {
        click(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + name + "\")"));
    }

    // ===================== SHARE SHEET =====================
    public boolean isShareSheetOpen() { return existsImmediately(shareSheet); }
    public boolean waitShareSheetOpen(long ms) {
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end) {
            if (existsImmediately(shareSheet)) return true;
            sleep(200);
        }
        return false;
    }
    /**
     * Dong share resolver. OPPO intentresolver: BACK doi luc KHONG dong -> uu tien tap nut "Huy"
     * (resourceId oplus_resolve_close_icon), fallback BACK. VERIFY da dong (lap toi 4 lan) de tranh
     * ro ri resolver sang test sau.
     */
    public void closeShareSheet() {
        By cancel = AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"com.android.intentresolver:id/oplus_resolve_close_icon\")");
        for (int i = 0; i < 4 && isShareSheetOpen(); i++) {
            if (existsImmediately(cancel)) { click(cancel); log.info("Tap Huy dong share resolver"); }
            else { pressBack(); log.info("BACK dong share resolver (khong thay nut Huy)"); }
            sleep(700);
        }
    }

    // ===================== SELECT MODE (nhan giu artist) =====================
    /** Nhan giu artist dau tien -> mo man select mode. */
    public void longPressFirstArtist() {
        List<WebElement> c = driver.findElements(artistCard);
        Rectangle r = c.get(0).getRect();
        longPress(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2, 900);
        log.info("Long-press artist card -> select mode");
    }
    public boolean isSelectModeOpen() { return existsImmediately(selectTitle); }
    /** So item dang chon, doc tu title "N item selected". -1 neu loi. */
    public int getSelectedCount() { return parseIntBefore(getContentDesc(selectTitle), "item selected"); }
    /** Tap artist trong select mode de toggle chon. */
    public void tapArtistInSelectMode(int index) { driver.findElements(artistCard).get(index).click(); }
    public void tapSelectAll() { GestureUtils.tap(driver, SEL_ALL_X, SEL_ALL_Y); }
    public boolean isSelectModeActionsDisplayed() {
        return existsImmediately(selAddQueue) && existsImmediately(selAddList) && existsImmediately(selShareFile);
    }
    public void tapSelectAddQueue()  { GestureUtils.tap(driver, SEL_QUEUE_X, SEL_BAR_Y); }
    public void tapSelectAddList()   { GestureUtils.tap(driver, SEL_LIST_X, SEL_BAR_Y); }
    public void tapSelectShareFile() { GestureUtils.tap(driver, SEL_SHARE_X, SEL_BAR_Y); }
    public void closeSelectMode()    { GestureUtils.tap(driver, SEL_CLOSE_X, SEL_CLOSE_Y); }
}