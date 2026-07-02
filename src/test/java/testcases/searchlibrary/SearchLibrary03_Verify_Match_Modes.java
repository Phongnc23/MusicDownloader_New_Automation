package testcases.searchlibrary;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchInLibraryPage;
import report.ExtentReportManager;

/**
 * Module: Search In Library - Match Modes (TC_SL_009,010,011).
 * Dua tren nhom track "import_..." (thu vien co RAT NHIEU, on dinh - da xac nhan qua TC_SL_007).
 * "Blank Space" cu khong con tren may -> chuyen sang data "import".
 * FUZZY="import" (substring), EXACT="import_" (day du hon), EXPECT="import" (co trong content-desc),
 * LOWER="IMPORT" (CHU HOA - go khac case de test case-insensitive van khop "import_...").
 */
public class SearchLibrary03_Verify_Match_Modes extends BaseTest {

    private static final String FUZZY_QUERY = "import";       // substring ngan
    private static final String EXACT_QUERY = "import_";      // chuoi day du hon
    private static final String EXPECT_NAME = "import";       // ten xuat hien trong content-desc
    private static final String LOWER_QUERY = "IMPORT";       // CHU HOA de test case-insensitive

    private SearchInLibraryPage goSearch(HomePage home) {
        SearchInLibraryPage s = new SearchInLibraryPage();
        s.openSearch(home);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Khong mo duoc man Search In Library");
        return s;
    }

    @Test(description = "TC_SL_009: Fuzzy search - tim gan dung (substring)")
    public void TC_SL_009_fuzzy_search() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(FUZZY_QUERY);
        home.sleep(1300);
        Assert.assertTrue(s.hasAnyResult(), "Fuzzy '" + FUZZY_QUERY + "' khong ra ket qua");
        Assert.assertTrue(s.resultContains(EXPECT_NAME),
                "Fuzzy '" + FUZZY_QUERY + "' khong tim thay '" + EXPECT_NAME + "' (sua hang so)");
        ExtentReportManager.getTest().log(Status.PASS, "Fuzzy substring '" + FUZZY_QUERY + "' -> thay '" + EXPECT_NAME + "'.");
    }

    @Test(description = "TC_SL_010: Exact search - tim chinh xac")
    public void TC_SL_010_exact_search() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(EXACT_QUERY);
        home.sleep(1300);
        Assert.assertTrue(s.hasAnyResult(), "Exact '" + EXACT_QUERY + "' khong ra ket qua");
        Assert.assertTrue(s.resultContains(EXPECT_NAME),
                "Exact '" + EXACT_QUERY + "' khong thay '" + EXPECT_NAME + "'");
        ExtentReportManager.getTest().log(Status.PASS, "Exact '" + EXACT_QUERY + "' -> thay '" + EXPECT_NAME + "'.");
    }

    @Test(description = "TC_SL_011: Case insensitive search")
    public void TC_SL_011_case_insensitive() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.typeQuery(LOWER_QUERY);   // chu thuong
        home.sleep(1300);
        Assert.assertTrue(s.resultContains(EXPECT_NAME),
                "Query thuong '" + LOWER_QUERY + "' khong tim thay '" + EXPECT_NAME + "' (case-insensitive that bai)");
        ExtentReportManager.getTest().log(Status.PASS,
                "Query thuong '" + LOWER_QUERY + "' van khop '" + EXPECT_NAME + "' (case-insensitive).");
    }
}