package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.Test;

@Test(groups = "group_A")
public class TestA {

    public void test_A_1() {
        assert true;
    }

    @Test(dependsOnMethods = "test_A_1")
    public void test_A_2() {
        assert true;
    }

    @Test(dependsOnMethods = "test_A_2")
    public void test_A_3() {
        assert true;
    }
}
