package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Album/Folder section (TC_ART_034..042).
 * Dung folder "Music Download" (3 bai, nho -> rows hien het) lam dai dien.
 */
public class Artists06_Verify_Album_Folders extends BaseTest {

    private static final String FOLDER = "Music Download";

    private ArtistsPage goArtistDetail(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        artists.tapArtistCard(0);
        home.waitUntil(artists::isArtistDetailOpen, 6000);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong mo duoc Artist Detail");
        return artists;
    }

    private ArtistsPage goFolder(HomePage home) {
        ArtistsPage artists = goArtistDetail(home);
        artists.tapAlbumFolder(FOLDER);
        home.waitUntil(() -> artists.isFolderDetailOpen(FOLDER), 6000);
        Assert.assertTrue(artists.isFolderDetailOpen(FOLDER), "Khong mo duoc folder " + FOLDER);
        return artists;
    }

    @Test(description = "TC_ART_034: Section Albums hien thi cac folder")
    public void TC_ART_034_albums_section() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtistDetail(home);

        Assert.assertTrue(artists.isAlbumsSectionDisplayed(), "Khong co section Albums");
        Assert.assertTrue(artists.isFolderListed("Music Download"), "Khong thay folder Music Download");
        Assert.assertTrue(artists.isFolderListed("VoiceChanger"), "Khong thay folder VoiceChanger");
        ExtentReportManager.getTest().log(Status.PASS, "Section Albums hien cac folder.");
    }

    @Test(description = "TC_ART_035: Click folder Music Download mo man folder")
    public void TC_ART_035_open_folder() {
        HomePage home = new HomePage();
        goFolder(home);
        ExtentReportManager.getTest().log(Status.PASS, "Click folder -> mo man folder.");
    }

    @Test(description = "TC_ART_036: Folder hero co N songs")
    public void TC_ART_036_folder_hero_songs() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        Assert.assertTrue(artists.getDetailHeroSongCount() > 0, "Folder hero khong co so bai");
        ExtentReportManager.getTest().log(Status.PASS,
                "Folder hero: " + artists.getDetailHeroName() + " - " + artists.getDetailHeroSongCount() + " songs.");
    }

    @Test(description = "TC_ART_037: Play all trong folder phat bai dau folder")
    public void TC_ART_037_folder_play_all() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        artists.tapDetailPlayAll();
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Play all folder khong phat");
        ExtentReportManager.getTest().log(Status.PASS, "Play all folder -> phat.");
    }

    @Test(description = "TC_ART_038: Tracks trong folder chi gom bai cua folder do")
    public void TC_ART_038_folder_tracks_only() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        int hero = artists.getDetailHeroSongCount();
        int rows = artists.getDetailRowCount();
        ExtentReportManager.getTest().log(Status.INFO, "Folder " + FOLDER + " hero=" + hero + " rows=" + rows);
        // Flutter VIRTUALIZE list -> getDetailRowCount() chi tra ve so item DANG trong a11y tree
        // (~viewport), KHONG bang tong so bai khi folder dai hon 1 man hinh -> KHONG assert rows == hero
        // (fail oan, vd hero=24 nhung chi hien 12 row). Oracle ben vung:
        //   (1) rows > 0            : folder that su co bai
        //   (2) rows <= hero        : so row hien KHONG vuot so bai folder -> neu bai LA lot vao
        //                             (vd hien ca tracks cua artist) thi rows se vuot hero -> bat duoc.
        // hero lay tu chinh app (so bai cua folder) lam chuan.
        Assert.assertTrue(rows > 0, "Folder khong hien bai nao (rows=0)");
        Assert.assertTrue(rows <= hero,
                "So row hien thi (" + rows + ") VUOT so bai folder (" + hero + ") -> co bai la lot vao");
        ExtentReportManager.getTest().log(Status.PASS,
                "Folder hien " + rows + "/" + hero + " bai (virtualized), khong co bai la lot vao.");
    }

    @Test(description = "TC_ART_039: Back tu folder ve Artist Detail")
    public void TC_ART_039_back_to_artist_detail() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        artists.tapDetailBack();
        home.sleep(1300);
        Assert.assertTrue(artists.isArtistDetailOpen(), "Khong ve duoc Artist Detail tu folder");
        ExtentReportManager.getTest().log(Status.PASS, "Back tu folder -> Artist Detail.");
    }

    @Test(description = "TC_ART_040: Shuffle trong folder phat random tu folder")
    public void TC_ART_040_folder_shuffle() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        artists.tapDetailShuffle();
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerDisplayed(), "Shuffle folder khong phat");
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Shuffle folder khong chay");
        ExtentReportManager.getTest().log(Status.PASS, "Shuffle folder -> phat.");
    }

    @Test(description = "TC_ART_041: Folder 3 cham mo sheet 4 action, share theo gioi han 10")
    public void TC_ART_041_folder_sheet_share_limit() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        artists.tapDetailMenu(); home.sleep(1000);
        Assert.assertTrue(artists.areFourActionsDisplayed(), "Sheet folder thieu action");
        Assert.assertTrue(artists.sheetHasNoExtraActions(), "Sheet folder khong duoc co Rename/Delete/File info");
        // MINH CHUNG: sheet folder 4 action dang mo, chup truoc khi tap Share (dieu huong di)
        ExtentReportManager.attachProof("Sheet folder 4 action dang mo - minh chung");
        int n = artists.getSheetSongCount();
        artists.tapSheetShare();
        if (n > 10) {
            home.sleep(1500);
            Assert.assertFalse(artists.isShareSheetOpen(), "Folder " + FOLDER + " >10 van mo resolver");
        } else {
            Assert.assertTrue(artists.waitShareSheetOpen(5000), "Folder " + FOLDER + " <=10 khong mo resolver");
            artists.closeShareSheet();
        }
        ExtentReportManager.getTest().log(Status.PASS,
                "Sheet folder 4 action + share ap dung gioi han 10 (folder " + n + " bai).");
    }

    @Test(description = "TC_ART_042: Mini player hoat dong trong folder")
    public void TC_ART_042_folder_mini_player() {
        HomePage home = new HomePage();
        ArtistsPage artists = goFolder(home);

        artists.tapDetailPlayAll();
        home.sleep(2200);
        Assert.assertTrue(home.isMiniPlayerProgressAdvancing(4000), "Mini player khong chay trong folder");
        Assert.assertTrue(artists.isFolderDetailOpen(FOLDER), "Da roi folder khi phat");
        ExtentReportManager.getTest().log(Status.PASS, "Mini player phat dung trong folder.");
    }
}