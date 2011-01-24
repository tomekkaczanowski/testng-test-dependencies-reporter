package pl.kaczanowscy.tomek.test;

import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.kaczanowscy.tomek.testng.reporter.TestDependenciesReporter;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestA;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestB;
import pl.kaczanowscy.tomek.testng.reporter.tests.TestC;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static org.testng.Assert.assertTrue;

@Test
public class TestDependenciesReporterTest {

    // TODO temp files instead?

    private TestNG testNG;

    @BeforeMethod
    public void setUp() {
        testNG = new TestNG();
        testNG.setListenerClasses(Arrays.asList(new Class[]{TestDependenciesReporter.class}));
        testNG.setOutputDirectory(TestDependenciesReporter.DEFAULT_OUTPUT_DIR);
    }

    @AfterMethod
    public void tearDown() {
        removeOutputDir();
    }

    public void testOneTestClass() throws FileNotFoundException {
        testNG.setTestClasses(new Class[] {TestA.class});
        testNG.run();

        String text = readDotFile();
        assertTrue(text.contains("digraph testDependencies {"), text);
        assertTrue(text.contains("test_A_1"), text);
        assertTrue(text.contains("test_A_2"), text);
        assertTrue(text.contains("test_A_3"), text);

        assertTrue(text.contains("test_A_2 -> test_A_1"), text);
        assertTrue(text.contains("test_A_3 -> test_A_2"), text);
        assertTrue(text.contains("}"), text);
    }

    private String readDotFile() throws FileNotFoundException {
        File dotFile = new File(TestDependenciesReporter.DEFAULT_OUTPUT_DIR + "/dotFile.dot");
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
        File outputDir = new File(TestDependenciesReporter.DEFAULT_OUTPUT_DIR);
        outputDir.delete();
    }

}
