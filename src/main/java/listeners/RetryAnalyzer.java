package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Chay lai test FAIL toi da MAX_RETRY lan.
 *
 * Ly do: app phat cac ban ghi CUC NGAN (0:02-0:03) -> bai tu nhay lien tuc -> mini player &
 * danh sach churn (node bi recreate) -> doi luc gay StaleElement / timing flaky rai rac (~1/11
 * case moi lan, KHAC nhau moi lan). Retry o muc test (moi lan = session moi qua @BeforeMethod)
 * lam suite on dinh ma KHONG che giau loi that (loi that se fail het ca MAX_RETRY lan).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY = 2;
    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            log.warn("RETRY {}/{}: {} (flaky do app churn) - chay lai", count, MAX_RETRY,
                    result.getMethod().getMethodName());
            return true;
        }
        return false;
    }
}
