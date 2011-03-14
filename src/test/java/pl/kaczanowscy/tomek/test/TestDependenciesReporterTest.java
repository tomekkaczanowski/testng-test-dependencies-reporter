package pl.kaczanowscy.tomek.test;

import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.kaczanowscy.tomek.testng.listener.SkipTestsListener;
import pl.kaczanowscy.tomek.testng.reporter.TestDependenciesReporter;
import pl.kaczanowscy.tomek.testng.reporter.tests.complex.TestA;
import pl.kaczanowscy.tomek.testng.reporter.tests.complex.TestB;
import pl.kaczanowscy.tomek.testng.reporter.tests.complex.TestC;
import pl.kaczanowscy.tomek.testng.reporter.tests.group.TestGroup;
import pl.kaczanowscy.tomek.testng.reporter.tests.group.TestGroupFailedConfig;
import pl.kaczanowscy.tomek.testng.reporter.tests.nogroup.TestNoGroup;
import pl.kaczanowscy.tomek.testng.reporter.tests.nogroup.TestNoGroupFailedConfig;
import pl.kaczanowscy.tomek.testng.reporter.tests.varia.AnotherClass;
import pl.kaczanowscy.tomek.testng.reporter.tests.varia.TestDependsOnMethodFromAnotherClass;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test
public class TestDependenciesReporterTest {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private TestNG testNG;

    @BeforeMethod
    public void setUp() {
        testNG = new TestNG();
        // TODO remember you can also addListener not a Class
        //testNG.setOutputDirectory(TestDependenciesReporter.DEFAULT_OUTPUT_DIR);
    }

    @AfterMethod
    public void tearDown() {
        removeOutputDir();
    }

     /*@Test(enabled =  false)
     public void testNoColors() throws FileNotFoundException {
        TestDependenciesReporter reporter = new TestDependenciesReporter();
        reporter.noColor();
        testNG.setListenerClasses(Arrays.asList(new Class[] {SkipTestsListener.class}));
        testNG.addListener(reporter);
        testNG.setTestClasses(new Class[] {TestA.class, TestB.class, TestC.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("digraph testDependencies {"), text);
        assertTrue(text.contains("test_A_1"), text);
        assertTrue(text.contains("test_A_2"), text);
        assertTrue(text.contains("test_A_3"), text);

        assertTrue(text.contains("test_A_2 -> test_A_1"), text);
        assertTrue(text.contains("test_A_3 -> test_A_2"), text);
        assertTrue(text.contains("}"), text);
        assertFalse(text.contains("red"), text);
        assertFalse(text.contains("yellow"), text);
    }*/

     public void testNoGroup() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));

        testNG.setTestClasses(new Class[] {TestNoGroup.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("TestNoGroup_skippedBecauseDependsOnFailedMethod -> TestNoGroup_failedBecauseThrowsExc"), text);
        assertFalse(text.contains("TestNoGroup_neitherSkippedNorFailed"), text);
    }

    public void testNoGroupFailedConfig() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));

        testNG.setTestClasses(new Class[] {TestNoGroupFailedConfig.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("TestNoGroupFailedConfig_skippedBecauseDependsOnFailedConfig -> TestNoGroupFailedConfig_failedConfig"), text);
        assertTrue(text.contains("TestNoGroupFailedConfig_skippedBecauseDependsOnSkippedMethod -> TestNoGroupFailedConfig_skippedBecauseDependsOnFailedConfig"), text);
    }

    public void testGroup() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));

        testNG.setTestClasses(new Class[] {TestGroup.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("TestGroup_skippedBecauseDependsOnFailedMethod -> TestGroup_failedBecauseThrowsExc"), text);
        assertFalse(text.contains("TestGroup_neitherSkippedNorFailed"), text);
    }

    public void testGroupFailedConfig() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));

        testNG.setTestClasses(new Class[] {TestGroupFailedConfig.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("TestGroupFailedConfig_skippedBecauseDependsOnFailedConfig -> TestGroupFailedConfig_failedConfig"), text);
        assertTrue(text.contains("TestGroupFailedConfig_skippedBecauseDependsOnSkippedMethod -> TestGroupFailedConfig_skippedBecauseDependsOnFailedConfig"), text);
    }

    public void testDependencyOnAnotherClass() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));

        testNG.setTestClasses(new Class[] {AnotherClass.class, TestDependsOnMethodFromAnotherClass.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("TestDependsOnMethodFromAnotherClass_skippedBecauseDependsOnFailedMethodFromAnotherClass -> AnotherClass_failed"), text);
        assertFalse(text.contains("TestDependsOnMethodFromAnotherClass_neitherSkippedNorFailed"), text);
    }

    public void testComplexStuff() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));

        testNG.setTestClasses(new Class[] {TestA.class, TestB.class, TestC.class});
        testNG.run();

        String text = readDotFile();


        assertTrue(text.contains("TestB_skipped -> TestA_failure"), text);
        assertTrue(text.contains("TestB_alsoSkipped -> TestB_skipped"), text);
        assertTrue(text.contains("TestC_skipped -> Group_B"), text);
        assertFalse(text.contains("TestA_success"), text);
        assertFalse(text.contains("TestA_validSetUp"), text);
        assertFalse(text.contains("TestB_validSetUp"), text);
        assertFalse(text.contains("TestC_validSetUp"), text);
    }

    private String readDotFile() throws FileNotFoundException {
        File dotFile = new File(testNG.getOutputDirectory() + FILE_SEPARATOR + "dotFile.dot");
        assertTrue(dotFile.exists());

        return readFile(dotFile);
    }

   /* public void testThreeTestClasses() throws FileNotFoundException {
        //testNG.setTestJar("bab");
        testNG.setTestClasses(new Class[]{TestA.class, TestB.class, TestC.class});
        testNG.run();

        // FIXME test stuff here
        String text = readDotFile();
    }*/

    private String readFile(File blah) throws FileNotFoundException {
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(blah));
        try {
          while (scanner.hasNextLine()){
            text.append(scanner.nextLine() + NL);
          }
        }
        finally{
          scanner.close();
        }
        return text.toString();
    }

    private void removeOutputDir() {
        File outputDir = new File(testNG.getOutputDirectory());
        outputDir.delete();
    }

}
