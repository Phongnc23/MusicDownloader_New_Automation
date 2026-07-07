package constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants cho Music Downloader app.
 *
 * <p>Cac gia tri PHU THUOC THIET BI / KET NOI (UDID, device name, platform version, Appium URL...)
 * duoc nap tu file <b>.env</b> o thu muc goc project. Khi nguoi khac pull repo ve, chi can copy
 * <b>.env.example</b> thanh <b>.env</b> roi sua UDID / device cho may cua minh - KHONG phai sua code.
 *
 * <p>Thu tu uu tien moi gia tri: bien moi truong he thong &gt; -Dkey=value (JVM property)
 * &gt; file .env &gt; default trong code.
 */
public final class AppConstants {

    private static final Map<String, String> ENV = loadEnvFile();

    // App info
    public static final String APP_PACKAGE  = get("APP_PACKAGE", "com.musicdownloaderapp.musicdownloadappfree.mp3download");
    public static final String APP_ACTIVITY = get("APP_ACTIVITY", "com.example.blue_music_player.MainActivity");
    public static final String APP_NAME     = get("APP_NAME", "Music Downloader");

    // Device info (PHU THUOC MAY - sua trong .env)
    public static final String PLATFORM_NAME    = get("PLATFORM_NAME", "Android");
    public static final String PLATFORM_VERSION = get("PLATFORM_VERSION", "14");
    public static final String DEVICE_NAME      = get("DEVICE_NAME", "Android Device");
    public static final String UDID             = get("UDID", "");

    // Automation
    public static final String AUTOMATION_NAME = get("AUTOMATION_NAME", "UiAutomator2");

    // TOI UU TOC DO tao session (mac dinh TRUE - may test da cai san uiautomator2 server).
    // Bo cai lai server + bo device-init moi session -> tao driver nhanh hon ~5s/test.
    // Neu chay tren may FRESH (chua tung chay Appium) -> dat "false" trong .env cho lan dau.
    public static final boolean SKIP_SERVER_INSTALL = Boolean.parseBoolean(get("SKIP_SERVER_INSTALL", "true"));
    public static final boolean SKIP_DEVICE_INIT    = Boolean.parseBoolean(get("SKIP_DEVICE_INIT", "true"));

    // Toa do icon header (2 icon goc KHONG co content-desc -> tap theo toa do).
    // Lay tu tam bounds: drawer [9,64][117,154]; search [1594,64][1702,154].
    // PHU THUOC MAN HINH (1720x2408) - sua trong .env neu doi thiet bi.
    public static final int DRAWER_ICON_X = Integer.parseInt(get("DRAWER_ICON_X", "63"));
    public static final int DRAWER_ICON_Y = Integer.parseInt(get("DRAWER_ICON_Y", "109"));
    public static final int SEARCH_ICON_X = Integer.parseInt(get("SEARCH_ICON_X", "1648"));
    public static final int SEARCH_ICON_Y = Integer.parseInt(get("SEARCH_ICON_Y", "109"));

    // Appium server (PHU THUOC MAY - sua trong .env neu chay port/host khac)
    public static final String APPIUM_SERVER_URL = get("APPIUM_SERVER_URL", "http://127.0.0.1:4723");

    // Report (ExtentReports) - REPORT_DIR ket thuc bang '/' vi se noi truc tiep voi REPORT_NAME.
    public static final String REPORT_DIR       = get("REPORT_DIR", "reports/");
    public static final String REPORT_NAME      = get("REPORT_NAME", "ExtentReport.html");
    public static final String REPORT_TITLE     = get("REPORT_TITLE", "Music Downloader - Automation Report");
    public static final String REPORT_DOC_TITLE = get("REPORT_DOC_TITLE", "Music Downloader Test Report");

    // Screenshot - ket thuc bang '/' vi se noi truc tiep voi ten file.
    public static final String SCREENSHOT_DIR = get("SCREENSHOT_DIR", "screenshots/");

    // Playlist AUTO-FIXTURE: cac test can 1 user playlist rong (detail/sheet/search) TU TAO neu chua co
    // (PlaylistsPage.ensureUserPlaylist) -> portable, khong phu thuoc data san tren may. Ten rieng
    // (khac "QA_PL_" tao boi TC_TRK_034) de khong lan voi playlist rac.
    public static final String AUTO_USER_PLAYLIST = get("AUTO_USER_PLAYLIST", "QAAUTO_PL");

    // External packages (he thong Android - hiem khi doi)
    public static final String PERMISSION_PACKAGE      = get("PERMISSION_PACKAGE", "com.android.permissioncontroller");
    public static final String SETTINGS_PACKAGE        = get("SETTINGS_PACKAGE", "com.android.settings");
    public static final String INTENT_RESOLVER_PACKAGE = get("INTENT_RESOLVER_PACKAGE", "com.android.intentresolver");

    private AppConstants() {}

    /** Lay gia tri config theo thu tu: env he thong &gt; JVM -Dkey &gt; file .env &gt; default. */
    private static String get(String key, String def) {
        String sys = System.getenv(key);
        if (sys != null && !sys.trim().isEmpty()) return sys.trim();
        String prop = System.getProperty(key);
        if (prop != null && !prop.trim().isEmpty()) return prop.trim();
        String v = ENV.get(key);
        return (v != null && !v.trim().isEmpty()) ? v.trim() : def;
    }

    /** Doc file .env (KEY=VALUE moi dong, bo dong trong/comment #). Khong co file -> map rong. */
    private static Map<String, String> loadEnvFile() {
        Map<String, String> map = new HashMap<>();
        File f = findEnvFile();
        if (f == null) {
            System.out.println("[AppConstants] Khong tim thay .env -> dung default. "
                    + "Copy .env.example thanh .env de cau hinh thiet bi.");
            return map;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String k = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();
                // Bo dau nhay bao quanh value neu co
                if (val.length() >= 2
                        && ((val.startsWith("\"") && val.endsWith("\""))
                        || (val.startsWith("'") && val.endsWith("'")))) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(k, val);
            }
            System.out.println("[AppConstants] Da nap config tu: " + f.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("[AppConstants] Loi doc .env (" + e.getMessage() + ") -> dung default.");
        }
        return map;
    }

    /** Tim file .env tu thu muc lam viec hien tai len toi 4 cap cha (phong khi chay tu sub-dir). */
    private static File findEnvFile() {
        File dir = new File(System.getProperty("user.dir", "."));
        for (int i = 0; i < 5 && dir != null; i++) {
            File f = new File(dir, ".env");
            if (f.isFile()) return f;
            dir = dir.getParentFile();
        }
        return null;
    }
}
