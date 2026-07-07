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
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object cho module ALBUMS cua app Music Downloader (Flutter).
 *
 * Cau truc (gan giong Artists, vi moi "album" thuc chat la 1 folder):
 *  - Albums LIST: header (drawer/search), title "Albums", count "N albums",
 *    nut sort (Title-only), luoi album card content-desc "<name>\nN tracks" + 3 cham con (long-clickable).
 *  - Album DETAIL (giong folder detail): Back + 3 cham, hero "<name>\nN songs", Play all/Shuffle,
 *    section "Tracks" + sort (in-album, 7 options), danh sach track. KHONG co section Albums.
 *  - Edit sheet 4 action (album-level): Play / Add to playing queue / Add to playlist / Share track.
 *  - Sort dialog album-level: "Sort by" + chi 1 option "Title".
 *  - Select mode (NHAN GIU album): title "N item selected", X dong, chon-tat-ca,
 *    bottom bar Add to queue / Add to list / Share file.
 *
 * Menu 3 cham tren TUNG track (trong album detail) = sheet 7 action GIONG man Tracks
 * -> dung lai locator cua TracksPage de assert.
 *
 * Mini player + bottom nav dung lai HomePage.
 */
public class AlbumsPage extends BasePage {

    // ===================== LOCATORS =====================
    // Count "N albums". descriptionMatches KHONG on dinh tren Flutter app nay (rule toan du an)
    // -> dung descriptionContains("albums") (case-insensitive, bat ca tab "Albums" lan "N albums")
    // roi loc regex Java o getAlbumsCount (chi nhan element dang "<so> albums").
    private final By albumsCountCandidates = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"albums\")");
    private static final Pattern ALBUMS_COUNT_PATTERN = Pattern.compile("(\\d+)\\s*albums");
    // Album card tren LIST: content-desc "<name>\nN tracks"
    private final By albumCard = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\" tracks\")");

    // Row (track) trong album detail = content-desc chua " • "
    private static final String ROW_MARK = " • ";
    private final By rowItems = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"" + ROW_MARK + "\")");

    // Detail controls
    private final By playAll = AppiumBy.accessibilityId("Play all");
    private final By shuffle = AppiumBy.accessibilityId("Shuffle");
    private final By backBtn = AppiumBy.accessibilityId("Back");
    // Hero / sheet header: content-desc "<name>\nN songs"
    private final By heroSongs = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\" songs\")");

    // Sort dialog (album-level): "Sort by" + chi 1 option "Title".
    // Sort dialog (in-album, mo tu Album Detail): 7 options.
    private final By sortTitle = AppiumBy.accessibilityId("Sort by");
    private final By sortOptTitle = AppiumBy.accessibilityId("Title");
    private final By sortOptArtist = AppiumBy.accessibilityId("Artist");
    private final By sortOptAlbum = AppiumBy.accessibilityId("Album");
    private final By sortOptFileName = AppiumBy.accessibilityId("File name");
    private final By sortOptDuration = AppiumBy.accessibilityId("Duration");
    private final By sortOptDateAdded = AppiumBy.accessibilityId("Date added");
    private final By sortOptDateModified = AppiumBy.accessibilityId("Date modified");
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

    // ===== SELECT MODE (nhan giu album) =====
    private final By selectTitle  = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"item selected\")");
    private final By selAddQueue  = AppiumBy.accessibilityId("Add to queue");
    private final By selAddList   = AppiumBy.accessibilityId("Add to list");
    private final By selShareFile = AppiumBy.accessibilityId("Share file");

    // ===================== COORDS (DOM 1720x2408) =====================
    private static final int LIST_SORT_X = 1657, LIST_SORT_Y = 276;      // nut sort tren Albums list
    private static final int ALBUM_CARD_MENU_X = 770, ALBUM_CARD_MENU_Y = 1035; // 3 cham card dau (col 1)
    private static final int SORT_CLOSE_X = 1499, SORT_CLOSE_Y = 2171;   // X dong sort dialog album-level
    private static final int IN_ALBUM_SORT_CLOSE_X = 1499, IN_ALBUM_SORT_CLOSE_Y = 1401; // X dong sort in-album
    // Mui ten DAO CHIEU asc/desc: nam ben TRAI hang option DANG ACTIVE (DOM: con [185,2272][257,2344]
    // duoi option active). Voi "Title" (option dau, hang [140,1474][1580,1603]) -> mui ten ~ (221,1538).
    private static final int IN_ALBUM_SORT_DIR_TITLE_X = 221, IN_ALBUM_SORT_DIR_TITLE_Y = 1538;
    private static final int DETAIL_MENU_X = 1666, DETAIL_MENU_Y = 109;  // 3 cham tren album detail
    private static final int DETAIL_TRACKS_SORT_X = 1657, DETAIL_TRACKS_SORT_Y = 597; // sort section Tracks (in-album)
    private static final int ROW_MENU_X = 1666;                          // 3 cham tren tung track

    // Select mode coords
    private static final int SEL_CLOSE_X = 72,   SEL_CLOSE_Y = 127;      // X dong select mode
    private static final int SEL_ALL_X   = 1648, SEL_ALL_Y   = 127;      // chon tat ca (goc phai)
    private static final int SEL_BAR_Y   = 2311;                         // hang icon bottom bar
    private static final int SEL_QUEUE_X = 302;                          // icon Add to queue
    private static final int SEL_LIST_X  = 860;                          // icon Add to list
    private static final int SEL_SHARE_X = 1418;                         // icon Share file

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
    private By byDescContains(String s) {
        return AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + s + "\")");
    }
    /** Poll dieu kien trong ms mili-giay (dung khi cho dialog/sheet mo). */
    private boolean pollUntil(BooleanSupplier cond, long ms) {
        long end = System.currentTimeMillis() + ms;
        do { if (cond.getAsBoolean()) return true; sleep(150); } while (System.currentTimeMillis() < end);
        return cond.getAsBoolean();
    }
    /** Tap toa do (x,y) lap toi 3 lan, moi lan cho 'opened' toi 1.5s. Chong tap truot khi app churn. */
    private void tapUntilOpen(int x, int y, BooleanSupplier opened, String what) {
        for (int i = 0; i < 3; i++) {
            GestureUtils.tap(driver, x, y);
            if (pollUntil(opened, 1500)) { log.info("{} (mo sau {} tap)", what, i + 1); return; }
        }
        log.info("{} - CHUA mo sau 3 tap", what);
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

    // ===================== ALBUMS LIST =====================
    /**
     * Vao man Albums LIST CHAC CHAN. App noReset -> tab Albums NHO sub-route detail tu test truoc,
     * tapNavAlbums dap xuong dung detail dang ket (detail KHONG co bottom nav). Detail co "Play all"
     * con LIST thi KHONG -> dung playAll lam dau hieu "dang o detail" de BACK thoat ra.
     */
    public AlbumsPage gotoAlbumsList(HomePage home) {
        // Thoat moi overlay/sub-route con sot tu test truoc: select mode ("item selected"),
        // album detail (Play all), sort dialog (Sort by), edit sheet (Play), share resolver.
        escapeOverlays(6);
        home.tapNavAlbums();
        sleep(900);
        // Phong khi tab Albums van dap xuong detail/select da nho tu truoc
        escapeOverlays(4);
        return this;
    }
    /** BACK thoat cac overlay/sub-route cho toi khi ve list (KHONG BACK khi da o list -> tranh exit dialog). */
    private void escapeOverlays(int maxBack) {
        for (int i = 0; i < maxBack; i++) {
            boolean overlay = existsImmediately(selectTitle) || existsImmediately(playAll)
                    || existsImmediately(sortTitle) || existsImmediately(sPlay)
                    || isShareSheetOpen();
            if (!overlay) break;
            pressBack();
            sleep(600);
        }
    }
    /** Man Albums LIST: co count + album card, va KHONG phai detail (detail co Play all). */
    public boolean isAlbumsScreenDisplayed() {
        return !existsImmediately(playAll) && getAlbumsCount() >= 1 && existsImmediately(albumCard);
    }
    public int getAlbumsCount() {
        for (WebElement el : driver.findElements(albumsCountCandidates)) {
            Matcher m = ALBUMS_COUNT_PATTERN.matcher(descOf(el));
            if (m.find()) return Integer.parseInt(m.group(1));
        }
        return -1;
    }
    public int getAlbumCardCount() { return driver.findElements(albumCard).size(); }
    public String getFirstAlbumName() {
        List<WebElement> c = driver.findElements(albumCard);
        return c.isEmpty() ? "" : nameBeforeNewline(descOf(c.get(0)));
    }
    public int getFirstAlbumTrackCount() {
        List<WebElement> c = driver.findElements(albumCard);
        return c.isEmpty() ? -1 : parseIntBefore(descOf(c.get(0)), "tracks");
    }
    public void tapAlbumCard(int index) {
        driver.findElements(albumCard).get(index).click();
        log.info("Mo Album Detail (card index {})", index);
    }
    /**
     * Cuon luoi (swipe len) toi khi thay album theo ten. Flutter ScrollView AO HOA item ngoai
     * viewport (card thu 5 khong co trong tree luc dau) + UiScrollable.scrollIntoView khong on dinh
     * tren Flutter -> swipe thu cong trong vung luoi roi check existsImmediately. Tra true neu thay.
     */
    private boolean scrollToAlbum(String name, int maxSwipes) {
        By target = byDescContains(name);
        for (int i = 0; i < maxSwipes; i++) {
            if (existsImmediately(target)) return true;
            GestureUtils.swipe(driver, 860, 1900, 860, 800, 500); // vung luoi [330..2260], tranh mini player
            sleep(500);
        }
        return existsImmediately(target);
    }
    /** Cuon den album theo ten roi mo detail. Click co retry + fallback tap toa do (chong churn). */
    public void tapAlbumByName(String name) {
        scrollToAlbum(name, 6);
        By target = byDescContains(name);
        for (int i = 0; i < 3; i++) {
            try { click(target); log.info("Mo Album Detail: {}", name); return; }
            catch (Exception e) { sleep(400); }
        }
        // Fallback: tap thang vao tam card (Selenium 'clickable' co the khong dat duoc khi app churn)
        try {
            Rectangle r = driver.findElement(target).getRect();
            GestureUtils.tap(driver, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
            log.info("Mo Album Detail (fallback toa do): {}", name);
        } catch (Exception ignored) { log.info("Khong mo duoc album: {}", name); }
    }
    public boolean isAlbumListed(String name) {
        return scrollToAlbum(name, 6);
    }
    /**
     * Quet luoi album (tu dau), tra ve TEN album dau tien thoa rule so track:
     *  over10=true  -> album co >10 track (test nhanh Share BI CHAN);
     *  over10=false -> album co 1..10 track (test Share THANH CONG).
     * Rong neu khong tim thay. Dung THAY cho ten cung (album co the bi xoa giua cac lan chay).
     */
    public String findAlbumNameByRule(boolean over10) {
        for (int i = 0; i < 8; i++) { GestureUtils.swipe(driver, 860, 800, 860, 1900, 500); sleep(250); } // cuon len dau
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int pass = 0; pass < 8; pass++) {
            for (WebElement el : driver.findElements(albumCard)) {
                String d = descOf(el);
                String nm = nameBeforeNewline(d);
                if (nm.isEmpty() || seen.contains(nm)) continue;
                seen.add(nm);
                int cnt = parseIntBefore(d, "tracks");
                if (over10 && cnt > 10) return nm;
                if (!over10 && cnt >= 1 && cnt <= 10) return nm;
            }
            GestureUtils.swipe(driver, 860, 1900, 860, 800, 500);
            sleep(400);
        }
        return "";
    }
    /**
     * Mo album de KIEM TRA SORT (can nhieu track thi dao chieu moi thay doi bai dau):
     *  1) Uu tien "VoiceChanger" (nhieu track, on dinh) neu con tren may.
     *  2) Neu khong co -> quet luoi tim album co N tracks LON NHAT roi mo.
     * Tra ve ten album da mo (rong neu that bai).
     */
    public String openAlbumForSortCheck() {
        if (scrollToAlbum("VoiceChanger", 6)) {
            tapAlbumByName("VoiceChanger");
            return "VoiceChanger";
        }
        // Fallback: cuon len dau roi quet cac card, chon album N tracks lon nhat.
        for (int i = 0; i < 8; i++) { GestureUtils.swipe(driver, 860, 800, 860, 1900, 500); sleep(300); }
        String bestName = ""; int bestCount = -1;
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int pass = 0; pass < 8; pass++) {
            for (WebElement el : driver.findElements(albumCard)) {
                String d = descOf(el);
                String nm = nameBeforeNewline(d);
                if (nm.isEmpty() || seen.contains(nm)) continue;
                seen.add(nm);
                int cnt = parseIntBefore(d, "tracks");
                if (cnt > bestCount) { bestCount = cnt; bestName = nm; }
            }
            GestureUtils.swipe(driver, 860, 1900, 860, 800, 500);
            sleep(400);
        }
        if (!bestName.isEmpty()) {
            tapAlbumByName(bestName);
            log.info("Mo album nhieu bai nhat: {} ({} tracks)", bestName, bestCount);
        } else {
            log.info("Fallback that bai: khong tim duoc album nao");
        }
        return bestName;
    }
    public void openAlbumMenuFromList() {
        tapUntilOpen(ALBUM_CARD_MENU_X, ALBUM_CARD_MENU_Y, this::isFourActionSheetOpen, "Tap 3 cham album card dau");
    }
    public void tapListSort() {
        tapUntilOpen(LIST_SORT_X, LIST_SORT_Y, this::isSortDialogOpen, "Tap sort albums list");
    }

    // ===================== SORT DIALOG (ALBUM-LEVEL) =====================
    public boolean isSortDialogOpen() { return existsImmediately(sortTitle); }
    public boolean isSortTitleOptionDisplayed() { return existsImmediately(sortOptTitle); }
    public boolean isSortTitleActive() {
        return existsImmediately(AppiumBy.xpath("//*[@content-desc='Title']/*"));
    }
    public void tapSortTitle() { click(sortOptTitle); log.info("Chon sort Title"); }
    public void closeSortViaX() { GestureUtils.tap(driver, SORT_CLOSE_X, SORT_CLOSE_Y); }
    public void closeSortViaScrim() { if (existsImmediately(scrim)) click(scrim); }
    public void closeSortViaBack() { pressBack(); }

    // ----- In-album sort (7 options) -----
    /** Du 7 option: Title/Artist/Album/File name/Duration/Date added/Date modified. */
    public boolean areInAlbumSortOptionsDisplayed() {
        return existsImmediately(sortOptTitle) && existsImmediately(sortOptArtist)
                && existsImmediately(sortOptAlbum) && existsImmediately(sortOptFileName)
                && existsImmediately(sortOptDuration) && existsImmediately(sortOptDateAdded)
                && existsImmediately(sortOptDateModified);
    }
    public int getInAlbumSortOptionCount() {
        int c = 0;
        for (By b : new By[]{sortOptTitle, sortOptArtist, sortOptAlbum, sortOptFileName,
                sortOptDuration, sortOptDateAdded, sortOptDateModified}) {
            if (existsImmediately(b)) c++;
        }
        return c;
    }
    /** Option dang active (co icon mui ten con). */
    public boolean isSortOptionActive(String name) {
        return existsImmediately(AppiumBy.xpath("//*[@content-desc='" + name + "']/*"));
    }
    public void tapSortOption(String name) { click(AppiumBy.accessibilityId(name)); log.info("Chon sort: {}", name); }
    public void closeInAlbumSortViaX() { GestureUtils.tap(driver, IN_ALBUM_SORT_CLOSE_X, IN_ALBUM_SORT_CLOSE_Y); }
    /** Dao chieu sort in-album cho option "Title" (mui ten ben trai hang Title dang active). */
    public void toggleInAlbumSortDirectionTitle() {
        GestureUtils.tap(driver, IN_ALBUM_SORT_DIR_TITLE_X, IN_ALBUM_SORT_DIR_TITLE_Y);
        log.info("Tap mui ten dao chieu sort Title (asc/desc)");
    }

    // ===================== EDIT SHEET 4 ACTION (album-level) =====================
    public boolean isFourActionSheetOpen() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue)
                && existsImmediately(sAddList) && existsImmediately(sShare)
                && !existsImmediately(sRename) && !existsImmediately(sDelete);
    }
    public boolean areFourActionsDisplayed() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue)
                && existsImmediately(sAddList) && existsImmediately(sShare);
    }
    public boolean sheetHasNoExtraActions() {
        return !existsImmediately(sRename) && !existsImmediately(sDelete) && !existsImmediately(sFileInfo);
    }
    public boolean sheetShowsName(String name) { return existsImmediately(byDescContains(name)); }
    public int getSheetSongCount() { return parseIntBefore(getContentDesc(heroSongs), "songs"); }
    public void tapSheetPlay()     { click(sPlay); }
    public void tapSheetAddQueue() { click(sAddQueue); }
    public void tapSheetAddList()  { click(sAddList); }
    public void tapSheetShare()    { click(sShare); }
    public void closeSheetViaScrim() { if (existsImmediately(scrim)) click(scrim); }
    public void closeSheetViaBack()  { pressBack(); }

    // ===================== ALBUM DETAIL =====================
    public boolean isDetailWithTracksOpen() {
        return existsImmediately(playAll) && existsImmediately(shuffle);
    }
    public boolean isAlbumDetailOpen(String name) {
        return existsImmediately(playAll) && sheetShowsName(name);
    }
    public String getDetailHeroName() { return nameBeforeNewline(getContentDesc(heroSongs)); }
    public int getDetailHeroSongCount() { return parseIntBefore(getContentDesc(heroSongs), "songs"); }
    public void tapDetailBack()  { if (existsImmediately(backBtn)) click(backBtn); else pressBack(); }
    public void tapDetailMenu()  {
        tapUntilOpen(DETAIL_MENU_X, DETAIL_MENU_Y, this::isFourActionSheetOpen, "Tap 3 cham album detail");
    }
    public void tapDetailPlayAll() { click(playAll); }
    public void tapDetailShuffle() { click(shuffle); }
    public void tapDetailTracksSort() {
        tapUntilOpen(DETAIL_TRACKS_SORT_X, DETAIL_TRACKS_SORT_Y, this::isSortDialogOpen, "Tap sort in-album");
    }
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
    /** Menu 7-action cua track (trong album detail) DANG mo: co Share + Delete (album 4-action khong co Delete). */
    public boolean isTrackMenuOpen() {
        return existsImmediately(sShare) && existsImmediately(sDelete);
    }
    public void openDetailTrackMenu(int index) {
        int cy = rowCenterY(index);
        // Tap 3 cham track + VERIFY menu 7-action mo (tap toi 3 lan). Truoc day tap 1 lan: neu truot
        // (row dich do churn) -> menu khong mo -> tapSheetShare bam trong -> waitShareSheetOpen fail -> retry.
        tapUntilOpen(ROW_MENU_X, cy, this::isTrackMenuOpen, "Mo menu 3 cham track index " + index + " (y=" + cy + ")");
    }

    // ===================== ADD TO PLAYLIST =====================
    public boolean isAddToPlaylistOpen() {
        return existsImmediately(addPlaylistTitle) && existsImmediately(createNewPlaylist);
    }
    public void tapPlaylistByName(String name) { click(byDescContains(name)); }

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
     * ro ri resolver sang test sau (resolver con mo -> waitAppReady test sau khong thay bottom nav
     * -> "Khong vao duoc man ...").
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

    // ===================== SELECT MODE (nhan giu album) =====================
    public void longPressFirstAlbum() {
        List<WebElement> c = driver.findElements(albumCard);
        Rectangle r = c.get(0).getRect();
        longPress(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2, 900);
        log.info("Long-press album card -> select mode");
    }
    public boolean isSelectModeOpen() { return existsImmediately(selectTitle); }
    public int getSelectedCount() { return parseIntBefore(getContentDesc(selectTitle), "item selected"); }
    public void tapAlbumInSelectMode(int index) { driver.findElements(albumCard).get(index).click(); }
    public void tapSelectAll() { GestureUtils.tap(driver, SEL_ALL_X, SEL_ALL_Y); }
    public boolean isSelectModeActionsDisplayed() {
        return existsImmediately(selAddQueue) && existsImmediately(selAddList) && existsImmediately(selShareFile);
    }
    public void tapSelectAddQueue()  { GestureUtils.tap(driver, SEL_QUEUE_X, SEL_BAR_Y); }
    public void tapSelectAddList()   { GestureUtils.tap(driver, SEL_LIST_X, SEL_BAR_Y); }
    public void tapSelectShareFile() { GestureUtils.tap(driver, SEL_SHARE_X, SEL_BAR_Y); }
    public void closeSelectMode()    { GestureUtils.tap(driver, SEL_CLOSE_X, SEL_CLOSE_Y); }
}