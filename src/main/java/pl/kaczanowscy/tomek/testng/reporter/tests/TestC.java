package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.Test;

@Test(groups = "group_C")
public class TestC {

    public void test_C_1() {
        assert true;
    }

    @Test(dependsOnMethods = "test_C_1")
    public void test_C_2() {
        assert true;
    }

    public void test_C_3() {
        assert true;
    }
}
