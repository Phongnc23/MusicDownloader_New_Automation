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
 * Muc tieu: Search -> tai 2 bai -> kiem tra so bai trong thu vien (Tracks) tang >= 2.
 *
 * LUU Y: case E2E PHU THUOC MANG va tai that -> CHAM (poll toi 90s cho tai xong). Neu mang
 * yeu / 2 result trung bai (dedup) co the can chinh query/timeout. Tai xong bai moi len DAU
 * danh sach Tracks va so "N tracks" tang.
 */
public class Search04_Verify_E2E_Download_And_Verify extends SearchOnlineBaseTest {

    private static final long DOWNLOAD_POLL_MS = 90000; // toi da cho 2 bai tai xong

    @Test(description = "TC_DL_017: E2E Search tai 2 bai ve thu vien va kiem tra ca 2")
    public void TC_DL_017_e2e_download_two() {
        HomePage home = new HomePage();
        SearchOnlinePage search = new SearchOnlinePage();

        // 1) So bai Tracks ban dau
        home.tapNavTracks();
        home.waitUntil(() -> search.getLibraryTrackCount() >= 0, 8000);
        int before = search.getLibraryTrackCount();
        ExtentReportManager.getTest().log(Status.INFO, "So bai Tracks ban dau: " + before);
        Assert.assertTrue(before >= 0, "Khong doc duoc so bai Tracks ban dau");
        home.tapNavHome();
        home.sleep(1000);

        // 2) Search + tai 2 bai dau
        home.tapSearchBar();
        home.waitUntil(search::isSearchScreenDisplayed, 5000);
        search.searchFor("xao xuyen");
        int results = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So ket qua: " + results);
        Assert.assertTrue(results >= 2, "Khong du 2 ket qua de tai E2E");

        String t0 = search.getResultTitle(0);
        String t1 = search.getResultTitle(1);
        ExtentReportManager.getTest().log(Status.INFO, "Tai bai 1: " + t0);
        search.tapDownloadOnResult(0);
        home.sleep(2500);
        ExtentReportManager.getTest().log(Status.INFO, "Tai bai 2: " + t1);
        search.tapDownloadOnResult(1);
        home.sleep(2500);

        // 3) Ve Home -> Tracks -> poll so bai tang >= 2
        home.pressBackToHome();
        Assert.assertTrue(home.isHomeDisplayed(), "Khong ve duoc Home sau khi tai");
        home.tapNavTracks();
        home.sleep(1500);

        int after = before;
        long deadline = System.currentTimeMillis() + DOWNLOAD_POLL_MS;
        while (System.currentTimeMillis() < deadline) {
            after = search.getLibraryTrackCount();
            if (after >= before + 2) break;
            home.sleep(3000);
        }
        ExtentReportManager.getTest().log(Status.INFO,
                "So bai Tracks sau khi tai: " + after + " (truoc: " + before + ")");
        Assert.assertTrue(after >= before + 2,
                "Sau khi tai 2 bai, so bai thu vien khong tang du 2 (truoc=" + before + ", sau=" + after + ")");
        ExtentReportManager.getTest().log(Status.PASS, "E2E: tai 2 bai ve thu vien thanh cong.");
    }
}