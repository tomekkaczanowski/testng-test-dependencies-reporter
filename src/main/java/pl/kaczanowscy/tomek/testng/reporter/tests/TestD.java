package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class TestD {

    @BeforeClass
    public void failedBeforeClassMethod() {
        assert false;
    }

    public void test_D_1() {
        assert true;
    }

    public void test_D_2() {
        assert true;
    }
}
