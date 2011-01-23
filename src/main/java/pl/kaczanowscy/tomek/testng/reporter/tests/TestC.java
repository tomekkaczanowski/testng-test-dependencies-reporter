package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.Test;

@Test(groups = "group_C")
public class TestC {

    public void test_C_1() {
        assert true;
    }

    @Test(dependsOnMethods = "test_C_1", dependsOnGroups = "group_B")
    public void test_C_2() {
        assert true;
    }

    @Test(dependsOnGroups = "group_A")
    public void test_C_3() {
        assert true;
    }
}
