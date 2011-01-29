Implementation of a fancy TestNG reporter (IReporter) which visualizes test dependencies as a diagram (Dot / Graphviz). See this thread: http://groups.google.com/group/testng-dev/browse_thread/thread/48429064a526e809

run
  gradle test

and you will find dot file in:
  build/reports/test-dependencies/dotFile.dot

and if graphviz is on classpath, you will also find generated graph there:
  build/reports/test-dependencies/graph.png


Work in progress !

TODO
  * better layout of groups and methods
  * show why group failed
  * add configuration (report.properties on classpath?)
  * various detail levels (e.g. only test methods depended-upon/depending, all methods etc.)
