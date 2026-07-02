package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrapper log4j2 don gian de log nhanh tu bat ky dau.
 */
public class LogUtils {

    private static final Logger log = LogManager.getLogger(LogUtils.class);

    private LogUtils() {
    }

    public static void info(String message) {
        log.info(message);
    }

    public static void info(String format, Object... args) {
        log.info(format, args);
    }

    public static void warn(String message) {
        log.warn(message);
    }

    public static void error(String message) {
        log.error(message);
    }

    public static void error(String message, Throwable t) {
        log.error(message, t);
    }

    public static void debug(String message) {
        log.debug(message);
    }
}
