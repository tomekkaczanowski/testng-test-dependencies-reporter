package pl.kaczanowscy.tomek.testng.reporter;

//import org.apache.log4j.Logger;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestNGMethod;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.util.*;

public class TestDependenciesReporter implements IReporter {

    //Logger log = Logger.getLogger("blah");

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
    //Set<ConfigurationMethod> configs = new HashSet<ConfigurationMethod>();
    Collection<ITestNGMethod> failedMethods;
    Collection<ITestNGMethod> failedConfigurations;
    Collection<ITestNGMethod> skippedMethods;
    Collection<String> failedGroups = new HashSet<String>();

    // FIXME to be removed? there is no such thing as skipped group
    Collection<String> skippedGroups = new HashSet<String>();

    Set<String> uniqueGroups = new HashSet<String>();

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        analyzeTestDependencies(suites);
        File dotFile = dotGenerator.generateDotFile(outputDirectory);
        diagramGenerator.generateDiagram(outputDirectory, dotFile);
    }

    private void analyzeTestDependencies(List<ISuite> suites) {
        for (ISuite suite : suites) {
            log("isuite: " + suite.getName());

            // FIXME why implication that noColor = all_tests_were skipped?
            // in case of noColor all tests were probably skipped
            if (!noColor) {
                // FIXME will not work for more than one suite
                for (Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet()) {
                    failedConfigurations = entry.getValue().getTestContext().getFailedConfigurations().getAllMethods();
                    failedMethods = entry.getValue().getTestContext().getFailedTests().getAllMethods();
                    skippedMethods = entry.getValue().getTestContext().getSkippedTests().getAllMethods();
                }
            } else {
                // TODO what happens with configs and noColor - does it work?
                failedConfigurations = new ArrayList<ITestNGMethod>();
                failedMethods = new ArrayList<ITestNGMethod>();
                skippedMethods = new ArrayList<ITestNGMethod>();
            }

            for (ITestNGMethod method : failedMethods) {
            log("failed method: " + getClassAndMethodString(method));
            for (String depMethod : method.getMethodsDependedUpon()) {
                log("  dep on: " + depMethod);
            }
        }

            for (ITestNGMethod method : failedConfigurations) {
            log("failed config: " + getClassAndMethodString(method));
            for (String depMethod : method.getMethodsDependedUpon()) {
                log("  dep on: " + depMethod);
            }
        }
        for (ITestNGMethod method : skippedMethods) {
            log("skipped method: " + getClassAndMethodString(method));
            for (String depMethod : method.getMethodsDependedUpon()) {
                log("  dep on: " + depMethod);
            }
                for (String depGroup : method.getGroupsDependedUpon()) {
                log("  dep on: " + depGroup);
            }
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

                log("entry: " + entry.getKey());
                for (ITestNGMethod method : entry.getValue()) {
                    TestMethod tempMet = new TestMethod(method.getMethodName());
                    methods.add(tempMet);
                    //log("method: " + method.getMethodName() + " added to methods");
                    //log(method.getMethodName() + " groups: " + Arrays.deepToString(method.getGroups()));
                    //log(method.getMethodName() + " dep groups: " + Arrays.deepToString(method.getGroupsDependedUpon()));
                    for (String dependedUponMethod : method.getMethodsDependedUpon()) {
                        tempMet.addMethod(dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".") + 1));
                        //log("method: " + method.getMethodName() + " deps on " + dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".") + 1) + " added to methods");
                        //log(method.getMethodName() + " depends upon " + dependedUponMethod.substring(dependedUponMethod.lastIndexOf(".")+1));
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

        for (ITestNGMethod method : failedMethods) {
            log("failed method: " + getClassAndMethodString(method));
            for (String depMethod : method.getMethodsDependedUpon()) {
                log("  dep on: " + depMethod);
            }
        }

            for (ITestNGMethod method : failedConfigurations) {
            log("failed config: " + getClassAndMethodString(method));
            for (String depMethod : method.getMethodsDependedUpon()) {
                log("  dep on: " + depMethod);
            }}
        for (ITestNGMethod method : skippedMethods) {
            log("skipped method: " + getClassAndMethodString(method));
            for (String depMethod : method.getMethodsDependedUpon()) {
                log("  dep on: " + depMethod);
            }
                for (String depGroup : method.getGroupsDependedUpon()) {
                log("  dep on: " + depGroup);
            }
        }

        for (TestMethod method : methods) {
            //log("method: " + method.getName());
            uniqueGroups.addAll(method.getGroupsDepUpon());
        }
    }

    public interface DotFileGenerator {
        File generateDotFile(String outputDirectory);
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
            this.cssFailedMethod = "color=red,style=filled";
            this.cssSkippedMethod = "color=yellow,style=filled";
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


        public File generateDotFile(String outputDirectory) {
            File dotFile = new File(outputDirectory + FILE_SEPARATOR + "dotFile.dot");
            Writer out = null;
            try {
                out = new OutputStreamWriter(new FileOutputStream(dotFile));
                out.write("digraph testDependencies {" + NEWLINE);
                String failedConfigMethod = "";
                if (!failedConfigurations.isEmpty()) {
                    out.write("compound=true;" + NEWLINE); // otherwise lhead will not work
                    out.write(NEWLINE + "subgraph clusterFailedConfigs {" + NEWLINE);
                    out.write("label = \"Failed Configuration Methods\"" + NEWLINE);
                    //out.write("failedConfig;" + NEWLINE);
                    // TODO extract CSS
                    // FIXME does not work - expected border around failedConfigs cluster
                    out.write("color=blue;" + NEWLINE);
                    //out.write("node [color=white];" + NEWLINE);
                    for (ITestNGMethod failedConfig : failedConfigurations) {
                        out.write(getClassAndMethodString(failedConfig) + " [label=\"" + failedConfig.getMethodName() + "\"];" + NEWLINE);
                        failedConfigMethod = getClassAndMethodString(failedConfig);
                    }
                    out.write("}" + NEWLINE+NEWLINE);

                }
                out.write("  rankdir=BT;" + NEWLINE);

                for (ITestNGMethod failedMethod : failedMethods) {
                    //log("failed method: " + failedMethod.getMethodName());
                    out.write(getClassAndMethodString(failedMethod) + "[label=\"" + failedMethod.getMethodName() + "\"," + cssFailedMethod + "];"+ NEWLINE);
                    for (String depMethod : failedMethod.getMethodsDependedUpon()) {
                        out.write(getClassAndMethodString(failedMethod) + DEPENDS_UPON + getClassAndMethodString(depMethod) + NEWLINE);
                                    }
                }
                for (ITestNGMethod skippedMethod : skippedMethods) {
                    //log("skipped method: " + skippedMethod.getMethodName());
                    out.write(getClassAndMethodString(skippedMethod) + "[label=\"" + skippedMethod.getMethodName() + "\"," + cssSkippedMethod + "];"+ NEWLINE);
                    if (skippedMethod.getMethodsDependedUpon().length > 0) {
                        for (String depMethod : skippedMethod.getMethodsDependedUpon()) {
                            out.write(getClassAndMethodString(skippedMethod) + DEPENDS_UPON + getClassAndMethodString(depMethod) + NEWLINE);
                        }
                    }
                    else {
                        if (skippedMethod.getGroupsDependedUpon().length > 0) {
                        for (String depGroup : skippedMethod.getGroupsDependedUpon()) {
                            out.write(getClassAndMethodString(skippedMethod) + DEPENDS_UPON + "Group_" + depGroup + NEWLINE);
                        }

                    }
                        else {
                        out.write(getClassAndMethodString(skippedMethod) + DEPENDS_UPON + failedConfigMethod + NEWLINE);
                        }
                    }
                }

/**
                // let us gather all groups that are
                for (TestMethod method : methods) {

                    //log(method.getName() + " no groups: " + method.getGroupsDepUpon().isEmpty());
                    //log(method.getName() + " no methods: " + method.getMethodsDepUpon().isEmpty());

                    //if (method.getGroupsDepUpon().isEmpty() && method.getMethodsDepUpon().isEmpty()) {
                    //    out.write(method.getName() + DEPENDS_UPON + "failedConfig" + NEWLINE);
                    //}


//                    // draw dependency edges: method to method
//                    for (String dependedUponMethod : method.getMethodsDepUpon()) {
//                        out.write(method.getName() + DEPENDS_UPON + dependedUponMethod + NEWLINE);
//                    }

                    // draw dependency edges: method to group
                    for (String dependedUponGroup : method.getGroupsDepUpon()) {
                        out.write(method.getName() + DEPENDS_UPON + dependedUponGroup + NEWLINE);
                    }
                }
                */

                // draw group nodes
                for (String group : uniqueGroups) {
                    // failed - red
                    if (failedGroups.contains(group)) {
                        out.write(getGroupName(group) + cssFailedGroup + NEWLINE);
                    }
                    // skipped - yello
                    // FIXME to be removed? there is no such thing as skipped group
                    else if (skippedGroups.contains(group)) {
                        out.write(getGroupName(group) + cssSkippedGroup + NEWLINE);
                    }
                    // normal groups
                    else {
                        out.write(getGroupName(group) + cssGroup + NEWLINE);
                    }
                }

                // all groups on the same level
                if (!uniqueGroups.isEmpty()) {
                    out.write("{ rank = same;");
                    for (String group : uniqueGroups) {
                        out.write(" \"Group_" + group + "\"; ");
                    }
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

        private String getGroupName(String group) {
            return "Group_" + group;
        }
    }



        public static String getClassAndMethodString(ITestNGMethod method) {
            return method.getRealClass().getSimpleName() + "_" + method.getMethodName();
        }



        public static String getClassAndMethodString(String depMethod) {
            String methodName = depMethod.substring(depMethod.lastIndexOf(".") + 1);
            depMethod = depMethod.substring(0, depMethod.lastIndexOf("."));
            String className = depMethod.substring(depMethod.lastIndexOf(".") + 1);
            return className + "_" + methodName;
        }
    private void log(String msg) {
        System.out.println(msg);
        //System.err.println(msg);
        //log.warn(msg);
    }

    public interface DiagramGenerator {
        void generateDiagram(String outputDirectory, File dotFile);
    }

    /**
     *  Generates png file out of dot file.
     */
    class PngDiagramGenerator implements DiagramGenerator {

        public void generateDiagram(String outputDirectory, File dotFile) {
            try {
                Runtime run = Runtime.getRuntime();
                Process pr = run.exec("dot " + dotFile.getAbsolutePath() + " -Tpng -o" + outputDirectory + FILE_SEPARATOR + "graph.png");
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
