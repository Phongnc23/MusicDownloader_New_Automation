package testcases.artists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ArtistsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Artists - Sort By (TC_ART_047).
 * Artist list chi co 1 option sort "Title". Du chi co 1 artist, van phai co case
 * mo Sort By va NHAN 2 LAN (toggle tang/giam).
 *
 * Ghi chu: voi 1 artist, thu tu list bat bien; mui ten chieu sap xep khong co content-desc
 * (khong doc duoc asc/desc qua accessibility) -> verify bang: option Title luon ACTIVE sau moi
 * lan tap + dialog on dinh + list con nguyen. Khi co >1 artist co the bo sung assert dao thu tu.
 */
public class Artists08_Verify_Sort extends BaseTest {

    private ArtistsPage goArtists(HomePage home) {
        ArtistsPage artists = new ArtistsPage();
        home.tapNavArtists();
        home.waitUntil(artists::isArtistsScreenDisplayed, 6000);
        Assert.assertTrue(artists.isArtistsScreenDisplayed(), "Khong vao duoc man Artists");
        return artists;
    }

    @Test(description = "TC_ART_047: Sort By artist - nhan 2 lan (toggle tang/giam)")
    public void TC_ART_047_sort_toggle() {
        HomePage home = new HomePage();
        ArtistsPage artists = goArtists(home);

        artists.tapListSort();
        home.waitUntil(artists::isSortDialogOpen, 4000);
        Assert.assertTrue(artists.isSortDialogOpen(), "Khong mo duoc Sort dialog");
        Assert.assertTrue(artists.isSortTitleOptionDisplayed(), "Khong thay option 'Title'");
        Assert.assertTrue(artists.isSortTitleActive(), "Mac dinh 'Title' khong active (khong co mui ten)");

        // Tap Title -> ap dung + dialog co the TU DONG DONG (giong Tracks). Mo lai de check active da luu.
        artists.tapSortTitle();
        home.sleep(700);
        if (!artists.isSortDialogOpen()) { artists.tapListSort(); home.waitUntil(artists::isSortDialogOpen, 4000); }
        Assert.assertTrue(artists.isSortTitleActive(), "Sau tap lan 1 'Title' khong con active (sau reopen)");

        // Tap lan 2 (toggle chieu nguoc) -> mo lai check active
        artists.tapSortTitle();
        home.sleep(700);
        if (!artists.isSortDialogOpen()) { artists.tapListSort(); home.waitUntil(artists::isSortDialogOpen, 4000); }
        Assert.assertTrue(artists.isSortTitleActive(), "Sau tap lan 2 'Title' khong con active (sau reopen)");

        // MINH CHUNG: Sort dialog voi option 'Title' active sau khi toggle 2 lan, chup truoc khi dong
        ExtentReportManager.attachProof("Sort dialog - option 'Title' active sau toggle 2 lan - minh chung");
        if (artists.isSortDialogOpen()) artists.closeSortViaX();
        home.waitUntil(() -> !artists.isSortDialogOpen(), 3000);
        Assert.assertFalse(artists.isSortDialogOpen(), "Khong dong duoc Sort dialog bang X");
        Assert.assertTrue(artists.getArtistCardCount() >= 1, "List artist bi mat sau khi sort");
        ExtentReportManager.getTest().log(Status.PASS,
                "Sort By 'Title' tap 2 lan: option luon active (check qua reopen), list con nguyen "
                        + "(huong asc/desc khong doc duoc qua accessibility voi 1 artist).");
    }
}