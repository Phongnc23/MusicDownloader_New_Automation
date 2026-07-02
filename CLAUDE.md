# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Music Downloader (New) Automation Framework

## Commands

```bash
# Chay TOAN BO regression (mac dinh suite = regression.xml)
./gradlew.bat test

# Chay 1 SUITE cu the: -Psuite=<ten> tro toi src/test/resources/<ten>.xml
./gradlew.bat test -Psuite=_sl_tmp

# Chi compile (kiem tra loi nhanh, khong chay device)
./gradlew.bat compileTestJava

# RAM host thap (~<1GB) -> them --no-daemon de nhe bo nho
./gradlew.bat test --no-daemon -Psuite=regression
```

- **Chay 1 class / 1 method**: KHONG dung `--tests` (project chay qua TestNG suite XML, `--tests` bi bo qua). Thay vao do tao 1 suite XML tam (vd `_sl_tmp.xml`) voi `<class>` + `<methods><include name="TC_..."/>` roi chay `-Psuite=_sl_tmp`.
- Co che chon suite o `build.gradle`: `def suiteName = project.findProperty('suite') ?: 'regression'`.
- **Truoc khi chay**: Appium phai READY tai `http://127.0.0.1:4723` va thiet bi `adb devices` online (bypass quang cao lan dau bang Appium Inspector).
- Full regression ~1h30m+ tren may RAM thap (swap). Chay detached (`Start-Process`) neu can song qua gioi han timeout cua shell; kill java+gradle giua chung khi can dung.
- Report: `build/reports/tests/test/index.html` (Gradle) + ExtentReports (flush cuoi suite).

## Tech Stack
- Java 21, Gradle (Groovy DSL)
- Appium 2.18.0 + uiautomator2 driver v3.10.0
- java-client 9.3.0, Selenium 4.27.0
- TestNG 7.10.2, ExtentReports 5.1.2, Log4j 2.26.0

## App Under Test
- **App**: Music Downloader (New)
- **Package**: com.musicdownloaderapp.musicdownloadappfree.mp3download
- **Activity**: com.example.blue_music_player.MainActivity
- Cau hinh device/package qua `.env` (copy tu `.env.example`); default trong `constants/AppConstants.java`
- **Device**: Oppo Pad Neo (Android 14, ColorOS)
- **Screen size**: 1720 x 2408

## Project Structure

```
├── docs/
│   ├── dom/                        - DOM mau cua tung man hinh
│   └── testcases/                  - Excel testcase manual
│
├── src/
│   ├── main/java/
│   │   ├── base/           BasePage.java, BaseTest.java
│   │   ├── constants/      AppConstants.java, TimeOutConstants.java
│   │   ├── driver/         DriverFactory, DriverManager, AndroidDriverManager
│   │   ├── listeners/      TestListener.java
│   │   ├── pages/          Page Object classes
│   │   │   ├── HomePage.java
│   │   │   └── components/
│   │   │       └── InterstitialAdHandler.java (xu ly quang cao AdMob)
│   │   ├── report/         ExtentReportManager.java
│   │   └── utils/          GestureUtils, LogUtils, ScreenshotUtils
│   │
│   └── test/
│       ├── java/testcases/
│       │   └── home/       Home01_Verify_App_Launched.java
│       └── resources/
│           ├── log4j2.xml
│           └── regression.xml
│
├── build.gradle
├── settings.gradle
└── CLAUDE.md
```

## Coding Conventions

### Naming
- **Test class**: `FeatureNN_Verb_Description.java` (VD: `Home01_Verify_App_Launched.java`)
- **Test method**: prefix `TC_` (VD: `TC_HOME_001_app_launch_and_dismiss_ad()`)
- **Page class**: `XxxPage.java`
- **Component**: trong `pages/components/`
- **Constants**: UPPER_SNAKE_CASE
- **Package name**: lowercase

### Page Object Pattern
- Extends `BasePage`
- Private `By` locators o dau class
- Public action + verify methods
- Dung method tu BasePage: `click()`, `sendKeys()`, `findElement()`, `isDisplayed()`, `getText()`
- Logger da co san tu BasePage

### Test Class Pattern
- Extends `BaseTest`
- `@Test(description = "TC_XX: Mo ta tieng Viet khong dau")`
- Dung `ExtentReportManager.getTest().log(Status.INFO/PASS/FAIL/SKIP, ...)`
- Dung `Assert.assertEquals/assertTrue` tu TestNG
- `@BeforeMethod` (KHONG phai @BeforeClass) de moi test isolate

### Locator Strategy (theo thu tu uu tien)
1. `By.id("<package>:id/xxx")` - co resource-id (TOT NHAT)
2. `AppiumBy.accessibilityId("xxx")` - co content-desc
3. `By.xpath(...)` - last resort

