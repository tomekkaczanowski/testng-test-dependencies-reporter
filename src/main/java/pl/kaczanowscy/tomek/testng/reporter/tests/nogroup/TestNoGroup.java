package pl.kaczanowscy.tomek.testng.reporter.tests.nogroup;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class TestNoGroup {

    public void failedBecauseThrowsExc() {
        throw new IllegalArgumentException();
    }

    @Test(dependsOnMethods = "failedBecauseThrowsExc")
    public void skippedBecauseDependsOnFailedMethod() {
        assert true;
    }

    public void neitherSkippedNorFailed() {
        assert true;
    }
}
