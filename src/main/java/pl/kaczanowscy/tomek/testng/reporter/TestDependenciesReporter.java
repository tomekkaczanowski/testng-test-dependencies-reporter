package pl.kaczanowscy.tomek.testng.reporter;

import org.testng.ISuite;
import org.testng.ITestNGMethod;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.*;
import java.util.*;

public class TestDependenciesReporter implements org.testng.IReporter {

    // FIXME should rather use outputDirectory injected by TestNG, see method parameters below
    public final static String DEFAULT_OUTPUT_DIR = "build/reports/test-dependencies";
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String DEPENDS_UPON = " -> ";

    // FIXME add javadoc
    private class TestMethod {

        private final String name;

        private final Set<String> methodsDepUpon = new HashSet<String>();
        private final Set<String> groupsDepUpon = new HashSet<String>();

        public TestMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean addMethod(String method) {
            return methodsDepUpon.add(method);
        }

        public boolean addGroup(String group) {
            return methodsDepUpon.add(group);
        }

        public Set<String> getMethodsDepUpon() {
            return Collections.unmodifiableSet(methodsDepUpon);
        }

        public Set<String> getGroupsDepUpon() {
            return Collections.unmodifiableSet(groupsDepUpon);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestMethod that = (TestMethod) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    Set<TestMethod> methods = new HashSet<TestMethod>();

    public void generateReport(java.util.List<XmlSuite> xmlSuites, java.util.List<ISuite> suites, java.lang.String outputDirectory) {

            for (ISuite suite : suites) {
                //log("isuite: " + suite.getName());
                for (Map.Entry<String,Collection<ITestNGMethod>> entry : suite.getMethodsByGroups().entrySet()) {
                    //log("entry: " + entry.getKey());
                    for (ITestNGMethod method :  entry.getValue()) {
                        TestMethod tempMet = new TestMethod(method.getMethodName());
                        methods.add(tempMet);
                        //log("method: " + method.getMethodName());
                        //log(method.getMethodName() + " groups: " + Arrays.deepToString(method.getGroups()));
                        //log(method.getMethodName() + " dep groups: " + Arrays.deepToString(method.getGroupsDependedUpon()));
                        for (String dependedUponMethod : method.getMethodsDependedUpon()) {
                            tempMet.addMethod(dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".")+1));
                            //log(method.getMethodName() + DEPENDS_UPON + dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".")+1));
                            //log("dep upon method: " + dependedUponMethod);
                            //String[] tokens = dependedUponMethod.split("\\.");
                            //log(Arrays.deepToString(tokens));
                            //log(method.getMethodName() + DEPENDS_UPON + tokens[tokens.length-2] + "." + tokens[tokens.length-1]);

                        }
                        for (String dependedUponGroup : method.getGroupsDependedUpon()) {
                            tempMet.addGroup(dependedUponGroup);
                            //log(method.getMethodName() + DEPENDS_UPON + dependedUponGroup);
                            //log("dep upon group: " + dependedUponGroup);
                        }
                    }
                }
            }

        File dotFile = generateDotFile(suites);
        generateDiagram(dotFile);

        }

    private File generateDotFile(List<ISuite> suites) {
        File dotFile = new File(DEFAULT_OUTPUT_DIR + "/dotFile.dot");
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(dotFile));
            out.write("digraph testDependencies {"  + NEWLINE);
            out.write("  rankdir=BT;" + NEWLINE);
            for (TestMethod method : methods) {
                for (String dependedUponMethod : method.getMethodsDepUpon()) {
                    out.write(method.getName() + DEPENDS_UPON + dependedUponMethod + NEWLINE);
                }
                for (String dependedUponGroup : method.getGroupsDepUpon()) {
                    out.write(method.getName() + DEPENDS_UPON + dependedUponGroup + NEWLINE);
                }
            }
/*            for (ISuite suite : suites) {
                //System.out.println("isuite: " + suite.getName());
                // out.write("isuite: " + suite.getName()  + NEWLINE);
                for (Map.Entry<String,Collection<ITestNGMethod>> entry : suite.getMethodsByGroups().entrySet()) {
                    //out.write("entry: " + entry.getKey() + NEWLINE);
                    for (ITestNGMethod method :  entry.getValue()) {
                        //out.write("\tmethod: " + method.getMethodName() + NEWLINE);
                        //out.write(method.getMethodName() + " groups: " + Arrays.deepToString(method.getGroups()) + NEWLINE);
                        //out.write(method.getMethodName() + " dep groups: " + Arrays.deepToString(method.getGroupsDependedUpon()) + NEWLINE);
                        for (String dependedUponMethod : method.getMethodsDependedUpon()) {
                            out.write(method.getMethodName() + DEPENDS_UPON + dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".")+1) + NEWLINE);
                            //out.write("dep upon method: " + dependedUponMethod + NEWLINE);
                            //String[] tokens = dependedUponMethod.split("\\.");
                            //out.write(Arrays.deepToString(tokens));
                            //out.write(method.getMethodName() + DEPENDS_UPON + tokens[tokens.length-2] + "." + tokens[tokens.length-1]);

                        }
                        for (String dependedUponGroup : method.getGroupsDependedUpon()) {
                            out.write(method.getMethodName() + DEPENDS_UPON + dependedUponGroup + NEWLINE);
                            //out.write("dep upon group: " + dependedUponGroup + NEWLINE);
                        }
                    }
                }
            }*/
            out.write("}");

            // FIXME exception handling
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // FIXME
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return dotFile;
    }

    // TODO read parameters (e.g. output format) from properties
    private void generateDiagram(File dotFile) {
        try {
            Runtime run = Runtime.getRuntime() ;
            Process pr =  run.exec("dot " + dotFile.getAbsolutePath() + " -Tpng -o" + DEFAULT_OUTPUT_DIR + "/graph.png");
            pr.waitFor() ;
            BufferedReader buf = new BufferedReader( new InputStreamReader( pr.getInputStream() ) ) ;
            String line;
            while ( ( line = buf.readLine() ) != null )
            {
                System.out.println(line) ;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    // TODO dependencies-report.properties
}