### Comments
Code comments dung **tieng Viet KHONG dau** (console khong support UTF-8).
EXCEPTION: `InterstitialAdHandler.java` co text "Đóng" - phai giu UTF-8.

## Critical Behavior

### Quang cao Interstitial (BAT BUOC bypass truoc khi vao Home)
- `BaseTest.@BeforeMethod` da tu dong xu ly bang `InterstitialAdHandler.dismissAd()`
- Nut Close/Skip thuong `clickable=false` -> PHAI tap theo toa do (GestureUtils.tap)
- Vi tri nut random top-left HOAC top-right

### Test isolation & app state (moi test 1 driver)
- `BaseTest.@BeforeMethod` khoi tao driver moi + `activateApp` + `waitAppReady()`; `@AfterMethod` quit driver.
- `waitAppReady()` **KHONG ep app ve Home** (theo yeu cau): chi dong dialog Update, bam Cancel neu gap exit-dialog, va BACK de thoat overlay (Select mode/Queue/Play Now) cho toi khi thay BOTTOM NAV. -> App co the "san sang" MA DANG O TAB KHAC (Artists/Albums/...), khong phai Home.
- BaseTest pause playback o CA setUp lan tearDown (`HomePage.pausePlaybackIfPlaying`) -> moi test bat dau/ket thuc voi UI TINH.

### Retry flaky
- `listeners.RetryTransformer` + `RetryAnalyzer` (retry 2x) dang ky trong MOI suite XML (`<listener>`). Bai hat sieu ngan (0:03-0:07) auto-next lien tuc gay churn/StaleElement -> retry hap thu phan lon.

### Cross-test state leak (ap dung MOI module - quan trong)
- Voi `noReset`, moi TAB nho sub-route cuoi. Neu test truoc de app o DETAIL/SELECT MODE, `tapNavXxx()` cua test sau co the ket lai o sub-route do (detail khong co bottom nav de pop). List offstage van trong a11y tree -> `isXxxScreenDisplayed()` FALSE-POSITIVE. -> Nav helper moi module phai SELF-HEAL (BACK thoat overlay TRUOC + SAU khi tap nav) va screen-oracle phai EXCLUDE detail. Xem `AlbumsPage.gotoAlbumsList`.

### Search Online (TC_DL_*) CHI vao tu Home
- Thanh "Search music online..." chi co tren Home. Vi `waitAppReady` khong ep Home, cac test Search phai tu ep ve Home: extends `base.SearchOnlineBaseTest` (`@BeforeMethod ensureOnHomeBeforeSearch` chay SAU setUp cha). KHONG nhet logic nay vao BaseTest (tranh anh huong module khac).

### Playback tests PHAI sort "Date modified" truoc khi phat
- Thu vien co clip 0:03 tu auto-next/loop lien tuc -> Next/Prev doc nham bai cu, mini player chap chon. Goi `tracks.setSortDateModifiedTop()` trong nav helper (dua bai DAI/moi tai len dau) TRUOC moi test Play all/Next/Prev. Xem `Tracks03`/`Tracks07`.
- Man search phat track INLINE (khong mo full player), mini player o DAY bi BAN PHIM che -> goi `home.hideKeyboardSafe()` truoc khi doc trang thai playback.

### Locator luu y (Flutter a11y khong on dinh)
- Dung `descriptionContains(...)` (case-insensitive) + Java `Pattern`, KHONG dung `descriptionMatches`.
- Nhieu nut (mini player, select-mode toolbar, sort, tab search) co `clickable=false` hoac content-desc doi moi giay -> tap theo TOA DO (`GestureUtils.tap`), khong click element (tranh stale).
- Flutter list/grid VIRTUALIZE item off-screen; `UiScrollable.scrollIntoView` KHONG dang tin -> mo item bang vong swipe thu cong + `existsImmediately` moi lan.

### Data-dependent (nguyen nhan fail hay gap khi chay full)
- Nhieu test hard-code ten bai/album/playlist ("Blank Space", "RecoveredAudios", "VoiceChanger", "QA_PL_*"). Test delete + E2E download LAM DOI thu vien trong lung suite -> hang so co the khong con khop -> fail khi chay full du PASS khi chay le. Uu tien dung query ben vung (vd "import_" da co nhieu bai) hoac tu tao/tu don data.

## Workflow Phat Trien
1. Bypass quang cao bang Appium Inspector lan dau
2. Inspect man hinh -> luu DOM vao `docs/dom/<man_hinh>_dom.md`
3. Viet manual testcase tieng Viet -> export Excel vao `docs/testcases/`
4. Update / tao Page Object voi locator that
5. Viet test class theo pattern `FeatureNN_Verb_Description.java`
6. Add vao `regression.xml`
7. Run
