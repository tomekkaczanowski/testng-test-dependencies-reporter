Implementation of a fancy TestNG reporter (IReporter) which visualizes test dependencies as a diagram (Dot / Graphviz). For more information (and some examples):

* read this blog post: http://is.gd/mFIh3J
* and some mailing list discussions: http://is.gd/SuDQW7 http://is.gd/ySRHZU

----------
Disclaimer
----------

Work in progress!

----------
Create JAR
----------

To produce JAR with reporter (so you can use it with Maven, Ant or Gradle later) run
  gradle build
and you will find test-dependencies-reporter-VERSION.jar in ./build

---------
Self-Test
---------

run
  gradle test

and you will find dot file in:
  test-output/dotFile.dot

and if graphviz is on classpath, you will also find generated graph there:
  test-output/graph.png

-----
Maven
-----

To use with Maven you need to update your pom.xml:

a) add dependency (watch out the version, it might have changed)

    <dependency>
      <groupId>pl.kaczanowscy.tomek.testng</groupId>
      <artifactId>testng-dependencies-reporter</artifactId>
      <version>1.0-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>


b) configure surefire plugin (there is lot of incostency in surefire, e.g. documentation says you should use "reporter" property which does not work, sorry, not my fault)

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
          <version>2.7.1</version>
        <configuration>
          <properties>
            <property>
              <name>listener</name>
              <value>pl.kaczanowscy.tomek.testng.reporter.TestDependenciesReporter</value>
            </property>
          </properties>
          <forkMode>never</forkMode>
        </configuration>
      </plugin>

c) if you want to have diagram generate WITHOUT running tests, replace surefire configuration with the following:
TODO

-----
FIXME
-----

  * make it work properly when FQN is given, and not only method name

----
TODO
----
  
  * should be configured:
    * only methods or class_method (can not use .)
    * classes in subgraphs or not
    * how? - report.properties on classpath?
  * be able to generate dependencies graph without running tests (but of cours no failed/skipped info then!)
    * done, but need it to be configurable (right now you can do it programmaticaly only)
  * better layout of groups and methods
  * methods of one class should be printed in cluster
  * show why group failed (which methods went red within this group)
  * various detail levels (e.g. only test methods depended-upon/depending, all methods etc.

---------
CHANGELOG
---------

* since 2011-02-14
  * changed output dir: dot file and png is generated in TestNG default output
  * possible to draw diagram of test dependencies without running tests
  * works for FQN, not only for method names, so now both are working as expected:
    // will be skipped because depends on failed method
    @Test(dependsOnMethods = "pl.kaczanowscy.tomek.testng.reporter.tests.complex.TestA.failure")
    public void skipped() {
        assert true;
    }

    // will be skipped because depends on skipped method
    @Test(dependsOnMethods = "skipped")
    public void alsoSkipped() {
        assert false;
    }
  * if configuration method fails (e.g. @BeforeClass) then it is shown that configuration is a culprit
    * it is not possible to point exactly to the right failed config method - diagram shows all failed config methods and adds dependency on cluster
