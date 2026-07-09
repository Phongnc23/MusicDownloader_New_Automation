package testcases.menu;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MenuPage;
import report.ExtentReportManager;

/**
 * Module: Menu (Drawer) - Sleep Timer
 * Muc tieu: Kiem tra dialog Sleep timer: INITIAL / ACTIVE / Custom.
 *
 * Mo dialog qua CARD Sleep timer o Home (home.tapSleepTimer) - cung dialog voi drawer,
 * don gian hon (khong can mo drawer).
 *
 * QUAN TRONG ve trang thai: timer da set CO THE ton tai qua phien (noReset). Vi vay:
 *  - openSleepInitial() goi resetIfActive() de don timer cu -> luon bat dau o INITIAL.
 *  - Moi test set timer deu RESET truoc khi ket thuc (resetAndClose) de khong ro ri sang test khac.
 */
public class Menu03_Verify_Sleep_Timer extends BaseTest {

    /** Mo dialog Sleep timer va dam bao ve INITIAL (don timer cu neu con). */
    private void openSleepInitial(HomePage home, MenuPage menu) {
        home.tapSleepTimer();
        // Cho dialog mo (cold start lan dau co the cham hon sleep co dinh) -> waitUntil
        home.waitUntil(menu::isSleepTimerDialogOpen, 5000);
        menu.resetIfActive();
        home.waitUntil(menu::isSleepTimerInitial, 3000);
    }

    private void reopenSleep(HomePage home, MenuPage menu) {
        home.tapSleepTimer();
        home.waitUntil(menu::isSleepTimerDialogOpen, 5000);
    }

    /** Set timer 15 phut (ket thuc: dialog dong, timer chay). */
    private void setTimer15(HomePage home, MenuPage menu) {
        openSleepInitial(home, menu);
        menu.selectPreset15();
        home.sleep(400);
        menu.tapSetTimer();
        home.waitUntil(() -> !menu.isSleepTimerDialogOpen(), 3000);
    }

    /** Don dep khi dialog DANG mo: reset timer (neu co) + dong dialog. */
    private void resetAndClose(HomePage home, MenuPage menu) {
        menu.resetIfActive();
        if (menu.isSleepTimerDialogOpen()) {
            menu.tapCancelSleepDialog();
            home.sleep(600);
        }
    }

    @Test(description = "TC_MENU_012: Sleep timer dialog mo o INITIAL (6 option + Set timer, chua countdown)")
    public void TC_MENU_012_initial_state() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSleepInitial(home, menu);
        Assert.assertTrue(menu.isSleepTimerInitial(),
                "Dialog khong o INITIAL (thieu option/Set timer)");
        Assert.assertFalse(menu.isSleepTimerActive(), "INITIAL ma da co countdown/Reset");
        // MINH CHUNG: chup dialog Sleep timer o INITIAL NGAY luc nay, truoc khi Cancel dong
        ExtentReportManager.attachProof("Sleep timer dialog o INITIAL (6 option + Set timer) - minh chung");

