package pl.kaczanowscy.tomek.testng.listener;

import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestListenerAdapter;

/**
 * Skips all tests. Useful if all you want is to draw a diagram of test dependencies (without running tests, which takes time)..
 */
public class SkipTestsListener extends TestListenerAdapter {

    /**
     * Skips test by throwing SkipException.
     *
     * @param testResult
     */
    public void onTestStart(ITestResult testResult) {
        super.onTestStart(testResult);
        throw new SkipException("Skipping Test: " +
                testResult.getMethod().getMethodName());
    }
}
