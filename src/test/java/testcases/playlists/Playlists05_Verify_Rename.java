package testcases.playlists;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PlaylistsPage;
import report.ExtentReportManager;

/**
 * Module: Playlists - Rename (TC_PL_017..018).
 * Tu tao playlist tam de thao tac (an toan, chay lai duoc); cleanup XOA THAT.
 * Rename dialog dung lai EditText + CANCEL/SAVE (DOM xac nhan: prefill ten cu, hint "Title").
 */
public class Playlists05_Verify_Rename extends BaseTest {

    private PlaylistsPage goPlaylists(HomePage home) {
        PlaylistsPage pl = new PlaylistsPage();
        pl.gotoPlaylistsList(home);
        Assert.assertTrue(pl.isPlaylistsScreenDisplayed(), "Khong vao duoc man Playlists");
        return pl;
    }

    private String tempName(String prefix) { return prefix + (System.currentTimeMillis() % 100000); }

    @Test(description = "TC_PL_017: Rename - dialog prefill ten cu, CANCEL giu nguyen")
    public void TC_PL_017_rename_cancel_prefilled() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        String name = tempName("QA_REN_");
        pl.createPlaylistFlow(name);
        Assert.assertTrue(pl.isPlaylistListed(name), "Khong tao duoc playlist tam");

        pl.openPlaylistMenu(name);
        home.sleep(900);
        Assert.assertTrue(pl.isUserPlaylistSheetOpen(), "Khong mo duoc user sheet");
        pl.tapSheetRename();
        home.sleep(900);
        Assert.assertTrue(pl.isCreateDialogOpen(), "Khong mo duoc Rename dialog");
        Assert.assertEquals(pl.getNameFieldText(), name, "Rename dialog khong prefill ten cu");

        pl.tapDialogCancel();
        home.sleep(900);
        Assert.assertTrue(pl.isPlaylistListed(name), "CANCEL nhung ten bi doi/mat");
        ExtentReportManager.getTest().log(Status.INFO, "Rename prefill OK: " + name + " (CANCEL giu nguyen)");

        // Cleanup XOA THAT
        boolean deleted = pl.deletePlaylistReal(name);
        Assert.assertTrue(deleted, "Cleanup: khong xoa duoc playlist tam");
        ExtentReportManager.getTest().log(Status.PASS, "Rename CANCEL giu nguyen ten + cleanup OK.");
    }

    @Test(description = "TC_PL_018: Rename - SAVE doi ten")
    public void TC_PL_018_rename_save_changes_name() {
        HomePage home = new HomePage();
        PlaylistsPage pl = goPlaylists(home);

        // src/dst KHONG la substring cua nhau de assert chinh xac
        long ts = System.currentTimeMillis() % 100000;
        String src = "QA_OLD_" + ts;
        String dst = "QA_NEW_" + ts;

        pl.createPlaylistFlow(src);
        Assert.assertTrue(pl.isPlaylistListed(src), "Khong tao duoc playlist tam");

        pl.openPlaylistMenu(src);
        home.sleep(900);
        pl.tapSheetRename();
        home.sleep(900);
        Assert.assertTrue(pl.isCreateDialogOpen(), "Khong mo duoc Rename dialog");
        pl.typePlaylistName(dst);   // sendKeys tu clear ten cu
        home.sleep(500);
        pl.tapDialogSave();
        home.sleep(1300);

        Assert.assertTrue(pl.isPlaylistListed(dst), "SAVE nhung khong thay ten moi");
        Assert.assertFalse(pl.isPlaylistListed(src), "SAVE nhung ten cu van con");
        ExtentReportManager.getTest().log(Status.INFO, "Rename: " + src + " -> " + dst);

        // Cleanup XOA THAT (ten moi)
        boolean deleted = pl.deletePlaylistReal(dst);
        Assert.assertTrue(deleted, "Cleanup: khong xoa duoc playlist da rename");
        ExtentReportManager.getTest().log(Status.PASS, "Rename SAVE doi ten thanh cong + cleanup OK.");
    }
}