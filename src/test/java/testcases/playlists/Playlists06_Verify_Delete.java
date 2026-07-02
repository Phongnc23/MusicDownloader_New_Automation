package testcases.playlists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PlaylistsPage;
import report.ExtentReportManager;

/**
 * Module: Playlists - Delete (TC_PL_019..020). XOA THAT de kiem chung chuc nang.
 * Tu tao playlist tam de thao tac (chay lai duoc, khong dung fixture co dinh).
 * Delete confirm dialog (DOM xac nhan): "Do you want to delete the ?" + CANCEL + DELETE.
 */
public class Playlists06_Verify_Delete extends BaseTest {

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    private String tempName(String prefix) { return prefix + (System.currentTimeMillis() % 100000); }

    @Test(description = "TC_PL_019: Delete - CANCEL count khong doi")
    public void TC_PL_019_delete_cancel() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        String name = tempName("QA_DCAN_");
        pl.createPlaylistFlow(name);
        int afterCreate = pl.getUserPlaylistCount();
        Assert.assertTrue(pl.isPlaylistListed(name), "Khong tao duoc playlist tam");

        pl.openPlaylistMenu(name);
        home.sleep(900);
        pl.tapSheetDelete();
        home.sleep(900);
        Assert.assertTrue(pl.isConfirmDialogOpen(), "Khong hien dialog xac nhan xoa");
        pl.tapConfirmCancel();
        home.sleep(1000);

        Assert.assertEquals(pl.getUserPlaylistCount(), afterCreate, "CANCEL nhung count doi");
        Assert.assertTrue(pl.isPlaylistListed(name), "CANCEL nhung playlist bi xoa");
        ExtentReportManager.getTest().log(Status.INFO, "Delete CANCEL giu nguyen (count=" + afterCreate + ")");

        // Cleanup XOA THAT
        boolean deleted = pl.deletePlaylistReal(name);
        Assert.assertTrue(deleted, "Cleanup: khong xoa duoc playlist tam");
        ExtentReportManager.getTest().log(Status.PASS, "Delete CANCEL khong xoa + cleanup OK.");
    }

    @Test(description = "TC_PL_020: Delete - CONFIRM count giam con N-1 (XOA THAT)")
    public void TC_PL_020_delete_confirm() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        int before = pl.getUserPlaylistCount();
        String name = tempName("QA_DEL_");
        pl.createPlaylistFlow(name);
        int afterCreate = pl.getUserPlaylistCount();
        Assert.assertEquals(afterCreate, before + 1, "Tao tam khong tang count");
        Assert.assertTrue(pl.isPlaylistListed(name), "Khong thay playlist tam");

        pl.openPlaylistMenu(name);
        home.sleep(900);
        pl.tapSheetDelete();
        home.sleep(900);
        Assert.assertTrue(pl.isConfirmDialogOpen(), "Khong hien dialog xac nhan xoa");
        boolean confirmed = pl.tapConfirmAccept();   // bam DELETE that
        home.sleep(1300);

        Assert.assertTrue(confirmed, "Khong bam duoc nut DELETE");
        Assert.assertFalse(pl.isPlaylistListed(name), "Da CONFIRM nhung playlist van con");
        Assert.assertEquals(pl.getUserPlaylistCount(), afterCreate - 1, "Count khong giam sau khi xoa");
        Assert.assertEquals(pl.getUserPlaylistCount(), before, "Count khong tro ve gia tri ban dau");
        ExtentReportManager.getTest().log(Status.PASS,
                "Delete CONFIRM xoa that: count " + afterCreate + " -> " + (afterCreate - 1) + ".");
    }
}