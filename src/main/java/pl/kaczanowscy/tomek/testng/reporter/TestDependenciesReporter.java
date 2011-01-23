package pl.kaczanowscy.tomek.testng.reporter;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestDependenciesReporter implements org.testng.IReporter {

    // FIXME should rather use outputDirectory injected by TestNG, see method parameters below
    public final static String DEFAULT_OUTPUT_DIR = "build/reports/test-dependencies";


    public void generateReport(java.util.List<XmlSuite> xmlSuites, java.util.List<ISuite> suites, java.lang.String outputDirectory) {

        File blah = new File(DEFAULT_OUTPUT_DIR + "/blah.dot");
        try {
            FileWriter writer = new FileWriter(blah);
            for (ISuite suite : suites) {
                        System.out.println("isuite: " + suite.getName());
                        writer.write("isuite: " + suite.getName());
                    }


        for (XmlSuite suite : xmlSuites) {
            System.out.println("xmlsuite: " + suite);
            writer.write("xmlsuite: " + suite);
                 }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    // TODO dependencies-report.properties
}
