package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.Test;

@Test(groups = "group_B")
public class TestB {

    @Test(dependsOnGroups = "group_A")
    public void test_B_1() {
        assert true;
    }

    @Test(dependsOnGroups = "group_A")
    public void test_B_2() {
        assert true;
    }

    @Test(dependsOnMethods = "test_B_1")
    public void test_B_3() {
        assert true;
    }
}
