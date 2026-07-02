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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object cho man TRACKS (thu vien bai hat da tai) cua app Music Downloader (Flutter).
 *
 * Vao tu bottom nav: home.tapNavTracks().
 *
 * Cau truc chinh (DOM 1720x2408):
 *  - Header: drawer icon trai, title "Tracks", search icon phai [1594,64][1702,154],
 *    nut SORT (Button khong content-desc) [1612,154][1702,244].
 *  - "Play all" [36,244][847,334], "Shuffle" [874,244][1684,334] (content-desc).
 *  - List: moi row = ImageView (o select mode la View) long-clickable, content-desc
 *    "Title\n<unknown> • m:ss" (CO " • "); nut 3 cham = Button con ben phai x≈1666.
 *  - Mini player o day; bottom nav.
 *
 * Cac man phu (mo tu row/menu): Sort dialog, Track context menu (7 action),
 * Add-to-playlist sheet, Create-new-playlist dialog, Rename dialog, Track information,
 * Delete confirm (in-app) + dialog quyen he thong, Select mode, Playing Queue, Play Now.
 *
 * Luu y: cac man phu dung android.view.View + content-desc (Flutter), locate bang accessibilityId.
 * Mot so nut khong co content-desc -> tap bang TOA DO (suy tu DOM 1720x2408).
 */
public class TracksPage extends BasePage {

    // ===================== LOCATORS =====================
    private final By headerTitle = AppiumBy.accessibilityId("Tracks");
    private final By playAllBtn  = AppiumBy.accessibilityId("Play all");
    private final By shuffleBtn  = AppiumBy.accessibilityId("Shuffle");