        menu.tapCancelSleepDialog();
    }

    @Test(description = "TC_MENU_013: Cancel o INITIAL dong dialog, khong set timer")
    public void TC_MENU_013_cancel_initial() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSleepInitial(home, menu);
        Assert.assertTrue(menu.isSleepTimerInitial(), "Truoc dieu kien: chua o INITIAL");

        menu.tapCancelSleepDialog();
        home.sleep(800);
        Assert.assertFalse(menu.isSleepTimerDialogOpen(), "Dialog khong dong sau Cancel");
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve Home sau Cancel");
        ExtentReportManager.getTest().log(Status.PASS, "Cancel dong dialog, khong set timer.");
    }

    @Test(description = "TC_MENU_014: Chon 15 phut + Set timer dong dialog va kich hoat timer")
    public void TC_MENU_014_set_15_min() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        setTimer15(home, menu);
        Assert.assertFalse(menu.isSleepTimerDialogOpen(), "Dialog khong dong sau Set timer");
        ExtentReportManager.getTest().log(Status.INFO, "Dialog da dong, kiem tra timer da kich hoat...");

        reopenSleep(home, menu);
        Assert.assertTrue(menu.isSleepTimerActive(), "Timer khong duoc kich hoat (khong thay ACTIVE)");
        // MINH CHUNG: chup dialog ACTIVE (timer 15 phut dang chay) NGAY luc nay, truoc khi reset+dong
        ExtentReportManager.attachProof("Set timer 15 phut OK, timer dang chay (ACTIVE) - minh chung");

        resetAndClose(home, menu);
    }

    @Test(description = "TC_MENU_015: Trang thai ACTIVE hien countdown + nut Reset")
    public void TC_MENU_015_active_countdown() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        setTimer15(home, menu);
        reopenSleep(home, menu);
        Assert.assertTrue(menu.isSleepTimerActive(), "Khong o trang thai ACTIVE");
        String timer = menu.getActiveTimerText();
        ExtentReportManager.getTest().log(Status.INFO, "Countdown: " + timer);
        Assert.assertTrue(timer.contains("Timer:"), "Khong hien countdown 'Timer: Xm Ys'");
        // MINH CHUNG: chup trang thai ACTIVE (countdown + nut Reset) NGAY luc nay, truoc khi reset+dong
        ExtentReportManager.attachProof("Trang thai ACTIVE hien countdown + nut Reset - minh chung");

        resetAndClose(home, menu);
    }

    @Test(description = "TC_MENU_016: Reset o ACTIVE -> dialog van mo, ve INITIAL")
    public void TC_MENU_016_reset_active() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        setTimer15(home, menu);
        reopenSleep(home, menu);
        Assert.assertTrue(menu.isSleepTimerActive(), "Truoc dieu kien: chua ACTIVE");

        menu.tapReset();
        home.sleep(800);
        Assert.assertTrue(menu.isSleepTimerDialogOpen(), "Dialog dong sau Reset (phai van mo)");
        Assert.assertTrue(menu.isSleepTimerInitial(), "Sau Reset khong ve INITIAL");
        Assert.assertFalse(menu.isSleepTimerActive(), "Sau Reset van con countdown/Reset");
        // MINH CHUNG: chup dialog van mo va da ve INITIAL sau Reset, truoc khi Cancel dong
        ExtentReportManager.attachProof("Reset o ACTIVE -> dialog van mo, ve INITIAL - minh chung");

        menu.tapCancelSleepDialog();
    }

    @Test(description = "TC_MENU_017: Click Custom mo Custom sleep timer dialog")
    public void TC_MENU_017_open_custom() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSleepInitial(home, menu);
        menu.tapCustom();
        home.sleep(1000);
        Assert.assertTrue(menu.isCustomDialogOpen(), "Khong mo Custom sleep timer dialog");
        // MINH CHUNG: chup Custom sleep timer dialog NGAY luc nay, truoc khi Cancel+dong
        ExtentReportManager.attachProof("Da mo Custom sleep timer dialog - minh chung");

        menu.tapCustomCancel();
        home.sleep(600);
        resetAndClose(home, menu);
    }

    @Test(description = "TC_MENU_018: Cancel trong Custom dong Custom, quay lai Sleep timer INITIAL")
    public void TC_MENU_018_cancel_custom() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSleepInitial(home, menu);
        menu.tapCustom();
        home.sleep(1000);
        Assert.assertTrue(menu.isCustomDialogOpen(), "Truoc dieu kien: Custom chua mo");

        menu.tapCustomCancel();
        home.sleep(800);
        Assert.assertFalse(menu.isCustomDialogOpen(), "Custom dialog khong dong sau Cancel");
        Assert.assertTrue(menu.isSleepTimerInitial(), "Khong quay lai Sleep timer INITIAL");
        // MINH CHUNG: chup da quay lai Sleep timer INITIAL sau khi Cancel Custom, truoc khi dong
        ExtentReportManager.attachProof("Cancel Custom -> ve Sleep timer INITIAL - minh chung");

        menu.tapCancelSleepDialog();
    }

    @Test(description = "TC_MENU_019: Nhap 25 + Done -> ca 2 dialog dong, set custom timer")
    public void TC_MENU_019_custom_25() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSleepInitial(home, menu);
        menu.tapCustom();
        home.sleep(1000);
        Assert.assertTrue(menu.isCustomDialogOpen(), "Custom chua mo de nhap");

        menu.enterCustomMinutes("25");
        home.sleep(500);
        // KHONG hideKeyboard: hideKeyboard gui BACK -> DONG luon Custom dialog (mat input).
        // Nut Done o y=1149 nam TREN ban phim nen tap truc tiep duoc.
        menu.tapCustomDone();
        home.sleep(1200);

        Assert.assertFalse(menu.isCustomDialogOpen(), "Custom dialog chua dong");
        Assert.assertFalse(menu.isSleepTimerDialogOpen(), "Sleep timer dialog chua dong");
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve Home sau Done");
        ExtentReportManager.getTest().log(Status.INFO, "Ca 2 dialog dong, kiem tra custom timer da set...");

        reopenSleep(home, menu);
        Assert.assertTrue(menu.isSleepTimerActive(), "Custom timer 25 phut khong duoc set");
        // MINH CHUNG: chup dialog ACTIVE (custom 25 phut da set) NGAY luc nay, truoc khi reset+dong
        ExtentReportManager.attachProof("Custom timer 25 phut da set (ACTIVE) - minh chung");

        resetAndClose(home, menu);
    }

    @Test(description = "TC_MENU_020: Nhap 5 + Done -> set custom value nho thanh cong")
    public void TC_MENU_020_custom_5() {
        HomePage home = new HomePage();
        MenuPage menu = new MenuPage();

        openSleepInitial(home, menu);
        menu.tapCustom();
        home.sleep(1000);
        Assert.assertTrue(menu.isCustomDialogOpen(), "Custom chua mo de nhap");

        menu.enterCustomMinutes("5");
        home.sleep(500);
        // KHONG hideKeyboard (gui BACK -> dong Custom dialog). Done click theo element.
        menu.tapCustomDone();
        home.sleep(1200);

        Assert.assertFalse(menu.isCustomDialogOpen(), "Custom dialog chua dong");
        reopenSleep(home, menu);
        Assert.assertTrue(menu.isSleepTimerActive(), "Custom timer 5 phut khong duoc set");
        // MINH CHUNG: chup dialog ACTIVE (custom 5 phut da set) NGAY luc nay, truoc khi reset+dong
        ExtentReportManager.attachProof("Custom timer 5 phut da set (ACTIVE) - minh chung");

        resetAndClose(home, menu);
    }
}