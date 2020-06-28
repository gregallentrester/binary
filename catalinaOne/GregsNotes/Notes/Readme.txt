                                    Tomcat Catalina 
                                        7.0.50
                              Deployment Considerations
 


Most of this is appplicable to Tomcat 8. 


Notable artifacts present in the TC standard image:

   -   tomcat7-websocket.jar
   -   websocket-api.jar

   -   tomcat-i18n-es.jar
   -   tomcat-i18n-fr.jar
   -   tomcat-i18n-ja.jar


To the TC standard image, add this artifact:

  log4j-1.2.17.jar
  
  

A.  Configuring Catalina's Host *.

    The Host element is found in the server.xml file.  
    The default value of the 'deployXML' attribute is 'true'.
      
       <Host name="localhost"  appBase="webapps"
             unpackWARs="true" autoDeploy="true"
             deployXML="false">
            
            
      For security reasons, the value of the 'deployXML' attribute value 
      (an attribute of the Host element)  attribute has been (and should be) 
      set to a set to a value of 'false'.  
      
      This in turn, prevents applications from interacting with the container's 
      configuration; it does so by disabling parsing of the 'context.xml' file 
      embedded inside the application (located at /META-INF/context.xml). 
      
      When the value of the 'deployXML' attribute is set to 'false', the 
      server admin is responsible for providing an external context 
      configuration file, and placing it in this logical path:
      
            $CATALINA_HOME/conf/[enginename]/[hostname]/
          

      ...and this location (typically) equates to...
      
            $CATALINA_HOME/conf/Catalina/localhost/


      The responsibility put on the application deployer/server admin
      to maintain the  context configuration file is more than justified
      by the increase in security gained.
      
      The extent of the responsibility is that there be present, an individual 
      file at ../META-INF/context.xml inside the application artifacts. 
      
      Specifically, if the web application is packaged as a WAR, the  
      ../META-INF/context.xml will be copied to the appropriate place on 
      the server - $CATALINA_HOME/conf/Catalina[enginename]/[hostname]/ -
      and will be renamed to match the application's context path. 
      
      Finally, once this file exists, it won't be replaced if a new WAR having 
      a newer /META-INF/context.xml is deployed to the host's appBase.



    NOTE (from a previous nighmare):

    We have no need for symlinks, since we are using a reference implementation 
    of the Servlet Spec:  Tomcat.  When we tried using resin, it could not be run 
    successfully in Windows, so we had a resin image on a Linux VM (hence 
    the need for symlinks).
    
    Even the extremely non-standard way in which we deploy UI artifacts to 
    Tomcat (potentially using symlinks as well) needs to change.  Most - no all - 
    shops I have ever worked in placed UI artifacts in the WEB-INF folder.
    
    However, if non-standard practices above remain in place, we will need
    either a junction or symlinks.  In that case, add an 'allowLinking' attribute 
    to the <Context> element in the context.xml file and assign it a value of 'true':
    
    
        <Context allowLinking="true">
    
    Here is the carveat against using this practice:
    
        "The 'allowLinking' flag MUST NOT be set to true on the Windows 
        platform (or any other OS which does not have a case sensitive 
        filesystem), as it will disable case sensitivity checks, allowing 
        JSP source code disclosure, AMONG OTHER SECURITY PROBLEMS." 
        
          ~ emphasis mine

    ref:
    http://tomcat.apache.org/tomcat-8.0-doc/config/context.html
  
  
    ... and more...
    
    Mac OS is somewhat unusual in that it uses HFS+ in a case-insensitive 
    but case-preserving mode by default.  This causes some issues for 
    developers and power users, because most other environments are 
    case sensitive, but many Mac Installers fail on case sensitive 
    file systems.
    
    ref:
    http://en.wikipedia.org/wiki/Case_sensitivity
  

      ... and more...
    
    The allowLinking attribute controls if a context is allowed to use 
    linked files.  If enabled and the context is undeployed, the links 
    will be followed when deleting the context resources.  To avoid 
    this behaviour, use the aliases attribute.  Changing this setting 
    from the default of false on case insensitive operating systems 
    (this includes Windows) will disable a number of security measures 
    and allow, among other things, direct access to the WEB-INF directory.
  
    ref:
    http://tomcat.apache.org/tomcat-8.0-doc/security-howto.html    
    (the Context section of page)  
  
    
        ... and more...

  Using “allowLinking=true” in context.xml of tomcat 6, I am able to access
  soft links, within the application project directory, but the problem is 
  when I undeploy the application by deleting only project.war file, tomcat 
  is deleting all the contents inside the soft links along with that project 
  directory, but for me it’s causing the serious problem by deleting the 
  valuable web content.

  Let me know if you have any clue on this.      

  ref:    
  http://isocra.com/2008/01/following-symbolic-links-in-tomcat/
  


