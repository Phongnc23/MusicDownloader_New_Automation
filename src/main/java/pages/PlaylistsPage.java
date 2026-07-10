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
 * Page Object cho module PLAYLISTS cua app Music Downloader (Flutter).
 *
 * Cau truc:
 *  - Playlists LIST: header (drawer/search), title "Playlists", label "Local playlist",
 *    2 local playlist (My Favorite / Recently Played) - card content-desc "<name>\nN tracks", 3 cham con,
 *    label "My playlist (N)", nut "Create new playlist", cac user playlist (QA_PL_*) long-clickable + 3 cham.
 *  - Create dialog: label "Create new playlist", EditText (hint "Playlist name"), counter "0/60",
 *    nut CANCEL / SAVE, vung "Dismiss".
 *  - Playlist DETAIL (local & user): Back + "Show menu", hero "<name>\nN tracks", Play all/Shuffle,
 *    "N tracks" + sort, danh sach track. Empty (user, 0 track): "Empty playlist" + "Add new track".
 *  - Sheet user playlist (6 action): Play/Add to playing queue/Add to playlist/Rename/Share track/Delete from device.
 *  - Sheet local playlist: Play/Add to playing queue/Add to playlist/Share track
 *    + (Recently Played) "Clear recently played" / (My Favorite) "Clear my favorite".
 *  - Rename dialog (DOM thuc): label "Rename" + EditText prefill ten cu (hint "Title") + CANCEL/SAVE.
 *  - Delete confirm (DOM thuc): "Do you want to delete the ?" + CANCEL + DELETE (1 buoc, bam DELETE xoa luon).
 *  - Clear confirm: chi test CANCEL (nut CANCEL giong dialog Delete) -> non-destructive.
 *
 * Menu 3 cham tren tung track + Add-to-playlist picker dung lai pattern da co.
 */
public class PlaylistsPage extends BasePage {

