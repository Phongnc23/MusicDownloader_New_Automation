package base;

import org.testng.annotations.BeforeMethod;
import pages.HomePage;

/**
 * Base cho cac test Search Online (TC_DL_*).
 *
 * Ly do ton tai: man Search Online CHI vao duoc tu HOME (thanh "Search music online..."
 * chi hien tren Home). BaseTest.waitAppReady chi dam bao co BOTTOM NAV -> nhung Artists/
 * Albums/Tracks/Playlists list cung co bottom nav, nen app co the "san sang" MA KHONG o Home
 * (vd module truoc de lai app o tab Artists). Luc do home.tapSearchBar() tap nham -> test ket.
 *
 * @BeforeMethod nay chay SAU setUp() cua BaseTest (TestNG: @BeforeMethod cha chay truoc con)
 * nen driver da san sang; no EP app ve Home truoc moi test -> thu tu module trong regression
 * khong con anh huong den module Search Online. Dat o package 'base' cung BaseTest.
 */
public abstract class SearchOnlineBaseTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void ensureOnHomeBeforeSearch() {
        HomePage home = new HomePage();
        for (int i = 0; i < 6 && !home.isHomeDisplayed(); i++) {
            if (home.isDrawerOpen()) {           // drawer mo -> dong lai
                home.closeMenuDrawer();
                home.sleep(500);
                continue;
            }
            if (home.isBottomNavDisplayed()) {   // dang o tab list khac -> tap tab Home
                home.tapNavHome();
                home.sleep(800);
                continue;
            }
            home.hideKeyboardSafe();             // dang o overlay/detail -> BACK thoat
            home.pressBack();
            home.sleep(700);
        }
        log.info("ensureOnHomeBeforeSearch: isHome={}", home.isHomeDisplayed());
    }
}
