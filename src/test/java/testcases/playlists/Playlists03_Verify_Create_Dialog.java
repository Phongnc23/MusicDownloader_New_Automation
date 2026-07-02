package testcases.playlists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PlaylistsPage;
import report.ExtentReportManager;

/**
 * Module: Playlists - Create Dialog (TC_PL_008..011).
 * TC_PL_010 tao playlist that roi XOA THAT de kiem chung create + delete.
 */
public class Playlists03_Verify_Create_Dialog extends BaseTest {

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    private String tempName(String prefix) { return prefix + (System.currentTimeMillis() % 100000); }

    @Test(description = "TC_PL_008: Dialog mo, rong '0/60', char count update, Dismiss dong")
    public void TC_PL_008_dialog_open_counter_dismiss() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        pl.tapCreateNewPlaylist();
        home.sleep(900);
        Assert.assertTrue(pl.isCreateDialogOpen(), "Khong mo duoc Create dialog");
        Assert.assertTrue(pl.isCharCounterZero(), "Counter khong phai '0/60' (dang: " + pl.getCharCounter() + ")");

        pl.typePlaylistName("ABC");
        home.sleep(700);
        Assert.assertFalse(pl.isCharCounterZero(), "Counter khong cap nhat sau khi nhap");
        ExtentReportManager.getTest().log(Status.INFO, "Counter sau khi nhap 'ABC': " + pl.getCharCounter());

        pl.dismissDialog();
        home.sleep(900);
        Assert.assertFalse(pl.isCreateDialogOpen(), "Dismiss khong dong duoc dialog");
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong ve duoc man Playlists");
        ExtentReportManager.getTest().log(Status.PASS, "Dialog mo + counter '0/60' + update + Dismiss dong OK.");
    }

    @Test(description = "TC_PL_009: CANCEL khong tao, count khong doi")
    public void TC_PL_009_cancel_no_create() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        int before = pl.getUserPlaylistCount();
        String name = tempName("QA_CANCEL_");
        pl.tapCreateNewPlaylist();
        home.sleep(900);
        pl.typePlaylistName(name);
        home.sleep(500);
        pl.tapDialogCancel();
        home.sleep(1000);

        Assert.assertEquals(pl.getUserPlaylistCount(), before, "CANCEL nhung count van doi");
        Assert.assertFalse(pl.isPlaylistListed(name), "CANCEL nhung playlist van duoc tao");
        ExtentReportManager.getTest().log(Status.PASS, "CANCEL khong tao playlist (count=" + before + " giu nguyen).");
    }

    @Test(description = "TC_PL_010: SAVE tao playlist moi (kem cleanup XOA THAT)")
    public void TC_PL_010_save_create_then_delete() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        int before = pl.getUserPlaylistCount();
        String name = tempName("QA_AUTO_");

        pl.createPlaylistFlow(name);
        Assert.assertTrue(pl.isPlaylistListed(name), "SAVE nhung khong thay playlist moi");
        Assert.assertEquals(pl.getUserPlaylistCount(), before + 1, "Count khong tang sau khi tao");
        ExtentReportManager.getTest().log(Status.INFO, "Da tao playlist: " + name + " (count " + before + "->" + (before + 1) + ")");

        // Cleanup: XOA THAT playlist vua tao
        boolean deleted = pl.deletePlaylistReal(name);
        home.sleep(600);
        Assert.assertTrue(deleted, "Khong bam duoc nut DELETE confirm");
        Assert.assertFalse(pl.isPlaylistListed(name), "Sau khi xoa playlist van con");
        Assert.assertEquals(pl.getUserPlaylistCount(), before, "Count khong tro ve sau khi xoa");
        ExtentReportManager.getTest().log(Status.PASS, "SAVE tao playlist + cleanup XOA THAT thanh cong.");
    }

    @Test(description = "TC_PL_011: SAVE voi ten rong - validation (khong tao)")
    public void TC_PL_011_save_empty_validation() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        int before = pl.getUserPlaylistCount();
        pl.tapCreateNewPlaylist();
        home.sleep(900);
        Assert.assertTrue(pl.isCreateDialogOpen(), "Khong mo duoc Create dialog");

        pl.tapDialogSave();   // SAVE ngay khi ten rong
        home.sleep(1000);

        // Dialog co the van mo (validation chan) -> dong truoc khi doc count (dialog mo che label list).
        boolean blocked = pl.isCreateDialogOpen();
        if (blocked) {
            ExtentReportManager.getTest().log(Status.INFO, "Dialog van mo sau SAVE rong (chan dung).");
            pl.dismissDialog();
            home.sleep(800);
        }

        // Validation: khong duoc tao playlist ten rong -> count khong doi
        Assert.assertEquals(pl.getUserPlaylistCount(), before, "Ten rong nhung van tao playlist");
        ExtentReportManager.getTest().log(Status.PASS, "SAVE ten rong khong tao playlist (validation OK).");
    }
}