    // ===================== LOCATORS - LIST =====================
    private final By playlistsTitle = AppiumBy.accessibilityId("Playlists");
    private final By localLabel = AppiumBy.accessibilityId("Local playlist");
    private final By myPlaylistLabel = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"My playlist\")"); // "My playlist (N)"
    private final By createNewPlaylist = AppiumBy.accessibilityId("Create new playlist");
    // Card playlist: content-desc "<name>\nN tracks" (ca local + user)
    private final By playlistCard = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\" tracks\")");

    // ===================== LOCATORS - CREATE/RENAME DIALOG =====================
    private final By nameField = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\")");
    // descriptionMatches KHONG on dinh tren Flutter app nay -> descriptionContains("/60") (vd "0/60").
    private final By charCounter = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"/60\")");
    private final By cancelBtn = AppiumBy.accessibilityId("CANCEL");
    private final By saveBtn = AppiumBy.accessibilityId("SAVE");
    private final By dismissArea = AppiumBy.accessibilityId("Dismiss");
    // Rename dialog: label "Rename" + EditText prefilled (hint "Title") + CANCEL/SAVE
    private final By renameLabel = AppiumBy.accessibilityId("Rename");
    // Delete confirm dialog (DOM thuc): msg "Do you want to delete the ?" + CANCEL + DELETE
    private final By deleteConfirmMsg = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"want to delete\")");
    private final By deleteBtn = AppiumBy.accessibilityId("DELETE");

    // ===================== LOCATORS - SHEET =====================
    private final By sPlay     = AppiumBy.accessibilityId("Play");
    private final By sAddQueue = AppiumBy.accessibilityId("Add to playing queue");
    private final By sAddList  = AppiumBy.accessibilityId("Add to playlist");
    private final By sShare    = AppiumBy.accessibilityId("Share track");
    private final By sRename   = AppiumBy.accessibilityId("Rename");
    private final By sDelete   = AppiumBy.accessibilityId("Delete from device");
    private final By sClearRecent = AppiumBy.accessibilityId("Clear recently played");
    private final By sClearFavorite = AppiumBy.accessibilityId("Clear my favorite");

    // ===================== LOCATORS - DETAIL =====================
    private final By backBtn = AppiumBy.accessibilityId("Back");
    private final By showMenu = AppiumBy.accessibilityId("Show menu");
    private final By playAll = AppiumBy.accessibilityId("Play all");
    private final By shuffle = AppiumBy.accessibilityId("Shuffle");
    private final By addNewTrack = AppiumBy.accessibilityId("Add new track");
    private final By emptyPlaylist = AppiumBy.accessibilityId("Empty playlist");
    private static final String ROW_MARK = " • ";
    private final By rowItems = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"" + ROW_MARK + "\")");

    // Add-to-playlist picker (reuse pattern)
    private final By addPlaylistTitle = AppiumBy.accessibilityId("Add to playlist");

    // Share resolver
    private final By shareSheet = AppiumBy.androidUIAutomator(
            "new UiSelector().packageName(\"com.android.intentresolver\")");

    // ===================== COORDS (DOM 1720x2408) =====================
    private static final int CARD_MENU_X = 1666;                          // 3 cham card (x), y = tam card
    private static final int DETAIL_MENU_X = 1666, DETAIL_MENU_Y = 109;   // "Show menu" tren detail
    private static final int DETAIL_SORT_X = 1639, DETAIL_SORT_Y = 597;   // sort section tracks trong detail
    private static final int ROW_MENU_X = 1666;                           // 3 cham tren tung track

    // ===================== HELPERS =====================
    private String descOf(WebElement el) {
        try {
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
    private int parseIntInParens(String s) {
        if (s == null) return -1;
        Matcher m = Pattern.compile("\\((\\d+)\\)").matcher(s);
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
    /**
     * Cuon (swipe len) toi khi thay playlist theo ten. Danh sach user playlist co the DAI; playlist
     * moi tao nam cuoi (ngoai viewport) + Flutter AO HOA item ngoai man -> swipe thu cong roi check.
     * UiScrollable.scrollIntoView khong on dinh tren Flutter. Tra true neu thay.
     */
    private boolean scrollToPlaylistName(String name, int maxSwipes) {
        By target = byDescContains(name);
        String lastSig = null;
        for (int i = 0; i < maxSwipes; i++) {
            if (existsImmediately(target)) return true;
            String sig = listSignature();
            if (sig.equals(lastSig)) break;   // swipe truoc khong doi noi dung -> DA O DAY list, dung kéo thêm
            lastSig = sig;
            GestureUtils.swipe(driver, 860, 1900, 860, 800, 500); // vung list, tranh mini player
            sleep(500);
        }
        return existsImmediately(target);
    }
    /** Chu ky nhan dien vi tri cuon: ten card cuoi cung dang render. Khong doi sau swipe = da o day. */
    private String listSignature() {
        List<WebElement> cs = driver.findElements(playlistCard);
        return cs.isEmpty() ? "" : descOf(cs.get(cs.size() - 1));
    }
    private WebElement cardByName(String name) {
        scrollToPlaylistName(name, 14);
        List<WebElement> all = driver.findElements(byDescContains(name));
        return all.isEmpty() ? null : all.get(0);
    }

    // ===================== LIST =====================
    /**
     * Vao man Playlists LIST CHAC CHAN. App noReset -> tab Playlists NHO sub-route (detail/dialog/sheet)
     * tu test truoc; tapNavPlaylists dap xuong dung man do (detail KHONG co bottom nav). Detail co
     * "Play all" con LIST thi KHONG -> escape overlay bang BACK truoc khi vao list.
     */
    public PlaylistsPage gotoPlaylistsList(HomePage home) {
        if (home.isExitDialogDisplayed()) { home.tapCancelOnDialog(); sleep(400); }
        escapeOverlays(6);
        home.tapNavPlaylists();
        sleep(900);
        if (home.isExitDialogDisplayed()) { home.tapCancelOnDialog(); sleep(400); }
        escapeOverlays(4);
        // List co the dang cuon xuong tu test truoc (scroll tim playlist) -> dua ve DAU list de
        // 'Local playlist'/'Create new playlist' (o tren cung) hien ra, tranh false-negative.
        scrollListToTop(6);
        return this;
    }
    /** Swipe xuong dua list ve dau (dung khi thay 'Local playlist' HOAC khong cuon len duoc nua). */
    private void scrollListToTop(int maxSwipes) {
        String lastSig = null;
        for (int i = 0; i < maxSwipes && !existsImmediately(localLabel); i++) {
            String sig = listSignature();
            if (sig.equals(lastSig)) break;   // khong cuon len them duoc -> da o dau
            lastSig = sig;
            GestureUtils.swipe(driver, 860, 700, 860, 1900, 400);
            sleep(400);
        }
    }
    /** BACK thoat detail/sheet/dialog/share-resolver cho toi khi ve list (KHONG BACK khi da o list). */
    private void escapeOverlays(int maxBack) {
        for (int i = 0; i < maxBack; i++) {
            boolean overlay = existsImmediately(playAll) || existsImmediately(sPlay)
                    || existsImmediately(nameField) || existsImmediately(deleteBtn)
                    || isShareSheetOpen();
            if (!overlay) break;
            pressBack();
            sleep(600);
        }
    }
    /** Man Playlists LIST: co Local label + Create, va KHONG phai detail (detail co Play all). */
    public boolean isPlaylistsScreenDisplayed() {
        return !existsImmediately(playAll) && existsImmediately(localLabel) && existsImmediately(createNewPlaylist);
    }
    public boolean isLocalLabelDisplayed() { return existsImmediately(localLabel); }
    public boolean isCreateButtonDisplayed() { return existsImmediately(createNewPlaylist); }
    public boolean isMyFavoriteListed() { return existsImmediately(byDescContains("My Favorite")); }
    public boolean isRecentlyPlayedListed() { return existsImmediately(byDescContains("Recently Played")); }
    public boolean isPlaylistListed(String name) { return scrollToPlaylistName(name, 14); }
    /** So user playlist tu label "My playlist (N)". Cuon ve dau truoc (label o tren cung). -1 neu loi. */
    public int getUserPlaylistCount() {
        scrollListToTop(8);
        return parseIntInParens(getContentDesc(myPlaylistLabel));
    }
    public int getPlaylistCardCount() { return driver.findElements(playlistCard).size(); }
    public int getTrackCountOf(String name) {
        WebElement c = cardByName(name);
        return c == null ? -1 : parseIntBefore(descOf(c), "tracks");
    }
    public void tapPlaylistByName(String name) {
        WebElement c = cardByName(name);
        if (c != null) c.click();
        log.info("Mo playlist detail: {}", name);
    }
    public void openPlaylistMenu(String name) {
        // Tap 3 cham card la coordinate-based (Flutter) -> DE TRUOT (content-desc/vi tri doi, list vua render
        // sau khi tao playlist). Tap mu 1 lan hay fail oan "khong mo duoc sheet". RETRY toi 3 lan: moi lan
        // re-find card + re-tap, cho toi khi 1 sheet mo (isSheetOpen). Cung pattern voi removeFromQueue.
        for (int attempt = 1; attempt <= 3; attempt++) {
            WebElement c = cardByName(name);
            if (c == null) return;
            Rectangle r = c.getRect();
            int cy = r.getY() + r.getHeight() / 2;
            // Card sat day (gan mini player) -> tap 3 cham co the truot. Nang len roi tim lai.
            if (cy > 2050) {
                GestureUtils.swipe(driver, 860, 1600, 860, 1050, 400);
                sleep(500);
                c = cardByName(name);
                if (c == null) return;
                r = c.getRect();
                cy = r.getY() + r.getHeight() / 2;
            }
            GestureUtils.tap(driver, CARD_MENU_X, cy);
            log.info("Tap 3 cham playlist: {} (y={}) lan {}", name, cy, attempt);
            if (waitUntil(this::isSheetOpen, 1500)) return;   // sheet da mo -> xong
            sleep(400);
        }
        log.warn("openPlaylistMenu: khong mo duoc sheet cho '{}' sau 3 lan tap 3 cham", name);
    }
    public void tapCreateNewPlaylist() { click(createNewPlaylist); log.info("Tap Create new playlist"); }

    // ===================== CREATE / RENAME DIALOG =====================
    public boolean isCreateDialogOpen() {
        return existsImmediately(nameField) && existsImmediately(saveBtn) && existsImmediately(cancelBtn);
    }
    public boolean isCharCounterZero() {
        String c = getContentDesc(charCounter);
        return c != null && c.startsWith("0/");
    }
    public String getCharCounter() { return getContentDesc(charCounter); }
    public String getNameFieldText() {
        try { return getText(nameField); } catch (Exception e) { return ""; }
    }
    public void typePlaylistName(String name) { sendKeys(nameField, name); log.info("Nhap ten playlist: {}", name); }
    public void tapDialogSave() { click(saveBtn); }
    public void tapDialogCancel() { click(cancelBtn); }
    public void dismissDialog() {
        // Khi keyboard mo, dialog bi day len -> tap scrim (860,350) co the trung dialog.
        // BACK dong keyboard roi dialog tin cay hon. Lan 2 neu lan 1 moi chi dong keyboard.
        pressBack();
        sleep(500);
        if (isCreateDialogOpen()) { pressBack(); sleep(400); }
    }

    // Rename dialog (label "Rename" + EditText prefilled + SAVE/CANCEL)
    public boolean isRenameDialogOpen() {
        return existsImmediately(renameLabel) && existsImmediately(nameField) && existsImmediately(saveBtn);
    }
    public String getRenameFieldText() {
        try { return getText(nameField); } catch (Exception e) { return ""; }
    }
    public void clearNameField() {
        try { driver.findElement(nameField).clear(); } catch (Exception ignored) {}
    }

    // ===================== SHEET =====================
    public boolean isSheetOpen() { return existsImmediately(sPlay) && existsImmediately(sShare); }
    /** User playlist: 6 action (co Rename + Delete). */
    public boolean isUserPlaylistSheetOpen() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue) && existsImmediately(sAddList)
                && existsImmediately(sRename) && existsImmediately(sShare) && existsImmediately(sDelete);
    }
    public boolean userSheetHasSixActions() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue) && existsImmediately(sAddList)
                && existsImmediately(sRename) && existsImmediately(sShare) && existsImmediately(sDelete);
    }
    /** Local playlist: KHONG co Rename/Delete. */
    public boolean isLocalSheetOpen() {
        return existsImmediately(sPlay) && existsImmediately(sAddQueue) && existsImmediately(sAddList)
                && existsImmediately(sShare) && !existsImmediately(sRename) && !existsImmediately(sDelete);
    }
    public boolean sheetHasClearRecentlyPlayed() { return existsImmediately(sClearRecent); }
    public boolean sheetHasClearMyFavorite() { return existsImmediately(sClearFavorite); }
    public boolean sheetShowsName(String name) { return existsImmediately(byDescContains(name)); }
    public void tapSheetPlay()     { click(sPlay); }
    public void tapSheetAddQueue() { click(sAddQueue); }
    public void tapSheetAddList()  { click(sAddList); }
    public void tapSheetShare()    { click(sShare); }
    public void tapSheetRename()   { click(sRename); }
    public void tapSheetDelete()   { click(sDelete); }
    public void tapSheetClearRecent() { click(sClearRecent); }
    public void closeSheetViaBack() { pressBack(); }
    public void closeSheetViaScrim() {
        // Scrim phia tren sheet -> tap vung tren cung
        GestureUtils.tap(driver, 860, 200);
    }

    // ===================== ADD TO PLAYLIST PICKER =====================
    public boolean isAddToPlaylistPickerOpen() {
        // Picker co "Create new playlist" + it nhat 1 ten playlist (vd "My Favorite")
        return existsImmediately(createNewPlaylist)
                && (existsImmediately(byDescContains("My Favorite")) || existsImmediately(addPlaylistTitle));
    }

    // ===================== DETAIL =====================
    /** Doc hero "<name>\nN tracks" (phan tu co \n + tracks). */
    private String detailHeroRaw() {
        for (WebElement e : driver.findElements(byDescContains("tracks"))) {
            String d = descOf(e);
            if (d.contains("\n")) return d;
        }
        return "";
    }
    public boolean isDetailWithControlsOpen() {
        return existsImmediately(playAll) && existsImmediately(shuffle);
    }
    public boolean isPlaylistDetailOpen(String name) {
        return existsImmediately(playAll) && sheetShowsName(name);
    }
    public String getDetailHeroName() { return nameBeforeNewline(detailHeroRaw()); }
    public int getDetailHeroTrackCount() { return parseIntBefore(detailHeroRaw(), "tracks"); }
    public boolean isEmptyPlaylistShown() { return existsImmediately(emptyPlaylist); }
    public boolean isAddNewTrackDisplayed() { return existsImmediately(addNewTrack); }
    public void tapAddNewTrack() { click(addNewTrack); }
    public void tapDetailBack()  { if (existsImmediately(backBtn)) click(backBtn); else pressBack(); }
    public void tapDetailMenu()  { GestureUtils.tap(driver, DETAIL_MENU_X, DETAIL_MENU_Y); }
    public void tapDetailPlayAll() { click(playAll); }
    public void tapDetailShuffle() { click(shuffle); }
    public void tapDetailSort() { GestureUtils.tap(driver, DETAIL_SORT_X, DETAIL_SORT_Y); }
    public int getDetailRowCount() { return rows().size(); }
    public String getFirstDetailTrackTitle() {
        List<WebElement> r = rows();
        return r.isEmpty() ? "" : nameBeforeNewline(descOf(r.get(0)));
    }
    public void playDetailTrack(int index) { rows().get(index).click(); }
    public void openDetailTrackMenu(int index) {
        int cy = rowCenterY(index);
        GestureUtils.tap(driver, ROW_MENU_X, cy);
    }

    // ===================== CONFIRM DIALOG =====================
    // Delete playlist (DOM thuc): "Do you want to delete the ?" + CANCEL + DELETE (1 buoc, bam DELETE xoa luon).
    public boolean isDeleteConfirmOpen() {
        return existsImmediately(deleteBtn) && existsImmediately(cancelBtn);
    }
    public boolean deleteConfirmMsgShown() { return existsImmediately(deleteConfirmMsg); }
    public void tapDeleteConfirm() { click(deleteBtn); log.info("Bam DELETE xac nhan"); }
    public void tapDeleteCancel() { click(cancelBtn); }

    // Confirm chung (Clear recently played - test CANCEL): dialog co CANCEL, khong phai create/rename (khong EditText).
    public boolean isConfirmDialogOpen() {
        if (existsImmediately(nameField)) return false;
        return existsImmediately(cancelBtn);
    }
    public void tapConfirmCancel() { if (existsImmediately(cancelBtn)) click(cancelBtn); else pressBack(); }
    /** Bam nut xac nhan (Delete/Clear). Thu DELETE truoc, roi cac nhan du phong (Clear chua co DOM). */
    public boolean tapConfirmAccept() {
        String[] labels = {"DELETE", "CLEAR", "OK", "CONFIRM", "YES", "Delete", "Clear", "Ok", "Confirm", "Yes"};
        for (String lbl : labels) {
            By b = AppiumBy.accessibilityId(lbl);
            if (existsImmediately(b)) { click(b); log.info("Bam confirm: {}", lbl); return true; }
        }
        return false;
    }

    // ===================== HIGH-LEVEL FLOWS =====================
    /** Tao playlist moi (mo Create dialog -> nhap ten -> SAVE). */
    /**
     * Dam bao co user playlist RONG ten `name` (tao neu CHUA co). Portable: chay may nao cung co data,
     * KHONG phu thuoc fixture san tren thiet bi. Cac test lien quan (detail/sheet/search) goi ham nay
     * -> test chay dau se TAO, cac test sau DUNG LAI (tan dung data da tao). Ket thuc tren Playlists list.
     */
    public void ensureUserPlaylist(HomePage home, String name) {
        gotoPlaylistsList(home);
        if (isPlaylistListed(name)) { gotoPlaylistsList(home); return; }
        gotoPlaylistsList(home);          // ve dau list de thay nut "Create new playlist"
        createPlaylistFlow(name);
        gotoPlaylistsList(home);
        log.info("ensureUserPlaylist: da dam bao playlist '{}' ton tai", name);
    }

    public void createPlaylistFlow(String name) {
        tapCreateNewPlaylist();
        sleep(900);
        typePlaylistName(name);
        sleep(400);
        tapDialogSave();
        sleep(1300);
        log.info("Tao playlist xong: {}", name);
    }
    /** Xoa THAT 1 user playlist theo ten (bam DELETE tren confirm dialog). Retry 3 lan. true neu da bam confirm. */
    public boolean deletePlaylistReal(String name) {
        for (int attempt = 0; attempt < 3; attempt++) {
            openPlaylistMenu(name);
            sleep(900);
            if (existsImmediately(sDelete)) {
                tapSheetDelete();
                sleep(900);
                boolean ok = tapConfirmAccept();
                sleep(1300);
                return ok;
            }
            // Tap 3 cham truot -> dong overlay AN TOAN, KHONG blind-BACK tren list (se bung exit dialog).
            if (isSheetOpen()) closeSheetViaScrim();          // mo nham 1 sheet khac -> tap scrim
            else if (existsImmediately(playAll)) pressBack();  // lo vao detail -> BACK ve list
            sleep(600);
        }
        return false;
    }

    /** Tien ich don rac: xoa het user playlist co ten bat dau bang prefix junk (tru cac ten can giu). */
    private static final String[] JUNK_PREFIXES = {
            "QA_REN_", "QA_OLD_", "QA_NEW_", "QA_DEL_", "QA_DCAN_", "QA_AUTO_", "QA_CANCEL_"};
    public int deleteJunkPlaylists() {
        int deleted = 0;
        java.util.Set<String> skip = new java.util.HashSet<>();   // junk xoa khong duoc -> bo qua, khong dung loop
        for (int guard = 0; guard < 80; guard++) {
            String junk = findJunkName(skip);
            if (junk == null) break;
            if (deletePlaylistReal(junk)) { deleted++; log.info("Da xoa junk: {}", junk); }
            else { skip.add(junk); log.warn("Bo qua junk khong xoa duoc: {}", junk); }
            sleep(500);
        }
        return deleted;
    }
    private String findJunkName(java.util.Set<String> skip) {
        scrollListToTop(14);
        String lastSig = null;
        for (int sw = 0; sw < 16; sw++) {
            for (WebElement e : driver.findElements(playlistCard)) {
                String nm = nameBeforeNewline(descOf(e));
                for (String p : JUNK_PREFIXES) if (nm.startsWith(p) && !skip.contains(nm)) return nm;
            }
            String sig = listSignature();
            if (sig.equals(lastSig)) break;   // da o day list
            lastSig = sig;
            GestureUtils.swipe(driver, 860, 1900, 860, 800, 500);
            sleep(400);
        }
        return null;
    }

    // ===================== SHARE =====================
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
}