# Music Downloader (New) - Automation Framework

Framework kiem thu tu dong (Appium + Java + TestNG, mo hinh Page Object) cho app
**Music Downloader (New)** tren Android.

---

## 1. Yeu cau moi truong

| Thanh phan | Phien ban / Ghi chu |
|------------|---------------------|
| JDK | **21** (bat buoc) |
| Node.js + Appium | **Appium 2.x** (`npm i -g appium`) |
| Appium driver | `uiautomator2` (`appium driver install uiautomator2`) |
| Android SDK | `adb` co trong PATH |
| Thiet bi | Android device/emulator (dev: Oppo Pad Neo - Android 14, 1720x2408) |
| IDE (tuy chon) | IntelliJ IDEA |

> Gradle KHONG can cai san - dung `gradlew` (wrapper, tu tai Gradle 9.0.0).

Kiem tra nhanh:
```bash
java -version        # phai la 21
appium --version     # 2.x
appium driver list   # thay uiautomator2 (installed)
adb devices          # thay thiet bi cua ban
```

---

## 2. Cai dat (lan dau)

### B1. Clone / giai nen project
```bash
cd MusicDownloader_Flutter-V2
```

### B2. Tao file cau hinh `.env`
File `.env` chua cau hinh RIENG cua may ban (da bi `.gitignore`). Copy tu mau:
```bash
cp .env.example .env      # Git Bash / macOS / Linux
# copy .env.example .env  # Windows CMD
```
Mo `.env` va sua **UDID** (lay tu `adb devices`) + `DEVICE_NAME` cho dung may.

> Neu la may FRESH (chua tung chay Appium) -> dat `SKIP_SERVER_INSTALL=false`
> trong `.env` cho lan chay dau (de Appium cai uiautomator2 server), sau do co the bat lai `true` cho nhanh.

### B3. Cai app duoi test len thiet bi
Cai file APK cua Music Downloader (New) len thiet bi. Package mac dinh:
`com.musicdownloaderapp.musicdownloadappfree.mp3download` (sua trong `.env` neu khac).

---

## 3. Chay test

### B1. Cam thiet bi + kiem tra
```bash
adb devices          # phai thay thiet bi "device" (khong phai "unauthorized")
```

### B2. Khoi dong Appium server (mo 1 terminal rieng, de nguyen)
```bash
appium
```
Doi dong: `Appium REST http interface listener started on http://0.0.0.0:4723`

### B3. Chay test (terminal khac)
```bash
# Chay TOAN BO regression (mac dinh suite = src/test/resources/regression.xml)
./gradlew test              # macOS/Linux/Git Bash
gradlew.bat test           # Windows CMD/PowerShell

# May RAM thap -> them --no-daemon cho nhe bo nho
./gradlew test --no-daemon
```

#### Chay 1 SUITE cu the
Dat property `-Psuite=<ten>` (tro toi `src/test/resources/<ten>.xml`):
```bash
./gradlew test -Psuite=regression
```

#### Chay 1 CLASS hoac 1 METHOD
Project chay qua **TestNG suite XML** nen `--tests` KHONG co tac dung. Tao 1 file
suite XML tam (vd `src/test/resources/_tmp.xml`) roi tro toi no:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="tmp" verbose="1">
  <listeners>
    <listener class-name="listeners.TestListener"/>
    <listener class-name="listeners.RetryTransformer"/>
  </listeners>
  <test name="one">
    <classes>
      <class name="testcases.home.Home03_Verify_UI_Display">
        <methods><include name="TC_HOME_007_quick_actions"/></methods>
      </class>
    </classes>
  </test>
</suite>
```
```bash
./gradlew test -Psuite=_tmp
```

---

## 4. Xem ket qua

| Loai | Duong dan |
|------|-----------|
| ExtentReport (HTML dep) | `reports/ExtentReport.html` |
| Gradle test report | `build/reports/tests/test/index.html` |
| Screenshot khi FAIL | `screenshots/` |
| Log chi tiet | console + `log4j2.xml` cau hinh |

---

## 5. Cau truc & quy uoc

Xem chi tiet trong [CLAUDE.md](./CLAUDE.md): cau truc thu muc, quy uoc dat ten,
Page Object pattern, hanh vi dac thu (bypass quang cao, retry flaky, sort truoc khi
phat nhac, ...).

Tom tat:
- `src/main/java/` - code framework: `base/`, `pages/`, `driver/`, `constants/`, `helpers/`, `utils/`, `listeners/`
- `src/test/java/testcases/<module>/` - test theo module (home, menu, search, tracks, artists, albums, playlists, searchlibrary)
- `src/test/resources/*.xml` - TestNG suite (regression.xml = day du)
- `docs/dom/` - DOM tung man hinh; `docs/testcases/` - test case Excel

---

## 6. Troubleshooting

### "Could not start a new session"
- Appium server dang chay? (`appium` o terminal rieng)
- `adb devices` thay device (khong "unauthorized")?
- App da cai tren device? Package/activity dung trong `.env`?
- May fresh chua co uiautomator2 server -> dat `SKIP_SERVER_INSTALL=false` trong `.env`.

### "Element not found" / test flaky
- Quang cao chua tat -> xem log `[Ad]`.
- App churn (bai nhac ngan tu chuyen) -> `RetryTransformer` da tu retry 2 lan; xem [CLAUDE.md](./CLAUDE.md).
- UI thay doi -> re-inspect bang Appium Inspector, cap nhat locator/toa do.
- Timing -> tang `MEDIUM_WAIT` trong `constants/TimeOutConstants.java`.

### Test fail vi thieu du lieu (album/bai hat)
- Mot so test tung hard-code ten album/bai; da chuyen sang **tu tim du lieu co thuc**.
- Neu van thieu -> tai them nhac vao thu vien app, hoac xem hang so trong test class.

### "Test pass tren may khac, fail tren Oppo/ColorOS"
- Capability `ignoreHiddenApiPolicyError: true` (da bat san).
- Settings -> Developer Options -> bat **USB debugging (Security settings)**.
- Tat **Permission monitoring** neu co.

### App bi ket o 1 man (vd Search) lam test sau khong dieu huong duoc
```bash
adb shell am force-stop com.musicdownloaderapp.musicdownloadappfree.mp3download
```
