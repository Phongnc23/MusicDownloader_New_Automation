package testcases.tracks;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TracksPage;
import report.ExtentReportManager;

import java.util.List;

/**
 * Module: Tracks - Rename / Favorite / File info (TC_TRK_035..041).
 * Luu y: Rename SAVE (038/039) DOI TEN THAT roi KHOI PHUC lai. Sort mac dinh 'Date modified'
 * -> bai vua sua nhay len dau (index 0), dung de khoi phuc.
 */
public class Tracks05_Verify_Rename_Favorite_Info extends BaseTest {

    private TracksPage goTracks(HomePage home) {
        TracksPage tracks = new TracksPage();
        home.tapNavTracks();
        home.waitUntil(tracks::isTracksScreenDisplayed, 6000);
        Assert.assertTrue(tracks.isTracksScreenDisplayed(), "Khong vao duoc man Tracks");
        return tracks;
    }

    private void renameRow(HomePage home, TracksPage tracks, int index, String newName) {
        tracks.openTrackMenu(index); home.sleep(900);
        tracks.tapMenuRename(); home.sleep(900);
        tracks.setRenameText(newName);
        tracks.tapRenameSave(); home.sleep(1300);
    }

    @Test(description = "TC_TRK_035: Rename CANCEL title khong doi")
    public void TC_TRK_035_rename_cancel_unchanged() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String orig = tracks.getFirstTrackTitle();
        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuRename(); home.sleep(900);
        tracks.setRenameText("ZZZ_SHOULD_NOT_SAVE");
        tracks.tapRenameCancel(); home.sleep(1000);
        Assert.assertEquals(tracks.getFirstTrackTitle(), orig, "CANCEL ma title bi doi");
        ExtentReportManager.getTest().log(Status.PASS, "Rename CANCEL: title giu nguyen.");
    }

    @Test(description = "TC_TRK_036: Rename - clear input, char count real-time, nhap tren 60 ky tu bi gioi han")
    public void TC_TRK_036_rename_clear_and_maxlen() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuRename(); home.sleep(900);

        tracks.clearRenameViaX(); home.sleep(600);
        Assert.assertTrue(tracks.getRenameText().isEmpty(), "Nut X khong xoa het input");
        Assert.assertEquals(tracks.getRenameCounter(), "0/60", "Counter khong ve 0/60 sau clear");

        String longStr = "ABCDEFGHIJ".repeat(7); // 70 ky tu
        tracks.setRenameText(longStr); home.sleep(800);
        Assert.assertTrue(tracks.getRenameText().length() <= 60, "Khong gioi han 60 ky tu (len="
                + tracks.getRenameText().length() + ")");
        Assert.assertEquals(tracks.getRenameCounter(), "60/60", "Counter khong dung 60/60 khi vuot gioi han");
        // MINH CHUNG: chup Rename dialog da clear + gioi han 60/60 truoc khi cancel
        ExtentReportManager.attachProof("Rename dialog: da clear va gioi han 60 ky tu - minh chung");
        tracks.tapRenameCancel();
    }

    @Test(description = "TC_TRK_037: Rename SAVE input rong - validation chan")
    public void TC_TRK_037_rename_empty_blocked() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuRename(); home.sleep(900);
        tracks.clearRenameViaX(); home.sleep(600);
        tracks.tapRenameSave(); home.sleep(900);
        Assert.assertTrue(tracks.isRenameDialogOpen(), "SAVE input rong nhung dialog da dong (khong chan)");
        // MINH CHUNG: chup Rename dialog van mo (SAVE rong bi chan) truoc khi cancel
        ExtentReportManager.attachProof("Rename SAVE rong bi chan, dialog van mo - minh chung");
        tracks.tapRenameCancel();
    }

    @Test(description = "TC_TRK_038: Rename SAVE title moi xuat hien trong list (roi khoi phuc)")
    public void TC_TRK_038_rename_save_appears() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String orig = tracks.getFirstTrackTitle();
        String newName = "QA_RENAME_TEST";

        renameRow(home, tracks, 0, newName);
        Assert.assertEquals(tracks.getFirstTrackTitle(), newName,
                "Sau SAVE title moi khong len dau list");
        ExtentReportManager.getTest().log(Status.INFO, "Da doi ten thanh: " + newName);
        // MINH CHUNG: chup list co title moi len dau truoc khi khoi phuc ten cu
        ExtentReportManager.attachProof("Title moi sau rename da len dau list - minh chung");

        // Khoi phuc ten cu (bai vua sua dang o index 0)
        renameRow(home, tracks, 0, orig);
        Assert.assertEquals(tracks.getFirstTrackTitle(), orig, "Khong khoi phuc duoc ten cu");
        ExtentReportManager.getTest().log(Status.PASS, "Rename SAVE hien title moi, da khoi phuc ten cu.");
    }

    @Test(description = "TC_TRK_039: Rename voi tieng Viet va ky tu dac biet (roi khoi phuc)")
    public void TC_TRK_039_rename_vietnamese_special() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        String orig = tracks.getFirstTrackTitle();
        String newName = "Bai Test Tiếng Việt @#$ 1";

        renameRow(home, tracks, 0, newName);
        List<String> titles = tracks.getVisibleTrackTitles();
        Assert.assertTrue(titles.contains(newName) || tracks.getFirstTrackTitle().equals(newName),
                "Khong thay title tieng Viet/dac biet sau SAVE");
        ExtentReportManager.getTest().log(Status.INFO, "Da doi ten thanh: " + newName);
        // MINH CHUNG: chup list co title tieng Viet/dac biet truoc khi khoi phuc
        ExtentReportManager.attachProof("Title tieng Viet/dac biet sau rename - minh chung");

        renameRow(home, tracks, 0, orig);
        Assert.assertEquals(tracks.getFirstTrackTitle(), orig, "Khong khoi phuc duoc ten cu");
        ExtentReportManager.getTest().log(Status.PASS, "Rename tieng Viet + ky tu dac biet OK, da khoi phuc.");
    }

    @Test(description = "TC_TRK_040: Add to My Favorite (lap 2 lan, moi lan sheet dong)")
    public void TC_TRK_040_add_favorite_twice() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(900);
        Assert.assertTrue(tracks.isAddToPlaylistOpen(), "Lan 1: khong mo Add to playlist");
        tracks.tapPlaylistByName("My Favorite"); home.sleep(1100);
        Assert.assertFalse(tracks.isAddToPlaylistOpen(), "Lan 1: sheet khong dong sau khi chon My Favorite");

        tracks.openTrackMenu(1); home.sleep(900);
        tracks.tapMenuAddPlaylist(); home.sleep(900);
        Assert.assertTrue(tracks.isAddToPlaylistOpen(), "Lan 2: khong mo Add to playlist");
        tracks.tapPlaylistByName("My Favorite"); home.sleep(1100);
        Assert.assertFalse(tracks.isAddToPlaylistOpen(), "Lan 2: sheet khong dong sau khi chon My Favorite");

        ExtentReportManager.getTest().log(Status.PASS, "Add to My Favorite 2 lan, moi lan sheet dong.");
    }

    @Test(description = "TC_TRK_041: File info verify tat ca fields co gia tri")
    public void TC_TRK_041_file_info_values() {
        HomePage home = new HomePage();
        TracksPage tracks = goTracks(home);

        tracks.openTrackMenu(0); home.sleep(900);
        tracks.tapMenuFileInfo(); home.sleep(1000);
        Assert.assertTrue(tracks.areAllInfoFieldsDisplayed(), "Thieu field thong tin");
        Assert.assertTrue(tracks.infoHasFilePathValue(), "File path khong co gia tri /storage/...");
        // MINH CHUNG: chup Track information (day du fields + gia tri File path) truoc khi dong
        ExtentReportManager.attachProof("Track information day du fields + gia tri File path - minh chung");
        tracks.closeInfo();
    }
}