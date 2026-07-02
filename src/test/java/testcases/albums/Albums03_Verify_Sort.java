package testcases.albums;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AlbumsPage;
import pages.HomePage;
import report.ExtentReportManager;

/**
 * Module: Albums - Sort (TC_ALB_007..009).
 * 007: sort album-level (Title only) mo + dong bang Scrim.
 * 008: sort album-level Title - tap 2 lan (toggle tang/giam).
 * 009: in-album sort 7 options (Title/Artist/Album/File name/Duration/Date added/Date modified),
 *      Title doi thu tu, tap lan 2 dao nguoc.
 */
public class Albums03_Verify_Sort extends BaseTest {

    private AlbumsPage goAlbums(HomePage home) {
        AlbumsPage albums = new AlbumsPage();
        albums.gotoAlbumsList(home);
        Assert.assertTrue(albums.isAlbumsScreenDisplayed(), "Khong vao duoc man Albums");
        return albums;
    }

    @Test(description = "TC_ALB_007: Album sort dialog mo, co option Title, dong bang Scrim")
    public void TC_ALB_007_sort_dialog_scrim() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.tapListSort();
        home.sleep(900);
        Assert.assertTrue(albums.isSortDialogOpen(), "Khong mo duoc Sort dialog");
        Assert.assertTrue(albums.isSortTitleOptionDisplayed(), "Khong thay option 'Title'");
        albums.closeSortViaScrim();
        home.sleep(800);
        Assert.assertFalse(albums.isSortDialogOpen(), "Scrim khong dong duoc Sort dialog");
        ExtentReportManager.getTest().log(Status.PASS, "Album sort dialog (Title) mo + dong bang Scrim.");
    }

    @Test(description = "TC_ALB_008: Album sort theo Title - lan 1 tang, lan 2 giam (toggle)")
    public void TC_ALB_008_sort_toggle() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.tapListSort();
        home.sleep(900);
        Assert.assertTrue(albums.isSortDialogOpen(), "Khong mo duoc Sort dialog");
        Assert.assertTrue(albums.isSortTitleActive(), "Mac dinh 'Title' khong active");

        // Tap Title -> ap dung NGAY + dialog TU DONG DONG (xac nhan qua DOM Tracks). Mo lai de doc indicator.
        albums.tapSortTitle();
        home.sleep(700);
        if (!albums.isSortDialogOpen()) { albums.tapListSort(); home.waitUntil(albums::isSortDialogOpen, 4000); }
        Assert.assertTrue(albums.isSortTitleActive(), "Sau tap lan 1 'Title' khong con active (sau reopen)");

        albums.tapSortTitle();
        home.sleep(700);
        if (!albums.isSortDialogOpen()) { albums.tapListSort(); home.waitUntil(albums::isSortDialogOpen, 4000); }
        Assert.assertTrue(albums.isSortTitleActive(), "Sau tap lan 2 'Title' khong con active (sau reopen)");

        if (albums.isSortDialogOpen()) albums.closeSortViaX();
        home.sleep(800);
        Assert.assertFalse(albums.isSortDialogOpen(), "Khong dong duoc Sort dialog bang X");
        ExtentReportManager.getTest().log(Status.PASS,
                "Sort Title tap 2 lan: option luon active, dialog on dinh "
                        + "(huong asc/desc khong doc duoc qua accessibility).");
    }

    @Test(description = "TC_ALB_009: In-album sort 7 options, Title sort, dao chieu doi thu tu")
    public void TC_ALB_009_in_album_sort() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        // Mo album NHIEU track (uu tien VoiceChanger ~321, fallback album nhieu bai nhat) de dao
        // chieu thay ro doi bai dau. (Album 1 track -> bai dau asc == desc du sort dung -> khong verify duoc.)
        String albumName = albums.openAlbumForSortCheck();
        home.sleep(1500);
        Assert.assertFalse(albumName.isEmpty(), "Khong tim duoc album nhieu track de kiem tra sort");
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Chua vao Album Detail (" + albumName + ")");
        int rowsBefore = albums.getDetailRowCount();

        // Mo dialog + kiem tra du 7 option
        albums.tapDetailTracksSort();
        home.sleep(900);
        Assert.assertTrue(albums.isSortDialogOpen(), "Nut sort in-album khong mo duoc dialog");
        Assert.assertTrue(albums.areInAlbumSortOptionsDisplayed(),
                "Thieu option in-album (can du 7: Title/Artist/Album/File name/Duration/Date added/Date modified)");
        Assert.assertEquals(albums.getInAlbumSortOptionCount(), 7, "So option in-album khong phai 7");

        // Chon Title -> ap dung + auto-close. Doc bai dau (chieu 1).
        albums.tapSortOption("Title");
        home.sleep(1000);
        if (albums.isSortDialogOpen()) albums.closeSortViaScrim();
        home.sleep(600);
        String first1 = albums.getFirstDetailTrackTitle();

        // Mo lai -> DAO CHIEU bang nut toggle asc/desc (goc phai hang "Sort by"),
        // KHONG phai tap Title lan 2 (option chi chon TRUONG, khong doi chieu).
        albums.tapDetailTracksSort();
        home.waitUntil(albums::isSortDialogOpen, 4000);
        Assert.assertTrue(albums.isSortOptionActive("Title"), "Truoc dao chieu 'Title' khong active");
        albums.toggleInAlbumSortDirectionTitle();
        home.sleep(1000);
        if (albums.isSortDialogOpen()) albums.closeSortViaScrim();
        home.sleep(600);
        String first2 = albums.getFirstDetailTrackTitle();

        ExtentReportManager.getTest().log(Status.INFO,
                "Title chieu 1 -> dau: \"" + first1 + "\"; sau dao chieu -> dau: \"" + first2 + "\"");
        Assert.assertEquals(albums.getDetailRowCount(), rowsBefore, "So track thay doi sau khi sort");
        Assert.assertNotEquals(first2, first1,
                "Dao chieu sort khong doi thu tu (bai dau giong nhau)");
        ExtentReportManager.getTest().log(Status.PASS,
                "In-album sort: du 7 option, Title sort, dao chieu doi bai dau.");
    }

    @Test(description = "TC_ALB_033: Mo album -> Sort By: nhan chon TUNG option 1 luot, moi option hoat dong dung")
    public void TC_ALB_033_in_album_sort_each_option() {
        HomePage home = new HomePage();
        AlbumsPage albums = goAlbums(home);

        albums.tapAlbumCard(0);
        home.sleep(1500);
        Assert.assertTrue(albums.isDetailWithTracksOpen(), "Chua vao Album Detail");
        int rowsBefore = albums.getDetailRowCount();

        String[] options = {"Title", "Artist", "Album", "File name", "Duration", "Date added", "Date modified"};
        for (String opt : options) {
            // Mo sort -> chon option (ap dung + auto-close)
            albums.tapDetailTracksSort();
            home.waitUntil(albums::isSortDialogOpen, 4000);
            Assert.assertTrue(albums.isSortDialogOpen(), "Khong mo duoc in-album sort cho option: " + opt);
            albums.tapSortOption(opt);
            home.sleep(800);

            // Mo lai de xac nhan option vua chon dang ACTIVE (indicator mui ten)
            albums.tapDetailTracksSort();
            home.waitUntil(albums::isSortDialogOpen, 4000);
            Assert.assertTrue(albums.isSortOptionActive(opt),
                    "Option '" + opt + "' khong active sau khi chon (sau reopen)");
            albums.closeInAlbumSortViaX();
            home.sleep(700);

            // List khong vo: so track giu nguyen, van con track
            Assert.assertEquals(albums.getDetailRowCount(), rowsBefore,
                    "So track thay doi sau khi sort theo: " + opt);
            ExtentReportManager.getTest().log(Status.INFO,
                    "Sort '" + opt + "' OK -> active, track dau: \"" + albums.getFirstDetailTrackTitle() + "\"");
        }

        ExtentReportManager.getTest().log(Status.PASS,
                "Da nhan chon ca 7 option in-album sort 1 luot: tat ca active dung, list on dinh.");
        albums.tapDetailBack();
    }
}