B.  Authorization

    There  are known limitations to having Ascegi - now only available 
    via sprig - being able to providing (a granular enough) Authorization 
    mechanism:   namely, a lack of the XML model to be flexibile enough 
    to be adaptive to the twin SaaS needs:  
      
       Verticalization & Specialization.
      
      

C.  Authentication

    Because of the lack of flexibility of XML files (see the Authorization
    section, above) neither a purely Ascegi approach, nor the Tomcat Realm 
    approach will suffice for most applications' Authentication.
      
    For example, you can immediately see that the Tomcat Realm approach 
    will not scale due to the need to express Users - which by the way is 
    normally/commonly termed a 'Principal' - in XML.
      
    Here is an example of the contents of the file - located in the file named
    ..\conf\tomcat-users.xml - that is used when the Tomcat Realm approach 
    is being leveraged:
      
    <tomcat-users>

       <role rolename="tomcat"/>
       <role rolename="role1"/>

       <user username="tomcat" password="tomcat" roles="tomcat"/>
       <user username="both" password="tomcat" roles="tomcat,role1"/>
       <user username="role1" password="tomcat" roles="role1"/>

     </tomcat-users>
          
          
    There is no accommodation here (nor in sprig's Ascegi) for dynamic 
    modification(s) to either a Principal/User or their Role(s) because 
    of its reliance on an XML-based modeling scheme.
  
  

D.  Sundry Catalina Notes

Tomcat Contexts

First, a brief backgrounder on Catalina Basics.  Some concise facts:

      -  A  Context represents your web application.

      -  There can be multiple Services per Server.
      -  There can be multiple Connectors per Service.
      -  There can be only one Engine per Service.
      -  There can be multiple Hosts per Engine.
      -  There can be multiple Contexts per Host.
 
 ref:   See more at the Tomcat Connectors section.
 
 
 In the discussion below, this short-hand pattern is referenced:
 
    $CATALINA_HOME/conf/[enginename]/[hostname]/
           
 ... where the actual location (typically) equates to...
        
    $CATALINA_HOME/conf/Catalina/localhost/
 

The behavior described below will occur only if/when a context file does not exist
for the application in the $CATALINA_HOME/conf/[enginename]/[hostname]/ folder and
if there is no individual context.xml file at ../META-INF/context.xml inside the 
application files.  

The behavior is as follows.

If the web application is packaged as a WAR then /META-INF/context.xml will be copied
to $CATALINA_HOME/conf/[enginename]/[hostname]/ and renamed to match the application's
context path. Once this file exists, it will not be replaced if a new WAR with a newer
../META-INF/context.xml is placed in the host's appBase.


ref:
  $CATALINA_HOME/webapps/docs/web-socket-howto.html#Tomcat_WebSocket_specific_configuration
  
  ... which is an extract from:

     http://tomcat.apache.org/tomcat-8.0-doc/web-socket-howto.html



Tomcat Connectors

Each Connector element represents a port that Tomcat will listen to for
requests.  It is important to remember that an OS will only allow one
Connector per port, so every Connector requires its own unique port.


The Connector element only has one job - listening for requests, passing
them on to an Engine, and returning the results to its specified port.

By arranging these Connector elements within hierarchies of Services and
Engines, you can create a logical infrastructure for data to flow in and
out of the website.

Additionally, Connectors can be used to link Tomcat to other supporting
web technologies, like the Apache web server.

Information about what Server the specified port is located on, what Service
the Connector is a part of, and what Engine the Connection should be routed
to is conveyed to the Connector by its relative position in Tomcat's nested
element hierarchy.

In this example:

  <Server>
    <Service>
      <Connector port="8443"/>
      <Connector port="8444"/>
      <Engine>
        <Host name="yourhostname">
          <Context path="/webapp1"/>
          <Context path="/webapp2"/>
        </Host>
      </Engine>
    </Service>
  </Server>


... there are two Connectors listening for connections on individual ports.


Because both Connector elements are nested inside a single Service element,
and [verify that] a Service can have only one Engine, that Engine routes
the requests two both contained web applications, and passes the results
back to the Connectors.

This means that each request will potentially generate two responses:  
one from each web application.

To make each Connector pass requests from its port to only one specific
web application, change the configuration so that each request is routed
to one web application and the Connector sends a single response per request.

We rearrange the element hierarchy to designate two Services, each having
a single Connector:

  <Server>
    <Service name="Catalina">
      <Connector port="8443"/>
      <Engine>
        <Host name="yourhostname">
          <Context path="/webapp1"/>
        </Host>
      </Engine>
    </Service>

    <Service name="Catalina8444">
      <Connector port="8444"/>
      <Engine>
        <Host name="yourhostname">
          <Context path="/webapp2"/>
        </Host>
      </Engine>
    </Service>
  </Server>


Now there are two different Services, each with its own Connector that
is passing connections from two different ports on the same Server to
two different Engines for processing (by two different Hosts delegating
to two separate web applications).

This results in each request being routed to one web application, and
the Connector can only send a single response per request.



Types of Connectors

There are two basic Connector types available in Tomcat - HTTP and AJP.


A.  HTTP Connectors

Tomcat's base functionality as a servlet container, is augmented by the HTTP Connector
element, which allows it to also function as a stand-alone web server.

The HTTP Connector element, which supports the HTTP/1.1 protocol, represents a single
Connector component listening to a specific TCP port on a given Server for connections;
it also supports proxy forwarding and redirects.

Two of the most important attributes of this Connector are the "protocol" and "SSLEnabled"
attributes.



The "protocol" Attribute

Defines the protocol the Connector will use to communicate, is set by default to HTTP/1.1,
but can be modified to allow access to more specialized protocols.

For example, if you wanted to expose the connectors low level socket properties for fine tuning,
you could use the "protocol" attribute to enable the NIO protocol.


The "SSLEnabled" Attribute
Setting the value of this to "true" causes the connector to use SSL handshake/encryption/decryption.


HTTP Connectors can also be used as part of a load balancing scheme, in conjunction with an
HTTP load balancer that supports session stickiness, such as mod_proxy.  However, as AJP tends
to handle proxying better than HTTP, this usage is not common.



B.  AJP Connectors

AJP Connectors work in the same way as HTTP Connectors, but they use the AJP protocol in place of HTTP.

Apache JServ Protocol, or AJP, is an optimized binary version of HTTP that is typically used to allow
Tomcat to communicate with an Apache web server.  AJP Connectors are most commonly implemented in
Tomcat through the plug-in technology mod_jk, a re-write of the defunct mod_jserv plug-in with extensive
optimization, support for more protocols through the jk library, and Tomcat-specific functionality.

The mod_jk binaries and extensive documentation are available on the Tomcat Connector project website.

This functionality is typically required in a high-traffic production situation, where Tomcat clusters
are being run behind an Apache web server.

This allows the Apache server to deliver static content and proxy requests in order to balance request
loads effectively across the network and let the Tomcat servers focus on delivering dynamic content.


______________________________________

Footnotes

*   Configurating Catalina's Host
     
      ref:  https://tomcat.apache.org/tomcat-8.0-doc/config/host.html
      ref:  http://www.manydesigns.com/en/portofino/portofino3/3_1_x/installation-guide/deploying-on-tomcat/#TOC-Manually-deploying-and-configuring


        ... ... ...



