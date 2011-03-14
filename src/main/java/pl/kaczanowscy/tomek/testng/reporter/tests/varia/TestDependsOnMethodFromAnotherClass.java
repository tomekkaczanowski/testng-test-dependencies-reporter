package pl.kaczanowscy.tomek.testng.reporter.tests.varia;

import org.testng.annotations.Test;

@Test
public class TestDependsOnMethodFromAnotherClass {

    @Test(dependsOnMethods = "pl.kaczanowscy.tomek.testng.reporter.tests.varia.AnotherClass.failed")
    public void skippedBecauseDependsOnFailedMethodFromAnotherClass() {
        assert true;
    }

    public void neitherSkippedNorFailed() {
        assert true;
    }
}
