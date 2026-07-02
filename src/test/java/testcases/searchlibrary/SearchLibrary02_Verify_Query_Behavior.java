package testcases.searchlibrary;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchInLibraryPage;
import report.ExtentReportManager;

/**
 * Module: Search In Library - Query Behavior (TC_SL_004,007,008,012,013).
 * QUERY_HIT: substring chac chan co ket qua track (sua cho khop thu vien).
 * QUERY_NUM: chuoi so co trong ten file.
 */
public class SearchLibrary02_Verify_Query_Behavior extends BaseTest {

    private static final String QUERY_HIT = "import";   // nhieu track "import_..."
    private static final String QUERY_NUM = "1779";     // so co trong ten file import_1779... (nhieu bai)
    private static final String QUERY_NONE = "ZZZQQQXYW"; // chac chan khong khop

    private SearchInLibraryPage goSearch(HomePage home) {
        SearchInLibraryPage s = new SearchInLibraryPage();
        s.openSearch(home);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Khong mo duoc man Search In Library");
        return s;
    }

    @Test(description = "TC_SL_004: Query rong khong hien ket qua; 1 dau cach hien toan bo")
    public void TC_SL_004_empty_and_space() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        // Rong -> khong list, khong Nothing found
        Assert.assertTrue(s.isQueryEmpty(), "O nhap khong rong luc moi mo");
        Assert.assertTrue(s.isResultAreaEmpty(), "Query rong nhung van hien ket qua/Nothing found");

        // 1 dau cach -> hien toan bo (search theo substring)
        s.typeQuery(" ");
        home.sleep(1200);
        Assert.assertTrue(s.hasAnyResult(), "Nhap 1 dau cach nhung khong hien danh sach");
        ExtentReportManager.getTest().log(Status.PASS, "Rong -> khong list; 1 dau cach -> hien toan bo.");
    }

    @Test(description = "TC_SL_007: Nhap query ket qua hien thi")
    public void TC_SL_007_query_shows_results() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(QUERY_HIT);
        home.sleep(1300);
        Assert.assertTrue(s.hasAnyResult(), "Query '" + QUERY_HIT + "' khong ra ket qua (sua QUERY_HIT)");
        ExtentReportManager.getTest().log(Status.PASS, "Query '" + QUERY_HIT + "' -> "
                + s.getTrackResultCount() + " track result.");
    }

    @Test(description = "TC_SL_008: Xoa query ket qua bien mat")
    public void TC_SL_008_clear_query() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(QUERY_HIT);
        home.sleep(1300);
        Assert.assertTrue(s.hasAnyResult(), "Query khong ra ket qua truoc khi xoa");

        s.clearQueryViaX();
        home.sleep(1200);
        Assert.assertTrue(s.isQueryEmpty(), "Xoa nhung o nhap van con chu");
        Assert.assertTrue(s.isResultAreaEmpty(), "Xoa query nhung ket qua khong bien mat");
        ExtentReportManager.getTest().log(Status.PASS, "Xoa query -> ket qua bien mat.");
    }

    @Test(description = "TC_SL_012: Khong khop hien 'Nothing found!'")
    public void TC_SL_012_nothing_found() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(QUERY_NONE);
        home.sleep(1300);
        Assert.assertTrue(s.isNothingFound(), "Khong khop nhung khong hien 'Nothing found!'");
        Assert.assertFalse(s.hasAnyResult(), "Khong khop nhung van co row ket qua");
        ExtentReportManager.getTest().log(Status.PASS, "Query khong khop -> 'Nothing found!'.");
    }

    @Test(description = "TC_SL_013: Search bang so")
    public void TC_SL_013_search_by_number() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(QUERY_NUM);
        home.sleep(1300);
        Assert.assertTrue(s.hasAnyResult(), "Query so '" + QUERY_NUM + "' khong ra ket qua (sua QUERY_NUM)");
        ExtentReportManager.getTest().log(Status.PASS, "Search so '" + QUERY_NUM + "' -> co ket qua.");
    }
}