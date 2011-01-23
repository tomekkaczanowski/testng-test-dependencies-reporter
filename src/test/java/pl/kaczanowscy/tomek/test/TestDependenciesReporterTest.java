package pl.kaczanowscy.tomek.test;

import org.testng.TestNG;
import org.testng.annotations.Test;
import pl.kaczanowscy.tomek.testng.reporter.TestDependenciesReporter;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestA;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestB;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestC;

import java.io.File;
import java.util.Arrays;

import static org.testng.Assert.assertTrue;

@Test
public class TestDependenciesReporterTest {

	public void testReporter() {
		TestNG testNG = new TestNG();
        //testNG.setTestJar("bab");
        testNG.setTestClasses(new Class[] {TestA.class, TestB.class, TestC.class});
        testNG.setListenerClasses(Arrays.asList(new Class[] {TestDependenciesReporter.class}));
        testNG.setOutputDirectory(TestDependenciesReporter.DEFAULT_OUTPUT_DIR);
        testNG.run();

        // FIXME test stuff here
        File blah = new File(TestDependenciesReporter.DEFAULT_OUTPUT_DIR + "/blah.dot");
        assertTrue(blah.exists());

        blah = new File(TestDependenciesReporter.DEFAULT_OUTPUT_DIR + "/blah2.dot");
        assertTrue(blah.exists());
	}
}
