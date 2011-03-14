package pl.kaczanowscy.tomek.testng.reporter.tests.group;

import org.testng.annotations.Test;

@Test
public class TestGroup {

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
