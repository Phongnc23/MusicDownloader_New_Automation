package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Share theo gioi han 10 bai (TC_ART_026..033). Business rule:
 *  - Collection > 10 bai: Share BI CHAN (sheet dong + toast, KHONG mo share resolver).
 *  - Collection <= 10 bai: Share THANH CONG (mo Android share resolver).
 *
 * App moi co so bai thuc te khac file cu -> test DOC count dong tu sheet roi assert theo rule >10.
 * (BrowserDownloader=3, VoiceChanger=321, Music Download=3, RecoveredAudios=1, Notifications=1)
 */
public class Artists05_Verify_Share_With_Limit extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    /**
     * Mo artist card 0 -> resolve folder THAT (uu tien preferFolder, neu mat -> tim theo rule over10,
     * neu khong co -> skip) -> sheet folder -> Share, assert theo rule >10.
     */
    private void shareFolderAndVerify(HomePage home, ArtistsPage artists, String preferFolder, boolean over10) {
        artists.tapArtistCard(0); home.sleep(1500);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong mo duoc Artist Detail");
        String folder = preferFolder;
        if (folder == null || !artists.isFolderListed(folder)) {
            folder = artists.findFolderNameByRule(over10);
            if (folder == null || folder.isEmpty()) {
                throw new SkipException("Khong co folder " + (over10 ? ">10" : "<=10")
                        + " trong artist de test share (data khong du).");
            }
        }
        artists.tapAlbumFolder(folder); home.sleep(1500);
        Assert.assertTrue(artists.isFolderDetailOpen(folder), "Khong mo duoc folder " + folder);
        artists.tapDetailMenu(); home.sleep(1000);
        Assert.assertTrue(artists.isFourActionSheetOpen(), "Khong mo duoc sheet folder " + folder);
        int n = artists.getSheetSongCount();
        artists.tapSheetShare();
        if (n > 10) {
            home.sleep(1500);
            Assert.assertFalse(artists.isShareSheetOpen(), folder + " (" + n + " >10) van mo share resolver");
            Assert.assertFalse(artists.isFourActionSheetOpen(), folder + " bi chan nhung sheet khong dong");
            ExtentReportManager.getTest().log(Status.PASS,
                    "Share folder " + folder + " (" + n + " bai >10) BI CHAN: khong mo resolver, sheet dong.");
        } else {
            Assert.assertTrue(artists.waitShareSheetOpen(5000), folder + " (" + n + " <=10) khong mo resolver");
            ExtentReportManager.getTest().log(Status.PASS,
                    "Share folder " + folder + " (" + n + " bai <=10) THANH CONG: mo resolver.");
            artists.closeShareSheet(); home.sleep(800);
        }
    }

    @Test(description = "TC_ART_026: Share artist <unknown> (>10) bi chan")
    public void TC_ART_026_share_artist_blocked() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.openArtistMenuFromList(); home.sleep(1000);
        int n = artists.getSheetSongCount();
        Assert.assertTrue(n > 10, "Artist <unknown> phai co >10 bai (thuc te=" + n + ")");
        artists.tapSheetShare(); home.sleep(1500);
        Assert.assertFalse(artists.isShareSheetOpen(), "Share artist >10 van mo resolver");
        Assert.assertFalse(artists.isFourActionSheetOpen(), "Share bi chan nhung sheet khong dong");
        ExtentReportManager.getTest().log(Status.PASS, "Share artist (" + n + " bai >10) bi chan.");
    }

    @Test(description = "TC_ART_027: Share folder <=10 bai (uu tien Music Download) -> thanh cong")
    public void TC_ART_027_share_music_download() {
        HomePage home = new HomePage();
        shareFolderAndVerify(home, goArtists(home), "Music Download", false);
    }

    @Test(description = "TC_ART_028: Share folder >10 bai (uu tien VoiceChanger) -> bi chan")
    public void TC_ART_028_share_voicechanger() {
        HomePage home = new HomePage();
        shareFolderAndVerify(home, goArtists(home), "VoiceChanger", true);
    }

    @Test(description = "TC_ART_029: Share folder <=10 bai (uu tien Notifications) -> thanh cong")
    public void TC_ART_029_share_notifications() {
        HomePage home = new HomePage();
        shareFolderAndVerify(home, goArtists(home), "Notifications", false);
    }

    @Test(description = "TC_ART_030: Share folder <=10 bai (uu tien BrowserDownloader) -> thanh cong")
    public void TC_ART_030_share_browserdownloader() {
        HomePage home = new HomePage();
        shareFolderAndVerify(home, goArtists(home), "BrowserDownloader", false);
    }

    @Test(description = "TC_ART_031: Share folder RecoveredAudios (<=10) thanh cong")
    public void TC_ART_031_share_recoveredaudios() {
        // SKIP: folder/album "RecoveredAudios" khong con ton tai tren may (data that da bi xoa).
        // Giu lai test de tai kich hoat khi co du lieu RecoveredAudios.
        throw new SkipException("Bo qua: khong co du lieu 'RecoveredAudios' tren thiet bi.");
    }

    @Test(description = "TC_ART_032: Share 1 track tu danh sach luon thanh cong (1 file < 10)")
    public void TC_ART_032_share_single_track() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.tapArtistCard(0); home.sleep(1500);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong mo duoc Artist Detail");
        artists.openDetailTrackMenu(0); home.sleep(1000);
        artists.tapSheetShare(); // "Share track" trong menu 7 action
        Assert.assertTrue(artists.waitShareSheetOpen(5000), "Share 1 track khong mo resolver");
        ExtentReportManager.getTest().log(Status.PASS, "Share 1 track -> mo resolver (1 file <10).");
        artists.closeShareSheet(); home.sleep(800);
    }

    @Test(description = "TC_ART_033: Cancel share intent quay ve app")
    public void TC_ART_033_cancel_share() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.tapArtistCard(0); home.sleep(1500);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong mo duoc Artist Detail");
        artists.openDetailTrackMenu(0); home.sleep(1000);
        artists.tapSheetShare();
        Assert.assertTrue(artists.waitShareSheetOpen(5000), "Khong mo duoc resolver de cancel");
        artists.closeShareSheet(); home.sleep(1200);
        Assert.assertFalse(artists.isShareSheetOpen(), "Cancel xong van con o resolver");
        Assert.assertTrue(artists.isArtistDetailOpen(), "Cancel share khong tro lai app (Artist Detail)");
        ExtentReportManager.getTest().log(Status.PASS, "Cancel share -> tro lai app.");
    }
}