package pl.kaczanowscy.tomek.testng.reporter;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.util.*;

public class TestDependenciesReporter implements IReporter {

    // FIXME should rather use outputDirectory injected by TestNG, see method parameters below
    public final static String DEFAULT_OUTPUT_DIR = "build/reports/test-dependencies";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private DotFileGenerator dotGenerator;
    private final DiagramGenerator diagramGenerator;

    private boolean noColor = false;

    public TestDependenciesReporter() {
        this.dotGenerator = new ColoredDotFileGenerator();
        this.diagramGenerator = new PngDiagramGenerator();
    }

    /**
     * Should output graph be colored ? By default it is.
     */
    public void noColor() {
        this.noColor = true;
        this.dotGenerator = new ColorlessDotFileGenerator();
    }

    Set<TestMethod> methods = new HashSet<TestMethod>();
    Collection<ITestNGMethod> failedMethods;
    Collection<ITestNGMethod> skippedMethods;
    Collection<String> failedGroups = new HashSet<String>();

    // FIXME to be removed? there is no such thing as skipped group
    Collection<String> skippedGroups = new HashSet<String>();

    Set<String> uniqueGroups = new HashSet<String>();

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        analyzeTestDependencies(suites);
        File dotFile = dotGenerator.generateDotFile();
        diagramGenerator.generateDiagram(dotFile);
    }

    private void analyzeTestDependencies(List<ISuite> suites) {
        for (ISuite suite : suites) {
            //log("isuite: " + suite.getName());

            // FIXME why implication that noColor = all_tests_were?
            // in case of noColor all tests were probably skipped
            if (!noColor) {
                // FIXME will not work for more than one suite
                for (Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet()) {
                    failedMethods = entry.getValue().getTestContext().getFailedTests().getAllMethods();
                    skippedMethods = entry.getValue().getTestContext().getSkippedTests().getAllMethods();
                }
            } else {
                failedMethods = new ArrayList<ITestNGMethod>();
                skippedMethods = new ArrayList<ITestNGMethod>();
            }

            // the only way to learn about which groups failed is by checking groups that failed methods belong to
            for (ITestNGMethod method : failedMethods) {
                failedGroups.addAll(Arrays.asList(method.getGroups()));
            }


            // FIXME to be removed? there is no such thing as skipped group
            for (ITestNGMethod method : skippedMethods) {
                skippedGroups.addAll(Arrays.asList(method.getGroups()));
            }

            skippedGroups.removeAll(failedGroups);

            for (Map.Entry<String, Collection<ITestNGMethod>> entry : suite.getMethodsByGroups().entrySet()) {

                //log("entry: " + entry.getKey());
                for (ITestNGMethod method : entry.getValue()) {
                    TestMethod tempMet = new TestMethod(method.getMethodName(), "");
                    methods.add(tempMet);
                    //log("method: " + method.getMethodName());
                    //log(method.getMethodName() + " groups: " + Arrays.deepToString(method.getGroups()));
                    //log(method.getMethodName() + " dep groups: " + Arrays.deepToString(method.getGroupsDependedUpon()));
                    for (String dependedUponMethod : method.getMethodsDependedUpon()) {
                        tempMet.addMethod(dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".") + 1));
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


        for (TestMethod method : methods) {
            uniqueGroups.addAll(method.getGroupsDepUpon());
        }
    }

    public interface DotFileGenerator {
        File generateDotFile();
    }

    /**
     * No matter if it failed or was skipped - it will be painted white.
     */
    class ColorlessDotFileGenerator extends DefaultDotFileGenerator {
        ColorlessDotFileGenerator() {
            super();
            this.cssFailedMethod = "";
            this.cssSkippedMethod = "";
            this.cssFailedGroup = "";
            this.cssSkippedGroup = "";
        }
    }

    /**
     * Colors failed with red and skipped with yellow.
     */
    class ColoredDotFileGenerator extends DefaultDotFileGenerator {

        ColoredDotFileGenerator() {
            this.cssFailedMethod = " [color=red,style=filled];";
            this.cssSkippedMethod = " [color=yellow,style=filled];";
            this.cssFailedGroup = " [shape=box,peripheries=2,color=red,style=filled];";
            this.cssSkippedGroup = " [shape=box,peripheries=2,color=yellow,style=filled];";
        }

    }

    abstract class DefaultDotFileGenerator implements DotFileGenerator {

        private final String NEWLINE = System.getProperty("line.separator");

        private final String DEPENDS_UPON = " -> ";
        protected String cssFailedMethod;
        protected String cssSkippedMethod;
        protected String cssFailedGroup;
        protected String cssSkippedGroup;
        protected String cssGroup = " [shape=box,peripheries=2];";


        public File generateDotFile() {
            File dotFile = new File(DEFAULT_OUTPUT_DIR + FILE_SEPARATOR + "dotFile.dot");
            Writer out = null;
            try {
                out = new OutputStreamWriter(new FileOutputStream(dotFile));
                out.write("digraph testDependencies {" + NEWLINE);
                out.write("  rankdir=BT;" + NEWLINE);

                for (ITestNGMethod failedMethod : failedMethods) {
                    //log("failed method: " + failedMethod.getMethodName());
                    out.write(failedMethod.getMethodName() + cssFailedMethod + NEWLINE);
                }
                for (ITestNGMethod skippedMethod : skippedMethods) {
                    //log("skipped method: " + skippedMethod.getMethodName());
                    out.write(skippedMethod.getMethodName() + " " + cssSkippedMethod + NEWLINE);
                }

                // let us gather all groups that are
                for (TestMethod method : methods) {

                    // draw dependency edges: method to method
                    for (String dependedUponMethod : method.getMethodsDepUpon()) {
                        out.write(method.getName() + DEPENDS_UPON + dependedUponMethod + NEWLINE);
                    }

                    // draw dependency edges: method to group
                    for (String dependedUponGroup : method.getGroupsDepUpon()) {
                        out.write(method.getName() + DEPENDS_UPON + dependedUponGroup + NEWLINE);
                    }
                }

                // draw group nodes
                for (String group : uniqueGroups) {
                    // failed - red
                    if (failedGroups.contains(group)) {
                        out.write(group + cssFailedGroup + NEWLINE);
                    }
                    // skipped - yello
                    // FIXME to be removed? there is no such thing as skipped group
                    else if (skippedGroups.contains(group)) {
                        out.write(group + cssSkippedGroup + NEWLINE);
                    }
                    // normal groups
                    else {
                        out.write(group + cssGroup + NEWLINE);
                    }
                }

                // all groups on the same level
                out.write("{ rank = same;");
                for (String group : uniqueGroups) {
                    out.write(" \"" + group + "\"; ");
                }
                out.write("}" + NEWLINE);
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
                out.write("}" + NEWLINE);

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
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    public interface DiagramGenerator {
        void generateDiagram(File dotFile);
    }

    /**
     *  Generates png file out of dot file.
     */
    class PngDiagramGenerator implements DiagramGenerator {

        public void generateDiagram(File dotFile) {
            try {
                Runtime run = Runtime.getRuntime();
                Process pr = run.exec("dot " + dotFile.getAbsolutePath() + " -Tpng -o" + DEFAULT_OUTPUT_DIR + FILE_SEPARATOR + "graph.png");
                pr.waitFor();
                //BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

                // should the output be really printed?
//            String line;
//            while ( ( line = buf.readLine() ) != null )
//            {
//                System.out.println(line) ;
//            }
                // FIXME how to handle exceptions in listener?
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    // TODO dependencies-report.properties

    /**
     * Represents a single test method and gathers information about groups and methods it depends upon.
     */
    private class TestMethod {

        private final String name;

        //private final String state;

        private final Set<String> methodsDepUpon = new HashSet<String>();
        private final Set<String> groupsDepUpon = new HashSet<String>();

        public TestMethod(String name, String state) {
            this.name = name;
            //this.state = state;
        }

        public String getName() {
            return name;
        }

        public boolean addMethod(String method) {
            return methodsDepUpon.add(method);
        }

        public boolean addGroup(String group) {
            return groupsDepUpon.add(group);
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
}
