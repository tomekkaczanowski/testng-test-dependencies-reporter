package pl.kaczanowscy.tomek.testng.reporter.tests;

import org.testng.annotations.Test;

@Test(groups = {"group_A"})
public class TestA {

    public void test_A_1() {
        assert false;
    }

    @Test(dependsOnMethods = "test_A_1", dependsOnGroups = "group_C")
    public void test_A_2() {
        assert false;
    }

    @Test(dependsOnMethods = "test_A_2", dependsOnGroups = {"group_C", "group_D"})
    public void test_A_3() {
        assert true;
    }

    public void test_A_4() {
        assert true;
    }

    public void test_A_5() {
        assert true;
    }

    public void test_A_6() {
        assert true;
    }
}
