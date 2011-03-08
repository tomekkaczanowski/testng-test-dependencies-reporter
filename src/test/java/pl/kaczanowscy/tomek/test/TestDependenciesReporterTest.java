package pl.kaczanowscy.tomek.test;

import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.kaczanowscy.tomek.testng.listener.SkipTestsListener;
import pl.kaczanowscy.tomek.testng.reporter.TestDependenciesReporter;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestA;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestB;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestC;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test
public class TestDependenciesReporterTest {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    // TODO temp files instead?

    // TODO drawing

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
    }

    //@Test(enabled =  false)
    public void testOneTestClass() throws FileNotFoundException {
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));
        // FIXME separate test cases for B and C
        testNG.setTestClasses(new Class[] {TestA.class, TestB.class, TestC.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("digraph testDependencies {"), text);
        assertTrue(text.contains("test_A_1"), text);
        assertTrue(text.contains("test_A_2"), text);
        assertTrue(text.contains("test_A_3"), text);

        assertTrue(text.contains("test_A_2 -> test_A_1"), text);
        assertTrue(text.contains("test_A_3 -> test_A_2"), text);
        assertTrue(text.contains("test_C_6"), text);
        assertTrue(text.contains("test_A_6"), text);
        assertTrue(text.contains("}"), text);
        assertTrue(text.contains("red"), text);
        assertTrue(text.contains("yellow"), text);
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