    // Row (track) = element co content-desc chua " • " (dung cho ca Tracks list va Playing Queue)
    private static final String ROW_MARK = " • ";
    private final By rowItems = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"" + ROW_MARK + "\")");

    // Header count "N tracks". descriptionMatches doi full-match + case-sensitive -> hay truot.
    // Dung descriptionContains("tracks") (CASE-INSENSITIVE) lay TAT CA roi loc regex Java
    // (\\d+)\\s*tracks o getTrackCount (giong cach lam o SearchOnlinePage).
    private final By trackCountCandidates = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"tracks\")");
    private static final Pattern TRACK_COUNT_PATTERN = Pattern.compile("(\\d+)\\s*tracks");

    // EditText (rename / create playlist) - chi co 1 tren cac dialog do
    private final By editText = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");

    private final By scrim = AppiumBy.accessibilityId("Scrim");

    // Sort dialog
    private final By sortTitle = AppiumBy.accessibilityId("Sort by");

    // Context menu actions
    private final By mPlay        = AppiumBy.accessibilityId("Play");
    private final By mAddQueue    = AppiumBy.accessibilityId("Add to playing queue");
    private final By mAddPlaylist = AppiumBy.accessibilityId("Add to playlist");
    private final By mRename      = AppiumBy.accessibilityId("Rename");
    private final By mFileInfo    = AppiumBy.accessibilityId("File information");
    private final By mShareTrack  = AppiumBy.accessibilityId("Share track");
    private final By mDelete      = AppiumBy.accessibilityId("Delete from device");

    // Add-to-playlist sheet
    private final By addPlaylistTitle = AppiumBy.accessibilityId("Add to playlist");
    private final By createNewPlaylist = AppiumBy.accessibilityId("Create new playlist");
    private final By favoriteRow = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"My Favorite\")");

    // Dialog buttons (dung chung cho Rename / Create playlist / Delete)
    private final By btnSave   = AppiumBy.accessibilityId("SAVE");
    private final By btnCancel = AppiumBy.accessibilityId("CANCEL");
    private final By btnDelete = AppiumBy.accessibilityId("DELETE");
    private final By renameTitle = AppiumBy.accessibilityId("Rename");
    // descriptionMatches khong on dinh tren Flutter app nay -> dung descriptionContains.
    private final By renameCounter = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"/60\")");

    // Track information
    private final By infoTitle = AppiumBy.accessibilityId("Track information");

    // Delete confirm (in-app) title
    private final By deleteDialogTitle = AppiumBy.accessibilityId("Delete from device");

    // Dialog quyen xoa cua he thong (com.google.android.providers.media.module)
    private final By sysDeleteTitle = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.google.android.providers.media.module:id/dialog_title\")");
    private final By sysAllowBtn = AppiumBy.id("android:id/button1"); // Cho phep
    private final By sysDenyBtn  = AppiumBy.id("android:id/button2"); // Tu choi

    // Select mode. Nhan dem "N item selected" (descriptionContains - descriptionMatches hay truot).
    private final By selectCountLbl = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"item selected\")");
    private final By selAddQueue = AppiumBy.accessibilityId("Add to queue");
    private final By selAddList  = AppiumBy.accessibilityId("Add to list");
    private final By selShare    = AppiumBy.accessibilityId("Share file");
    private final By selDelete   = AppiumBy.accessibilityId("Delete file");

    // Share sheet he thong
    private final By shareSheet = AppiumBy.androidUIAutomator(
            "new UiSelector().packageName(\"com.android.intentresolver\")");

    // Playing Queue
    private final By queueTitle = AppiumBy.accessibilityId("Playing Queue");
    private final By queueCountLbl = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"tracks(\")");
    private final By queueBack = AppiumBy.accessibilityId("Back");

    // Play Now marker
    private final By playNowMarker = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Playing now\")");

    // ===================== COORDS (DOM 1720x2408) =====================
    private static final int SORT_BTN_X = 1657, SORT_BTN_Y = 199;   // nut sort tren header
    private static final int ROW_MENU_X = 1666;                     // nut 3 cham tren moi row
    private static final int SORT_CLOSE_X = 1499, SORT_CLOSE_Y = 1401; // X dong sort dialog
    private static final int RENAME_CLEAR_X = 1603, RENAME_CLEAR_Y = 982; // X clear o rename
    private static final int SELECT_BACK_X = 72, SELECT_BACK_Y = 127;  // thoat select mode
    private static final int SELECT_ALL_X = 1662, SELECT_ALL_Y = 127;  // chon tat ca
    // Toolbar select mode: NHAN "Delete file"/... la View clickable=false; phan tu bam duoc la
    // ICON ImageView phia tren (y 2275..2347 -> center 2311, KHONG content-desc) -> tap toa do.
    private static final int SEL_ACTION_Y = 2311;
    private static final int SEL_ADDQUEUE_X = 232, SEL_ADDLIST_X = 651, SEL_SHARE_X = 1069, SEL_DELETE_X = 1488;
    // Playing Queue header
    private static final int Q_SHUFFLE_X = 1558, Q_SHUFFLE_Y = 109;
    private static final int Q_REPEAT_X  = 1666, Q_REPEAT_Y  = 109;
    private static final int Q_PLAYPAUSE_X = 1648, Q_PLAYPAUSE_Y = 280;

    // ===== Play Now: TOA DO THAT tu DOM 1720x2408 (da xac nhan). =====
    // Collapse (down) Button [0,73][108,181]; Menu 3 cham Button [1612,73][1720,181].
    private static final int PN_COLLAPSE_X = 54,  PN_COLLAPSE_Y = 127;
    private static final int PN_MENU_X = 1666,    PN_MENU_Y = 127;
    // Hang icon (Button/ImageView) y 1900..2008 -> center 1954.
    private static final int PN_ICON_Y = 1954;
    private static final int PN_HEART_X = 81, PN_ADDLIST_X = 471, PN_EQ_X = 860, PN_SLEEP_X = 1250, PN_QUEUE_X = 1639;
    // SeekBar [32,2062][1698,2134] (content-desc "32%").
    private static final int PN_SEEK_Y = 2098, PN_SEEK_LEFT_X = 32, PN_SEEK_RIGHT_X = 1698;
    // Hang control y 2170..2341 -> center 2255.
    private static final int PN_CTRL_Y = 2255;
    private static final int PN_SHUFFLE_X = 81, PN_PREV_X = 455, PN_PLAYPAUSE_X = 860, PN_NEXT_X = 1265, PN_REPEAT_X = 1639;

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

    private int parseFirstInt(String s) {
        Matcher m = Pattern.compile("(\\d+)").matcher(s == null ? "" : s);
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

    /** Tap giu (long press) tai tam row de vao Select mode. */
    private void longPressRow(int index) {
        Rectangle r = rows().get(index).getRect();
        int cx = r.getX() + r.getWidth() / 2;
        int cy = r.getY() + r.getHeight() / 2;
        Map<String, Object> args = new HashMap<>();
        args.put("x", cx);
        args.put("y", cy);
        args.put("duration", 1200);
        driver.executeScript("mobile: longClickGesture", args);
    }

    // ===================== TRACKS SCREEN =====================
    public boolean isTracksScreenDisplayed() {
        return existsImmediately(playAllBtn) && existsImmediately(shuffleBtn);
    }

    public boolean areTopControlsDisplayed() {
        return existsImmediately(playAllBtn) && existsImmediately(shuffleBtn)
                && existsImmediately(headerTitle);
    }

    /** So bai doc tu header "N tracks". -1 neu khong doc duoc. */
    public int getTrackCount() {
        for (WebElement el : driver.findElements(trackCountCandidates)) {
            Matcher m = TRACK_COUNT_PATTERN.matcher(descOf(el));
            if (m.find()) return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    public int getRowCount() { return rows().size(); }

    public String getFirstTrackTitle() {
        List<WebElement> r = rows();
        if (r.isEmpty()) return "";
        String d = descOf(r.get(0));
        int nl = d.indexOf('\n');
        return nl >= 0 ? d.substring(0, nl).trim() : d.trim();
    }

    public List<String> getVisibleTrackTitles() {
        List<String> out = new ArrayList<>();
        for (WebElement el : rows()) {
            String d = descOf(el);
            int nl = d.indexOf('\n');
            out.add(nl >= 0 ? d.substring(0, nl).trim() : d.trim());
        }
        return out;
    }

    /** Thoi luong (giay) cua row dau, doc tu "... • m:ss". -1 neu loi. */
    public int getFirstTrackDurationSec() {
        List<WebElement> r = rows();
        if (r.isEmpty()) return -1;
        Matcher m = Pattern.compile("(\\d+):(\\d{2})").matcher(descOf(r.get(0)));
        if (m.find()) return Integer.parseInt(m.group(1)) * 60 + Integer.parseInt(m.group(2));
        return -1;
    }

    /** Row dau co title + duration (content-desc co newline va " • "). */
    public boolean firstRowHasTitleAndDuration() {
        List<WebElement> r = rows();
        if (r.isEmpty()) return false;
        String d = descOf(r.get(0));
        return d.contains("\n") && d.contains(ROW_MARK);
    }

    // ===================== TOP CONTROLS / PLAYBACK =====================
    public void tapPlayAll() { click(playAllBtn); log.info("Tap Play all"); }
    public void tapShuffle() { click(shuffleBtn); log.info("Tap Shuffle"); }

    /** Click 1 row theo index, retry chong stale (list churn khi bai ngan auto-nhay). */
    private void clickRow(int index) {
        org.openqa.selenium.WebDriverException last = null;
        for (int i = 0; i < 3; i++) {
            try {
                rows().get(index).click();
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                last = e;
                log.warn("Stale khi click row {} (lan {}), retry", index, i + 1);
            } catch (IndexOutOfBoundsException e) {
                log.warn("Row {} chua san sang (list refresh), retry", index);
                sleep(300);
            }
        }
        if (last != null) throw last;
        rows().get(index).click();
    }

    public void playTrack(int index) {
        clickRow(index);
        log.info("Play track index {}", index);
    }

    public String playTrackAndGetTitle(int index) {
        String title = "";
        List<WebElement> r = rows();
        if (index < r.size()) {
            String d = descOf(r.get(index));
            int nl = d.indexOf('\n');
            title = nl >= 0 ? d.substring(0, nl).trim() : d.trim();
        }
        clickRow(index);
        return title;
    }

    public void openTrackMenu(int index) {
        // Tap nut 3 cham; neu tap truot (trung row -> phat nhac) menu se khong mo -> retry + verify.
        for (int attempt = 1; attempt <= 3; attempt++) {
            int cy = rowCenterY(index);
            GestureUtils.tap(driver, ROW_MENU_X, cy);
            log.info("Mo context menu track index {} (y={}) lan {}", index, cy, attempt);
            if (waitUntil(this::isTrackMenuOpen, 1500)) return;
        }
        log.warn("openTrackMenu: chua mo duoc menu cho row {} sau 3 lan", index);
    }

    public void longPressTrackToSelect(int index) {
        // Long press doi luc khong kich Select mode ngay -> retry toi 3 lan, verify sau moi lan.
        for (int attempt = 1; attempt <= 3; attempt++) {
            longPressRow(index);
            log.info("Long press track index {} -> select mode (lan {})", index, attempt);
            if (waitUntil(this::isSelectModeActive, 1500)) return;
        }
        log.warn("longPressTrackToSelect: chua vao Select mode sau 3 lan");
    }

    public void scrollListDown() { GestureUtils.scrollDown(driver); }

    // ===================== SORT DIALOG =====================
    public void openSortDialog() {
        GestureUtils.tap(driver, SORT_BTN_X, SORT_BTN_Y);
        log.info("Tap nut Sort");
    }

    public boolean isSortDialogOpen() { return existsImmediately(sortTitle); }

    public boolean areAllSortOptionsDisplayed() {
        String[] opts = {"Title", "Artist", "Album", "File name", "Duration", "Date added", "Date modified"};
        for (String o : opts) {
            if (!existsImmediately(AppiumBy.accessibilityId(o))) return false;
        }
        return true;
    }

    public int getSortOptionCount() {
        String[] opts = {"Title", "Artist", "Album", "File name", "Duration", "Date added", "Date modified"};
        int c = 0;
        for (String o : opts) if (existsImmediately(AppiumBy.accessibilityId(o))) c++;
        return c;
    }

    public void tapSortOption(String name) {
        click(AppiumBy.accessibilityId(name));
        log.info("Chon sort: {}", name);
    }

    /**
     * Set sort = "Date modified" de bai DAI (4:59) len dau danh sach. Dung truoc cac test phat nhac:
     * tranh sort Duration (bai 0:03) khien bai tu nhay lien tuc -> mini player chap chon.
     * tapSortOption tu dong ap dung + dong dialog.
     */
    public void setSortDateModifiedTop() {
        ensureSortDialogOpen();
        tapSortOption("Date modified");
        sleep(600);
        if (isSortDialogOpen()) closeSortViaX();
        waitUntil(() -> !isSortDialogOpen(), 2000);
        log.info("Da set sort = Date modified (bai dai len dau)");
    }

    // Mui ten DAO CHIEU asc/desc nam ben TRAI hang option active. Hang "Duration" la option thu 5
    // trong dialog 7 option (cung layout voi Albums in-album sort) -> tam ~ (221, 2051).
    private static final int SORT_DIR_DURATION_X = 221, SORT_DIR_DURATION_Y = 2051;
    /**
     * Set sort = "Duration" GIAM DAN (bai DAI NHAT len dau) -> on dinh nhat cho test Next/Prev:
     * bai dai khong tu auto-next trong luc test, va cac bai dai nhat thuong la ban day du, ten
     * KHAC NHAU (tranh loi "Next khong doi bai" do 2 bai lien tiep TRUNG TEN). Neu sau khi chon
     * Duration bai dau con NGAN (tuc dang tang dan) -> tap mui ten dao chieu.
     */
    public void setSortDurationLongestTop() {
        ensureSortDialogOpen();
        tapSortOption("Duration");
        sleep(700);
        if (isSortDialogOpen()) closeSortViaX();
        waitUntil(() -> !isSortDialogOpen(), 2000);
        sleep(500);
        int firstDur = getFirstTrackDurationSec();
        log.info("Sort Duration - bai dau {}s", firstDur);
        if (firstDur >= 0 && firstDur < 120) { // bai dau ngan -> dang tang dan -> dao chieu
            ensureSortDialogOpen();
            GestureUtils.tap(driver, SORT_DIR_DURATION_X, SORT_DIR_DURATION_Y);
            sleep(700);
            if (isSortDialogOpen()) closeSortViaX();
            waitUntil(() -> !isSortDialogOpen(), 2000);
            sleep(500);
            log.info("Da dao chieu Duration -> bai dai len dau (bai dau moi {}s)", getFirstTrackDurationSec());
        }
    }

    /** Option dang active (co mui ten con). */
    public boolean isSortActive(String name) {
        return existsImmediately(AppiumBy.xpath("//*[@content-desc='" + name + "']/*"));
    }

    public void closeSortViaX() { GestureUtils.tap(driver, SORT_CLOSE_X, SORT_CLOSE_Y); }
    public void closeSortViaScrim() { if (existsImmediately(scrim)) click(scrim); }
    public void closeSortViaBack() { pressBack(); }

    /** Dam bao Sort dialog dang mo (mo neu chua). */
    public void ensureSortDialogOpen() {
        if (!isSortDialogOpen()) {
            openSortDialog();
            waitUntil(this::isSortDialogOpen, 3000);
        }
    }

    /**
     * Sau khi chon 1 option, app AP DUNG ngay va DONG dialog (hoac giu mo tuy build).
     * Method nay dong (neu con mo) roi mo lai de doc trang thai active da LUU -> isSortActive
     * doc duoc chinh xac trong moi truong hop.
     */
    public void reopenSortDialog() {
        if (isSortDialogOpen()) {
            closeSortViaX();
            waitUntil(() -> !isSortDialogOpen(), 2000);
        }
        openSortDialog();
        waitUntil(this::isSortDialogOpen, 3000);
    }

    // ===================== CONTEXT MENU (EDIT SHEET) =====================
    public boolean isTrackMenuOpen() {
        return existsImmediately(mShareTrack) && existsImmediately(mRename) && existsImmediately(mAddQueue);
    }

    public boolean areAllMenuActionsDisplayed() {
        return existsImmediately(mPlay) && existsImmediately(mAddQueue) && existsImmediately(mAddPlaylist)
                && existsImmediately(mRename) && existsImmediately(mFileInfo)
                && existsImmediately(mShareTrack) && existsImmediately(mDelete);
    }

    /** Sheet co dang hien title (track) tuong ung. */
    public boolean menuShowsTitle(String title) {
        return existsImmediately(AppiumBy.accessibilityId(title));
    }

    public void tapMenuPlay()        { click(mPlay); }
    public void tapMenuAddToQueue()  { click(mAddQueue); }
    public void tapMenuAddPlaylist() { click(mAddPlaylist); }
    public void tapMenuRename()      { click(mRename); }
    public void tapMenuFileInfo()    { click(mFileInfo); }
    public void tapMenuShareTrack()  { click(mShareTrack); }
    public void tapMenuDelete()      { click(mDelete); }

    public void closeMenuViaScrim()   { if (existsImmediately(scrim)) click(scrim); }
    public void closeMenuViaBack()    { pressBack(); }
    public void closeMenuViaSwipeDown() { GestureUtils.swipe(driver, 860, 1300, 860, 2200, 500); }

    // ===================== ADD TO PLAYLIST SHEET =====================
    public boolean isAddToPlaylistOpen() {
        return existsImmediately(addPlaylistTitle) && existsImmediately(createNewPlaylist);
    }

    public boolean isPlaylistListed(String name) {
        return existsImmediately(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"" + name + "\")"));
    }

    /** So bai trong My Favorite doc tu "My Favorite\nN tracks". -1 neu khong thay. */
    public int getFavoriteCount() {
        String d = getContentDesc(favoriteRow);
        if (d.isEmpty()) return -1;
        Matcher m = Pattern.compile("(\\d+)\\s*tracks").matcher(d);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    public void tapCreateNewPlaylist() { click(createNewPlaylist); }
    public void tapPlaylistByName(String name) {
        click(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + name + "\")"));
    }

    // ===================== CREATE NEW PLAYLIST DIALOG =====================
    public boolean isCreatePlaylistDialogOpen() {
        return existsImmediately(editText) && existsImmediately(btnSave) && existsImmediately(createNewPlaylist);
    }
    public void enterPlaylistName(String name) {
        WebElement e = findElement(editText);
        e.click(); e.clear(); e.sendKeys(name);
    }
    public void tapCreateSave()   { click(btnSave); }
    public void tapCreateCancel() { click(btnCancel); }

    // ===================== RENAME DIALOG =====================
    public boolean isRenameDialogOpen() {
        return existsImmediately(renameTitle) && existsImmediately(editText) && existsImmediately(btnSave);
    }
    public String getRenameText() {
        try { String t = findElement(editText).getText(); return t != null ? t : ""; }
        catch (Exception e) { return ""; }
    }
    public String getRenameCounter() { return getContentDesc(renameCounter); }
    public void clearRenameViaX() { GestureUtils.tap(driver, RENAME_CLEAR_X, RENAME_CLEAR_Y); }
    public void clearRenameField() {
        try { findElement(editText).clear(); } catch (Exception ignored) {}
    }
    public void setRenameText(String text) {
        WebElement e = findElement(editText);
        e.click(); e.clear(); e.sendKeys(text);
    }
    public void tapRenameSave()   { click(btnSave); }
    public void tapRenameCancel() { click(btnCancel); }

    // ===================== TRACK INFORMATION =====================
    public boolean isTrackInfoOpen() { return existsImmediately(infoTitle); }
    public boolean areAllInfoFieldsDisplayed() {
        String[] labels = {"File path", "Title", "Album", "Artist", "Genres", "Duration", "Size"};
        for (String l : labels) if (!existsImmediately(AppiumBy.accessibilityId(l))) return false;
        return true;
    }
    public boolean infoHasFilePathValue() {
        return existsImmediately(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"/storage/emulated/0/Music\")"));
    }
    public void closeInfo() { if (existsImmediately(scrim)) click(scrim); else pressBack(); }

    // ===================== DELETE CONFIRM (IN-APP) =====================
    public boolean isDeleteConfirmOpen() {
        return existsImmediately(btnDelete) && existsImmediately(btnCancel);
    }
    public boolean isDeleteTitleShown() { return existsImmediately(deleteDialogTitle); }
    public boolean deleteMessageContains(String sub) {
        return existsImmediately(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"" + sub + "\")"));
    }
    public void tapDeleteConfirm() { click(btnDelete); }
    public void tapDeleteCancel()  { click(btnCancel); }

    // ===================== SYSTEM MEDIA DELETE =====================
    // Nut Allow/Deny theo text (Android 14/ColorOS co the doi resource-id/package -> bam text cho ben).
    private final By sysAllowByText = AppiumBy.androidUIAutomator(
            "new UiSelector().textMatches(\"(?i)(allow|delete|move to trash|ok|cho phep|xoa)\")");
    private final By sysDenyByText = AppiumBy.androidUIAutomator(
            "new UiSelector().textMatches(\"(?i)(don.t allow|deny|cancel|tu choi|huy)\")");

    public boolean isSystemDeleteOpen() {
        return existsImmediately(sysDeleteTitle) || existsImmediately(sysAllowBtn)
                || existsImmediately(sysAllowByText);
    }
    public boolean waitSystemDeleteOpen(long ms) {
        return waitUntil(this::isSystemDeleteOpen, ms);
    }
    public void systemDeleteAllow() {
        if (existsImmediately(sysAllowBtn)) { click(sysAllowBtn); return; }
        if (existsImmediately(sysAllowByText)) { click(sysAllowByText); }
    }
    public void systemDeleteDeny() {
        if (existsImmediately(sysDenyBtn)) { click(sysDenyBtn); return; }
        if (existsImmediately(sysDenyByText)) { click(sysDenyByText); }
    }
    /** Allow dialog quyen xoa NEU co (Android co the xoa thang khong hoi neu da cap quyen). */
    public boolean allowSystemDeleteIfPresent(long ms) {
        if (waitSystemDeleteOpen(ms)) { systemDeleteAllow(); return true; }
        return false;
    }

    // ===================== SELECT MODE =====================
    public boolean isSelectModeActive() { return existsImmediately(selectCountLbl); }
    public int getSelectedCount() { return parseFirstInt(getContentDesc(selectCountLbl)); }
    public void selectTrack(int index) { clickRow(index); }
    public void tapSelectAll() { GestureUtils.tap(driver, SELECT_ALL_X, SELECT_ALL_Y); }
    public void exitSelectMode() { GestureUtils.tap(driver, SELECT_BACK_X, SELECT_BACK_Y); }
    public boolean areSelectActionsDisplayed() {
        return existsImmediately(selAddQueue) && existsImmediately(selAddList)
                && existsImmediately(selShare) && existsImmediately(selDelete);
    }
    // Nhan toa do ICON (label clickable=false). y=2311.
    public void tapSelAddToQueue() { GestureUtils.tap(driver, SEL_ADDQUEUE_X, SEL_ACTION_Y); log.info("Tap select: Add to queue"); }
    public void tapSelAddToList()  { GestureUtils.tap(driver, SEL_ADDLIST_X, SEL_ACTION_Y); log.info("Tap select: Add to list"); }
    public void tapSelShareFile()  { GestureUtils.tap(driver, SEL_SHARE_X, SEL_ACTION_Y); log.info("Tap select: Share file"); }
    public void tapSelDeleteFile() { GestureUtils.tap(driver, SEL_DELETE_X, SEL_ACTION_Y); log.info("Tap select: Delete file"); }

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
    public void closeShareSheet() { pressBack(); }

    // ===================== PLAYING QUEUE =====================
    public boolean isPlayingQueueOpen() { return existsImmediately(queueTitle); }
    public int getQueueTotal() {
        String d = getContentDesc(queueCountLbl);
        Matcher m = Pattern.compile("(\\d+)\\s*tracks\\((\\d+)/(\\d+)\\)").matcher(d);
        return m.find() ? Integer.parseInt(m.group(3)) : -1;
    }
    public int getQueuePosition() {
        String d = getContentDesc(queueCountLbl);
        Matcher m = Pattern.compile("\\((\\d+)/(\\d+)\\)").matcher(d);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }
    public int getQueueRowCount() { return rows().size(); }
    public void playQueueRow(int index) { clickRow(index); }
    public void tapQueueShuffle()   { GestureUtils.tap(driver, Q_SHUFFLE_X, Q_SHUFFLE_Y); }
    public void tapQueueRepeat()    { GestureUtils.tap(driver, Q_REPEAT_X, Q_REPEAT_Y); }
    public void tapQueuePlayPause() { GestureUtils.tap(driver, Q_PLAYPAUSE_X, Q_PLAYPAUSE_Y); }
    public void tapQueueBack()      { if (existsImmediately(queueBack)) click(queueBack); else pressBack(); }
    /**
     * Xoa 1 bai khoi queue: tap nut 3 cham ben phai row, neu hien option "Remove" thi tap.
     * CAN xac nhan DOM cua menu 3 cham trong Playing Queue.
     */
    public void removeFromQueue(int index) {
        // Nut 3 cham tren row queue mo menu co "Remove from queue" (+ Play/Add/.../Delete).
        // Tap 3 cham co the truot/menu mo cham -> retry + verify truoc khi click Remove.
        By remove = AppiumBy.accessibilityId("Remove from queue");
        for (int attempt = 1; attempt <= 3; attempt++) {
            int cy = rowCenterY(index);
            GestureUtils.tap(driver, ROW_MENU_X, cy);
            log.info("Tap 3 cham queue row {} (y={}) lan {}", index, cy, attempt);
            if (waitUntil(() -> existsImmediately(remove), 1500)) {
                click(remove);
                log.info("Da tap 'Remove from queue' row {}", index);
                // Hien dialog xac nhan "Do you want to remove...?" -> bam DELETE de xoa that.
                if (waitUntil(() -> existsImmediately(btnDelete), 2500)) {
                    click(btnDelete);
                    log.info("Da xac nhan DELETE remove khoi queue");
                }
                return;
            }
        }
        log.warn("removeFromQueue: khong mo duoc menu/Remove cho row {} sau 3 lan", index);
    }

    // ===================== PLAY NOW (FULL PLAYER) - COORDS UOC LUONG =====================
    public boolean isPlayNowOpen() { return existsImmediately(playNowMarker); }
    public void pnTapPlayPause() { GestureUtils.tap(driver, PN_PLAYPAUSE_X, PN_CTRL_Y); }
    public void pnTapNext()      { GestureUtils.tap(driver, PN_NEXT_X, PN_CTRL_Y); }
    public void pnTapPrev()      { GestureUtils.tap(driver, PN_PREV_X, PN_CTRL_Y); }
    public void pnTapShuffle()   { GestureUtils.tap(driver, PN_SHUFFLE_X, PN_CTRL_Y); }
    public void pnTapRepeat()    { GestureUtils.tap(driver, PN_REPEAT_X, PN_CTRL_Y); }
    public void pnTapHeart()     { GestureUtils.tap(driver, PN_HEART_X, PN_ICON_Y); }
    public void pnTapAddPlaylist() { GestureUtils.tap(driver, PN_ADDLIST_X, PN_ICON_Y); }
    public void pnTapEqualizer() { GestureUtils.tap(driver, PN_EQ_X, PN_ICON_Y); }
    public void pnTapSleep()     { GestureUtils.tap(driver, PN_SLEEP_X, PN_ICON_Y); }
    public void pnTapQueue()     { GestureUtils.tap(driver, PN_QUEUE_X, PN_ICON_Y); }
    public void pnTapMenu()      { GestureUtils.tap(driver, PN_MENU_X, PN_MENU_Y); }
    /** Dong Play Now: tap nut collapse (54,127); fallback BACK neu chua dong. */
    public void pnCollapse() {
        GestureUtils.tap(driver, PN_COLLAPSE_X, PN_COLLAPSE_Y);
        if (!waitUntil(() -> !isPlayNowOpen(), 1500)) {
            pressBack();
            waitUntil(() -> !isPlayNowOpen(), 1500);
        }
        log.info("Collapse Play Now");
    }
    /** Tap seekbar tai ti le 0..1. */
    public void pnSeekTo(double frac) {
        int x = (int) (PN_SEEK_LEFT_X + frac * (PN_SEEK_RIGHT_X - PN_SEEK_LEFT_X));
        GestureUtils.tap(driver, x, PN_SEEK_Y);
    }
}