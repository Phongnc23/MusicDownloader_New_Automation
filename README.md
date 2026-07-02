# Music Downloader (New) - Automation Framework

Framework Appium POM cho app Music Downloader (ban moi), copy pattern tu project Music Downloader / Voice Changer.

## Yeu cau moi truong

- JDK 21
- Appium Server 2.18.0 (driver `uiautomator2`)
- Android device/emulator (Oppo Pad Neo - Android 14)
- IntelliJ IDEA

## Cac buoc chay

### 1. Dien thong tin app moi
Mo `src/main/java/constants/AppConstants.java`, update:
```java
APP_PACKAGE  = "..."   // package app moi
APP_ACTIVITY = "..."   // main activity app moi
```
Lay bang lenh (khi app dang mo):
```bash
adb shell "dumpsys activity activities | grep mResumedActivity"
```

### 2. Open Project trong IntelliJ
1. **File** -> **Open** -> chon folder project
2. IntelliJ tu detect `build.gradle` -> click **Trust Project**
3. Doi Gradle sync xong
4. Neu code do: chuot phai `src/main/java` -> **Mark Directory as** -> **Sources Root**;
   `src/test/java` -> **Test Sources Root**

### 3. Verify Device
```bash
adb devices
```

### 4. Start Appium Server
```bash
appium
```
Doi thay:
```
[Appium] Appium REST http interface listener started on http://0.0.0.0:4723
```

### 5. Chay Test
- Chuot phai `src/test/resources/regression.xml` -> **Run 'regression.xml'**
- Hoac: `gradlew test`

Report nam o `reports/Music_Downloader_New_Report.html`.

## Project Structure
Xem chi tiet trong [CLAUDE.md](./CLAUDE.md).

## Troubleshooting

### "Could not start a new session"
- Check Appium server dang chay
- Check `adb devices` thay device
- Check app da cai tren device, package/activity dung trong AppConstants

### "Element not found"
- Quang cao chua tat -> check log `[Ad]`
- UI thay doi -> re-inspect bang Appium Inspector
- Timing -> tang `MEDIUM_WAIT` trong TimeOutConstants

### "Test passes locally but fails on Oppo"
- Capability `ignoreHiddenApiPolicyError: true` (da co)
- Settings -> Developer Options -> bat **USB debugging (Security settings)**
- Tat **Permission monitoring** neu co
