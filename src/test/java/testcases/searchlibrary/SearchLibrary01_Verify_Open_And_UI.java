package testcases.searchlibrary;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchInLibraryPage;
import report.ExtentReportManager;

/**
 * Module: Search In Library - Open & UI (TC_SL_001..003).
 */
public class SearchLibrary01_Verify_Open_And_UI extends BaseTest {

    private SearchInLibraryPage goSearch(HomePage home) {
        SearchInLibraryPage s = new SearchInLibraryPage();
        s.openSearch(home);
        Assert.assertTrue(s.isSearchScreenDisplayed(), "Khong mo duoc man Search In Library");
        return s;
    }

    @Test(description = "TC_SL_001: Tap search icon goc phai mo man Search")
    public void TC_SL_001_open_search() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);
        ExtentReportManager.getTest().log(Status.PASS, "Mo man Search In Library thanh cong.");
    }

    @Test(description = "TC_SL_002: Search screen co EditText va 5 tab")
    public void TC_SL_002_editext_and_tabs() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        Assert.assertTrue(s.isSearchFieldDisplayed(), "Khong thay o nhap (EditText)");
        Assert.assertTrue(s.areFilterTabsDisplayed(), "Thieu tab (can du 5: All/Tracks/Albums/Artists/Playlists)");
        ExtentReportManager.getTest().log(Status.PASS, "Co EditText + 5 tab.");
    }

    @Test(description = "TC_SL_003: Tap Back ve Home")
    public void TC_SL_003_back_to_home() {
        HomePage home = new HomePage();
        SearchInLibraryPage s = goSearch(home);

        s.tapBack();
        home.sleep(1200);
        Assert.assertFalse(s.isSearchScreenDisplayed(), "Van con o man Search sau khi Back");
        Assert.assertTrue(home.isHomeDisplayed(), "Back khong ve duoc Home");
        ExtentReportManager.getTest().log(Status.PASS, "Back ve Home OK.");
    }
}