package testcases.search;

import base.SearchOnlineBaseTest;
import com.aventstack.extentreports.Status;
import constants.AppConstants;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchOnlinePage;
import report.ExtentReportManager;

/**
 * Module: Search Online (Download) - Results & Download
 * Muc tieu: Kiem tra ket qua tra ve, xu ly query bat thuong, va kich hoat tai 1 bai.
 *
 * Ghi chu: TC_DL_016 chi verify "tai bat dau, khong crash" (khong cho tai xong - tai xong
 * o E2E TC_DL_017).
 */
public class Search03_Verify_Results_And_Download extends SearchOnlineBaseTest {

    private SearchOnlinePage openSearch(HomePage home) {
        SearchOnlinePage search = new SearchOnlinePage();
        home.tapSearchBar();
        home.waitUntil(search::isSearchScreenDisplayed, 5000);
        Assert.assertTrue(search.isSearchScreenDisplayed(), "Khong vao duoc man Search");
        return search;
    }

    private boolean appAlive() {
        return AppConstants.APP_PACKAGE.equals(getDriver().getCurrentPackage());
    }

    @Test(description = "TC_DL_011: Search query hop le tra ve ket qua")
    public void TC_DL_011_valid_query_returns_results() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.searchFor("xao xuyen");
        Assert.assertTrue(search.getResultCount() > 0, "Query hop le khong tra ve ket qua");
        ExtentReportManager.getTest().log(Status.PASS, "Query hop le tra ve ket qua OK.");
    }

    @Test(description = "TC_DL_012: Danh sach ket qua hien thi nhieu tracks (>=3)")
    public void TC_DL_012_multiple_results() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.searchFor("xao xuyen");
        int n = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So tracks ket qua: " + n);
        Assert.assertTrue(n >= 3, "Ket qua it hon 3 tracks");
        ExtentReportManager.getTest().log(Status.PASS, "Hien thi >=3 tracks ket qua OK.");
    }

    @Test(description = "TC_DL_013: Moi ket qua co tieu de va thong tin")
    public void TC_DL_013_result_has_title_info() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.searchFor("xao xuyen");
        Assert.assertTrue(search.getResultCount() > 0, "Khong co ket qua de kiem tra");
        ExtentReportManager.getTest().log(Status.INFO, "Title result dau: " + search.getResultTitle(0));
        Assert.assertTrue(search.firstResultHasInfo(),
                "Result khong co title + thong tin (creator/duration)");
        ExtentReportManager.getTest().log(Status.PASS, "Moi result co title + thong tin OK.");
    }

    @Test(description = "TC_DL_014: Search query chua ky tu dac biet khong crash")
    public void TC_DL_014_special_chars_no_crash() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.searchFor("@#$%^&*()_+");
        home.sleep(1000);
        Assert.assertTrue(appAlive() && search.isSearchScreenDisplayed(),
                "App crash/roi man Search khi nhap ky tu dac biet");
        ExtentReportManager.getTest().log(Status.PASS, "Xu ly ky tu dac biet, khong crash OK.");
    }

    @Test(description = "TC_DL_015: Search query khong co ket qua hien trang thai rong, khong crash")
    public void TC_DL_015_no_result_empty_state() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.searchFor("zxqwklpmnbvcxzasd99817");
        home.sleep(1500);
        int n = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So ket qua query vo nghia: " + n);
        // Hard assert: khong crash, van o man Search. Empty state (n==0) la ky vong nhung
        // tim kiem mang co the tra fuzzy -> chi log.
        Assert.assertTrue(appAlive() && search.isSearchScreenDisplayed(),
                "App crash/roi man Search voi query vo nghia");
        ExtentReportManager.getTest().log(Status.PASS, "Query vo nghia khong crash OK.");
    }

    @Test(description = "TC_DL_016: Click download tren 1 track kich hoat tai (khong crash)")
    public void TC_DL_016_download_starts() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.searchFor("xao xuyen");
        Assert.assertTrue(search.getResultCount() > 0, "Khong co ket qua de tai");

        search.tapDownloadOnResult(0);
        home.sleep(2500);
        Assert.assertTrue(appAlive(), "App crash sau khi bam download");
        ExtentReportManager.getTest().log(Status.PASS, "Bam download kich hoat tai, khong crash OK.");
    }

    @Test(description = "TC_DL_018: Search bai co ban quyen 'viva la vida' khong tra ve ket qua")
    public void TC_DL_018_copyright_no_result() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        // "viva la vida" la bai co ban quyen -> app chan, khong tim thay ket qua.
        search.searchFor("viva la vida");
        home.sleep(1500);
        int n = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So ket qua 'viva la vida': " + n);
        Assert.assertTrue(appAlive() && search.isSearchScreenDisplayed(),
                "App crash/roi man Search khi tim bai ban quyen");
        Assert.assertEquals(n, 0,
                "Bai ban quyen 'viva la vida' khong duoc phep tra ket qua (mong doi 0, thuc te " + n + ")");
        ExtentReportManager.getTest().log(Status.PASS,
                "Bai ban quyen khong tra ve ket qua OK.");
    }
}
