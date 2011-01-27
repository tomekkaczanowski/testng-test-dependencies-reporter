package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.Test;

@Test(groups = {"group_A"}, dependsOnGroups = "group_E")
public class TestA {

    public void test_A_1() {
        assert true;
    }

    @Test(dependsOnMethods = "test_A_1", dependsOnGroups = "group_C")
    public void test_A_2() {
        assert true;
    }

    @Test(dependsOnMethods = "test_A_2", dependsOnGroups = {"group_C", "group_D"})
    public void test_A_3() {
        assert true;
    }
}
