package pl.kaczanowscy.tomek.testng.reporter.tests.complex;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "B")
public class TestB {

    @BeforeClass
    public void validSetUp() {
        assert true;
    }

    // will be skipped because depends on failed method
    @Test(dependsOnMethods = "pl.kaczanowscy.tomek.testng.reporter.tests.complex.TestA.failure")
    public void skipped() {
        assert true;
    }

    // will be skipped because depends on skipped method
    @Test(dependsOnMethods = "skipped")
    public void alsoSkipped() {
        assert false;
    }
}
