package testcases.search;

import base.SearchOnlineBaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchOnlinePage;
import report.ExtentReportManager;

/**
 * Module: Search Online (Download) - E2E
 * Muc tieu: Search "xao xuyen" -> voi moi bai: NHAN CHON (phat) bai -> tap icon DOWNLOAD tren
 * MINI PLAYER (canh icon pause) -> kiem tra man DOWNLOADED (card Home) tang >= 2.
 *
 * LUU Y:
 *  - Oracle = so bai o man DOWNLOADED (card Home): Downloaded chi phan anh tai online, KHONG dedup
 *    (tai lai cung bai van tang) -> on dinh hon Tracks count (bi test khac them/xoa lam nhieu).
 *  - Tai qua MINI PLAYER: icon download tren ket qua search KHONG kich hoat bang tap Appium; nhung
 *    phat bai roi tap download tren mini player (dung clickGesture ~ adb tap) thi tai duoc on dinh.
 *  - Tai xong hien NGAY tren card Downloaded -> poll ngan.
 */
public class Search04_Verify_E2E_Download_And_Verify extends SearchOnlineBaseTest {

    private static final long DOWNLOAD_POLL_MS = 30000; // tai xong hien NGAY tren card Downloaded

    @Test(description = "TC_DL_017: E2E Search 'xao xuyen' phat + tai 2 bai qua mini player, Downloaded tang >= 2")
    public void TC_DL_017_e2e_download_two() {
        HomePage home = new HomePage();
        SearchOnlinePage search = new SearchOnlinePage();

        // 1) So bai DA TAI (card "Downloaded" tren Home) ban dau
        home.tapNavHome();
        home.waitUntil(home::isHomeDisplayed, 5000);
        int before = home.getDownloadedCount();
        log.info("[TC_DL_017] Downloaded ban dau = {}", before);
        ExtentReportManager.getTest().log(Status.INFO, "So bai Downloaded ban dau: " + before);
        Assert.assertTrue(before >= 0, "Khong doc duoc so bai Downloaded ban dau tren Home");

        // 2) Search "xao xuyen"
        home.tapSearchBar();
        home.waitUntil(search::isSearchScreenDisplayed, 5000);
        search.searchFor("xao xuyen");
        int results = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So ket qua: " + results);
        Assert.assertTrue(results >= 2, "Khong du 2 ket qua de tai E2E");

        // 3) Voi moi bai (0 va 1): NHAN CHON (phat) -> tap DOWNLOAD tren mini player
        for (int i = 0; i < 2; i++) {
            String title = search.getResultTitle(i);
            ExtentReportManager.getTest().log(Status.INFO, "Phat + tai bai " + (i + 1) + ": " + title);
            search.playResult(i);
            home.sleep(2500);              // cho mini player hien bai dang phat
            search.downloadViaMiniPlayer();
            home.sleep(2500);              // cho lenh tai duoc nhan
        }

        // 4) Ve Home -> poll so bai o man Downloaded (card Home) tang >= 2
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home sau khi tai");

        int after = before;
        long deadline = System.currentTimeMillis() + DOWNLOAD_POLL_MS;
        while (System.currentTimeMillis() < deadline) {
            after = home.getDownloadedCount();
            log.info("[TC_DL_017] poll Downloaded = {} (can >= {})", after, before + 2);
            if (after >= before + 2) break;
            home.sleep(3000);
        }
        ExtentReportManager.getTest().log(Status.INFO,
                "So bai Downloaded sau khi tai: " + after + " (truoc: " + before + ")");
        Assert.assertTrue(after >= before + 2,
                "Sau khi tai 2 bai, man Downloaded khong tang du 2 (truoc=" + before + ", sau=" + after + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "E2E: phat + tai 2 bai 'xao xuyen' qua mini player thanh cong (Downloaded +" + (after - before) + ").");
    }
}