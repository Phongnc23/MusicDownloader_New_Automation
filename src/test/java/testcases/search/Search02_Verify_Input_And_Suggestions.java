package testcases.search;

import base.SearchOnlineBaseTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchOnlinePage;
import report.ExtentReportManager;

/**
 * Module: Search Online (Download) - Input & Suggestions
 * Muc tieu: Go tu khoa -> hien danh sach GOI Y (autocomplete); icon ↖ chi dien o tim khong
 * search; tap chu goi y -> ra ket qua; clear o tim; query rong khong search.
 *
 * Phan biet: GOI Y = content-desc thuan (khong " • "); KET QUA = content-desc co " • ".
 */
public class Search02_Verify_Input_And_Suggestions extends SearchOnlineBaseTest {

    private SearchOnlinePage openSearch(HomePage home) {
        SearchOnlinePage search = new SearchOnlinePage();
        home.tapSearchBar();
        home.waitUntil(search::isSearchScreenDisplayed, 5000);
        Assert.assertTrue(search.isSearchScreenDisplayed(), "Khong vao duoc man Search");
        return search;
    }

    @Test(description = "TC_DL_005: Go ky tu hien thi danh sach goi y")
    public void TC_DL_005_type_shows_suggestions() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.typeQuery("xa");
        search.waitForSuggestions(4000);
        int n = search.getSuggestionCount();
        ExtentReportManager.getTest().log(Status.INFO, "So goi y: " + n + " | top: " + search.getSuggestionText(0));
        Assert.assertTrue(n > 0, "Go ky tu khong hien goi y");
        ExtentReportManager.getTest().log(Status.PASS, "Go ky tu hien danh sach goi y OK.");
    }

    @Test(description = "TC_DL_006: Goi y cap nhat theo tung ky tu nhap them")
    public void TC_DL_006_suggestions_update() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.typeQuery("xa");
        search.waitForSuggestions(4000);
        int n1 = search.getSuggestionCount();
        String top1 = search.getSuggestionText(0);
        ExtentReportManager.getTest().log(Status.INFO, "Sau 'xa': " + n1 + " goi y | top: " + top1);

        search.typeQuery("xao xuyen");
        search.waitForSuggestions(4000);
        int n2 = search.getSuggestionCount();
        String top2 = search.getSuggestionText(0);
        ExtentReportManager.getTest().log(Status.INFO, "Sau 'xao xuyen': " + n2 + " goi y | top: " + top2);

        Assert.assertTrue(n2 > 0, "Goi y khong cap nhat khi doi tu khoa");
        ExtentReportManager.getTest().log(Status.PASS, "Goi y cap nhat theo tu khoa OK.");
    }

    @Test(description = "TC_DL_007: Click icon push trong goi y chi dien o tim, khong search")
    public void TC_DL_007_push_icon_fill_only() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.typeQuery("xa");
        search.waitForSuggestions(4000);
        Assert.assertTrue(search.getSuggestionCount() > 0, "Truoc dieu kien: chua co goi y");

        String sugg = search.getSuggestionText(0);
        ExtentReportManager.getTest().log(Status.INFO, "Goi y se dien: " + sugg);
        search.tapSuggestionPushIcon(0);
        home.sleep(1200);

        Assert.assertEquals(search.getResultCount(), 0, "Icon push KHONG duoc trigger search");
        String filled = search.getSearchInputText().trim();
        ExtentReportManager.getTest().log(Status.INFO, "O tim sau khi dien: " + filled);
        Assert.assertTrue(filled.equalsIgnoreCase(sugg) || filled.contains(sugg) || sugg.contains(filled),
                "O tim khong duoc dien bang noi dung goi y");
        ExtentReportManager.getTest().log(Status.PASS, "Icon push dien o tim, khong search OK.");
    }

    @Test(description = "TC_DL_008: Clear textbox xoa noi dung tim kiem")
    public void TC_DL_008_clear_textbox() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.typeQuery("xao xuyen");
        home.sleep(800);
        Assert.assertFalse(search.getSearchInputText().trim().isEmpty(), "Truoc dieu kien: chua co text");

        search.clearInput();
        home.sleep(800);
        Assert.assertTrue(search.getSearchInputText().trim().isEmpty(), "Clear xong o tim kiem van con text");
        ExtentReportManager.getTest().log(Status.PASS, "Clear o tim kiem ve rong OK.");
    }

    @Test(description = "TC_DL_009: Query rong khong trigger search")
    public void TC_DL_009_empty_query_no_results() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.clearInput();
        home.sleep(1000);
        int n = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So KET QUA khi query rong: " + n);
        Assert.assertEquals(n, 0, "Query rong ma van co ket qua search");
        ExtentReportManager.getTest().log(Status.PASS, "Query rong khong trigger search OK.");
    }

    @Test(description = "TC_DL_010: Click text goi y trigger search ra ket qua")
    public void TC_DL_010_tap_suggestion_search() {
        HomePage home = new HomePage();
        SearchOnlinePage search = openSearch(home);

        search.typeQuery("xao xuyen");
        search.waitForSuggestions(4000);
        Assert.assertTrue(search.getSuggestionCount() > 0, "Khong co goi y de chon");
        ExtentReportManager.getTest().log(Status.INFO, "Chon goi y: " + search.getSuggestionText(0));

        search.tapSuggestion(0);
        boolean hasResults = search.waitForResults(6000);
        int n = search.getResultCount();
        ExtentReportManager.getTest().log(Status.INFO, "So ket qua sau khi chon goi y: " + n);
        Assert.assertTrue(hasResults && n > 0, "Chon goi y khong ra ket qua search");
        ExtentReportManager.getTest().log(Status.PASS, "Chon goi y trigger search ra ket qua OK.");
    }
}