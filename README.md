Nexus Token Auth Manager
========================

[![Build Status](https://travis-ci.org/InfoSec812/nexus-token-manager.svg)](https://travis-ci.org/InfoSec812/nexus-token-manager)

[Sonatype Nexus](http://www.sonatype.org/nexus/) is a Java based artifact repository
where developers can publish their code artifacts for reuse in other projects. The
open source version of that product does this job phenomenally well, with some small
exceptions. One of those exceptions is that the open source version does not support
token authentication out of the box. 

It does however have a nod to those who might like to implement this capability 
themselves. Built in to the OSS version is a plugin called the *RUT Auth Plugin*.
This is a terse name for "Remote User Token" authentication plugin. What this
plugin does is it allows an external service/application/proxy to set a header
on requests to the Nexus server and this header specifies the username to be
authenticated as. No other information is required. This means that if you 
enable this plugin and don't put some sort of filter in front of your Nexus
server, anyone can get it just by setting a header on their requests.

What this application does is use the stand-alone Nexus OSS distribution.
That archive includes an installation of Jetty which loads and runs Nexus. This
application will ALSO load itself into that same Jetty container and run at
a different endpoint (/nexusmanager). In addition, it will load a filter
library into the Jetty configuration which will prevent externally set 
REMOTE_USER headers from being accepted. That filter will also check for a 
attached AUTH_TOKEN header. That AUTH_TOKEN header will be verified against
this application's built-in database to either allow or deny access to the
Nexus server by setting or unsetting the appropriate REMOTE_USER header to
be passed on to the underlying servlets.

Configuration
-------------

By default, the application uses a file-based instance of [HSQLDB](http://hsqldb.org/)
to store token information. Changing the web.xml and including the appropriate
JDBC driver will allow you to override that behavior.

To configure where the application looks for a Nexus server to authenticate against,
you would also modify some of the initParam values in the applications's web.xml file.

The web.xml file can be found at <root>/src/main/webapp/WEB-INF/web.xml
