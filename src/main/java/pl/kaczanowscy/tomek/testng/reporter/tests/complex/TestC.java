package pl.kaczanowscy.tomek.testng.reporter.tests.complex;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "C", dependsOnGroups = "B")
public class TestC {

    @BeforeClass
    public void validSetUp() {
        assert true;
    }

    public void skipped() {
        assert true;
    }

}
