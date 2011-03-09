Implementation of a fancy TestNG reporter (IReporter) which visualizes test dependencies as a diagram (Dot / Graphviz). For more information:

* read this blog post: http://is.gd/mFIh3J
* and some mailing list discussions: http://is.gd/SuDQW7 http://is.gd/ySRHZU

run
  gradle test

and you will find dot file in:
  test-output/dotFile.dot

and if graphviz is on classpath, you will also find generated graph there:
  test-output/graph.png


Work in progress !

FIXME
  * make it work properly when FQN is given, and not only method name

TODO
  * if configuration method fails (e.g. @BeforeClass) then it should be shown that configuration is a culprit; right now test is skipped and you don't know why
    * it is not possible to point exactly to the right failed config method - at least show all failed config methods and add dependency on cluster
  * should be configured:
    * only methods or class_method (can not use .)
    * classes in subgraphs or not
  * be able to generate dependencies graph without running tests (but of cours no failed/skipped info then!)
    * done, but need it to be configurable (right now you can do it programmaticaly only)
  * better layout of groups and methods
  * methods of one class should be printed in cluster
  * show why group failed (which methods went red within this group)
  * add configuration (report.properties on classpath?)
  * various detail levels (e.g. only test methods depended-upon/depending, all methods etc.

CHANGELOG
* since 2011-02-14
  * changed output dir: dot file and png is generated in TestNG default output
  * possible to draw diagram of test dependencies without running tests
