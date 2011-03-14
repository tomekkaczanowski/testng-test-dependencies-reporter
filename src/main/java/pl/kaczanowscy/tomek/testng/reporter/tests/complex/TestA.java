package pl.kaczanowscy.tomek.testng.reporter.tests.complex;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class TestA {

    @BeforeClass
    public void validSetUp() {
        assert true;
    }

    public void success() {
        assert true;
    }

    public void failure() {
        assert false;
    }
}
