package pl.kaczanowscy.tomek.testng.reporter.tests.group;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class TestGroupFailedConfig {

    @BeforeClass
    public void failedConfig() {
        throw new IllegalArgumentException();
    }

    public void skippedBecauseDependsOnFailedConfig() {
        assert true;
    }

    @Test(dependsOnMethods = "skippedBecauseDependsOnFailedConfig")
    public void skippedBecauseDependsOnSkippedMethod() {
        assert true;
    }
}
