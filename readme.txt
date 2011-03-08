Implementation of a fancy TestNG reporter (IReporter) which visualizes test dependencies as a diagram (Dot / Graphviz). For more information

* read this blog post: http://is.gd/mFIh3J
* and some mailing list discussions: http://is.gd/SuDQW7 http://is.gd/ySRHZU

run
  gradle test

and you will find dot file in:
  build/reports/test-dependencies/dotFile.dot

and if graphviz is on classpath, you will also find generated graph there:
  build/reports/test-dependencies/graph.png


Work in progress !

TODO
  * be able to generate dependencies graph without running tests (but of cours no failed/skipped info then!)
  * better layout of groups and methods
  * show why group failed (which methods went red within this group)
  * add configuration (report.properties on classpath?)
  * various detail levels (e.g. only test methods depended-upon/depending, all methods etc.)
