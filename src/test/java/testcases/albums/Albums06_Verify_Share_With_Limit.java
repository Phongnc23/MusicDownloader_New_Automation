package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Share theo gioi han 10 bai (TC_ALB_016..022). Business rule:
 *  - Album > 10 bai: Share BI CHAN (sheet dong + toast, KHONG mo share resolver).
 *  - Album <= 10 bai: Share THANH CONG (mo Android share resolver).
 *
 * LUU Y DATA: album co the bi xoa giua cac lan chay (test delete o module khac). -> KHONG dua vao
 * ten cung: shareAlbumAndVerify uu tien ten mong muon, neu KHONG con -> TU TIM album co thuc theo
 * rule (>10 hoac <=10). Test DOC count dong tu sheet roi assert theo rule >10.
 */
public class Albums06_Verify_Share_With_Limit extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    /**
     * Resolve ten album THAT: neu preferName con tren may -> dung; neu khong -> tim album theo rule
     * (over10). Skip neu khong co album nao thoa (data khong du de test).
     */
    private String resolveAlbum(AlbumsPage albums, String preferName, boolean over10) {
        if (preferName != null && albums.isAlbumListed(preferName)) return preferName;
        String found = albums.findAlbumNameByRule(over10);
        if (found == null || found.isEmpty()) {
            throw new SkipException("Khong co album " + (over10 ? ">10" : "<=10")
                    + " tren may de test share (data khong du).");
        }
        return found;
    }

    /** Mo album theo ten -> 3 cham detail -> sheet -> Share, assert theo rule >10. */
    private void shareAlbumAndVerify(HomePage home, AlbumsPage albums, String name) {
        albums.tapAlbumByName(name); home.sleep(1500);
        Assert.assertTrue(albums.isAlbumDetailOpen(name), "Khong mo duoc album " + name);
        albums.tapDetailMenu(); home.sleep(1000);
        Assert.assertTrue(albums.isFourActionSheetOpen(), "Khong mo duoc sheet album " + name);
        int n = albums.getSheetSongCount();
        albums.tapSheetShare();
        if (n > 10) {
            home.sleep(1500);
            Assert.assertFalse(albums.isShareSheetOpen(), name + " (" + n + " >10) van mo share resolver");
            Assert.assertFalse(albums.isFourActionSheetOpen(), name + " bi chan nhung sheet khong dong");
            ExtentReportManager.getTest().log(Status.PASS,
                    "Share album " + name + " (" + n + " bai >10) BI CHAN: khong mo resolver, sheet dong.");
        } else {
            Assert.assertTrue(albums.waitShareSheetOpen(5000), name + " (" + n + " <=10) khong mo resolver");
            // MINH CHUNG: chup share resolver dang mo NGAY luc nay, truoc khi dong
            ExtentReportManager.attachProof(
                    "Share album " + name + " (" + n + " bai <=10) THANH CONG: mo resolver - minh chung");
            albums.closeShareSheet(); home.sleep(800);
        }
    }

    @Test(description = "TC_ALB_016: Share album <=10 bai (uu tien Music Download) -> thanh cong")
    public void TC_ALB_016_share_music_download() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);
        shareAlbumAndVerify(home, albums, resolveAlbum(albums, "Music Download", false));
    }

    @Test(description = "TC_ALB_017: Share album >10 bai (uu tien VoiceChanger) -> bi chan")
    public void TC_ALB_017_share_voicechanger() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);
        shareAlbumAndVerify(home, albums, resolveAlbum(albums, "VoiceChanger", true));
    }

    @Test(description = "TC_ALB_018: Share album <=10 bai (uu tien Notifications) -> thanh cong")
    public void TC_ALB_018_share_notifications() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);
        shareAlbumAndVerify(home, albums, resolveAlbum(albums, "Notifications", false));
    }

    @Test(description = "TC_ALB_019: Share album dau tien theo gioi han 10 (assert theo count that)")
    public void TC_ALB_019_share_first_album() {
        // Dung ALBUM DAU TIEN (chac chan ton tai). shareAlbumAndVerify assert theo rule >10 dong
        // nen dung voi bat ky album nao.
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);
        String name = albums.getFirstAlbumName();
        Assert.assertFalse(name == null || name.isEmpty(), "Khong doc duoc ten album dau tien");
        shareAlbumAndVerify(home, albums, name);
    }

    @Test(description = "TC_ALB_020: Share album <=10 bai (uu tien BrowserDownloader) -> thanh cong")
    public void TC_ALB_020_share_browserdownloader() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);
        shareAlbumAndVerify(home, albums, resolveAlbum(albums, "BrowserDownloader", false));
    }

    @Test(description = "TC_ALB_021: Share single track trong album luon thanh cong (1 file < 10)")
    public void TC_ALB_021_share_single_track() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.tapAlbumCard(0); home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Khong mo duoc Album Detail");
        albums.openDetailTrackMenu(0); home.sleep(1000);
        albums.tapSheetShare(); // "Share track" trong menu 7 action
        Assert.assertTrue(albums.waitShareSheetOpen(5000), "Share 1 track khong mo resolver");
        // MINH CHUNG: chup share resolver dang mo NGAY luc nay, truoc khi dong
        ExtentReportManager.attachProof("Share 1 track -> mo resolver (1 file <10) - minh chung");
        albums.closeShareSheet(); home.sleep(800);
    }

    @Test(description = "TC_ALB_022: Cancel share intent ve app")
    public void TC_ALB_022_cancel_share() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        String name = albums.getFirstAlbumName();
        albums.tapAlbumCard(0); home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Khong mo duoc Album Detail");
        albums.openDetailTrackMenu(0); home.sleep(1000);
        albums.tapSheetShare();
        Assert.assertTrue(albums.waitShareSheetOpen(5000), "Khong mo duoc resolver de cancel");
        albums.closeShareSheet(); home.sleep(1200);
        Assert.assertFalse(albums.isShareSheetOpen(), "Cancel xong van con o resolver");
        Assert.assertTrue(albums.isAlbumDetailOpen(name), "Cancel share khong tro lai app (Album Detail)");
        ExtentReportManager.getTest().log(Status.PASS, "Cancel share -> tro lai app.");
    }
}