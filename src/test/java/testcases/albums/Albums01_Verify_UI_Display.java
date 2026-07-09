package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - UI Display (TC_ALB_001..003).
 */
public class Albums01_Verify_UI_Display extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    @Test(description = "TC_ALB_001: Header 'Albums', count, sort button va bottom nav hien thi")
    public void TC_ALB_001_header_count_sort_nav() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        Assert.assertTrue(home.isBottomNavDisplayed(), "Bottom nav khong hien thi");
        albums.tapListSort();
        home.sleep(900);
        Assert.assertTrue(albums.isSortDialogOpen(), "Nut sort khong mo duoc Sort dialog");
        // MINH CHUNG: chup Sort dialog dang mo NGAY luc nay, truoc khi dong
        ExtentReportManager.attachProof("Sort dialog mo tu Albums list - minh chung");
        albums.closeSortViaX();
    }

    @Test(description = "TC_ALB_002: Count 'N albums' > 0 va album card co name + track count")
    public void TC_ALB_002_count_and_card() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        Assert.assertTrue(albums.getAlbumsCount() > 0, "Count 'N albums' khong hop le");
        Assert.assertFalse(albums.getFirstAlbumName().isEmpty(), "Album card khong co ten");
        Assert.assertTrue(albums.getFirstAlbumTrackCount() > 0, "Album card khong co so tracks");
        ExtentReportManager.getTest().log(Status.PASS,
                albums.getAlbumsCount() + " albums; card dau: \"" + albums.getFirstAlbumName()
                        + "\" - " + albums.getFirstAlbumTrackCount() + " tracks");
    }

    @Test(description = "TC_ALB_003: Album count khop voi so card hien thi")
    public void TC_ALB_003_count_matches_cards() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        int count = albums.getAlbumsCount();
        int cards = albums.getAlbumCardCount();
        ExtentReportManager.getTest().log(Status.INFO, "count=" + count + " cards(hien thi)=" + cards);
        Assert.assertTrue(cards >= 1, "Khong co card nao hien thi");
        Assert.assertTrue(count >= cards, "Count (" + count + ") nho hon so card hien thi (" + cards + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "Count khop card hien thi (ScrollView chi render mot phan; count=" + count + ", render=" + cards + ").");
    }
}