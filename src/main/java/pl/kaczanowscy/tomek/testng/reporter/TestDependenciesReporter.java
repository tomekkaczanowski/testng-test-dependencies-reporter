package pl.kaczanowscy.tomek.testng.reporter;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestNGMethod;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestDependenciesReporter implements org.testng.IReporter {

    // FIXME should rather use outputDirectory injected by TestNG, see method parameters below
    public final static String DEFAULT_OUTPUT_DIR = "build/reports/test-dependencies";
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String DEPENDS_UPON = " -> ";


    public void generateReport(java.util.List<XmlSuite> xmlSuites, java.util.List<ISuite> suites, java.lang.String outputDirectory) {

        File blah = new File(DEFAULT_OUTPUT_DIR + "/dotFile.dot");
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(blah));
            out.write("digraph testDependencies {"  + NEWLINE);
            // TODO
            for (ISuite suite : suites) {
                System.out.println("isuite: " + suite.getName());
                out.write("isuite: " + suite.getName()  + NEWLINE);
                for (Map.Entry<String,Collection<ITestNGMethod>> entry : suite.getMethodsByGroups().entrySet()) {
                    out.write("entry: " + entry.getKey() + NEWLINE);
                    for (ITestNGMethod method :  entry.getValue()) {
                        out.write("\tmethod: " + method.getMethodName() + NEWLINE);
                        for (String dependedUponMethod : method.getMethodsDependedUpon()) {
                            out.write(method.getMethodName() + DEPENDS_UPON + dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".")+1) + NEWLINE);
                            //out.write("dep upon method: " + dependedUponMethod + NEWLINE);
                            //String[] tokens = dependedUponMethod.split("\\.");
                            //out.write(Arrays.deepToString(tokens));
                            //out.write(method.getMethodName() + DEPENDS_UPON + tokens[tokens.length-2] + "." + tokens[tokens.length-1]);
                        }
                    }
                }
            }


            for (XmlSuite suite : xmlSuites) {
                System.out.println("xmlsuite: " + suite);
                out.write("xmlsuite: " + suite + NEWLINE);
                List<XmlTest> tests = suite.getTests();
                for (XmlTest test: tests) {
                    out.write("test: " + test + NEWLINE);
                    for (XmlClass clazz : test.getXmlClasses()) {
                        out.write("clazz: " + clazz + NEWLINE);
                        for (XmlInclude include : clazz.getIncludedMethods()) {
                            out.write("include: " + include + NEWLINE);
                        }
                    }
                }
            }
            out.write("}");

            // FIXME exception handling
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            try {
                out.close();
            } catch (IOException e) {
                // FIXME
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    // TODO dependencies-report.properties
}
