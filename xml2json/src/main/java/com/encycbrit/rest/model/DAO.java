package com.encycbrit.rest.model;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;

import  javax.xml.parsers.*;
import javax.xml.transform.*;

import org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import org.w3c.dom.*;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;


/**
 * Facade for Data Acquitition/Data Persistence tha publishes XML, JSON; offers
 * a conceptual SQL Query that parallels the supplied XML file's model/structure.
 * Accommodates User-Agents:  cURL, a browser, Google Postman, etc
 */
public enum DAO {

    ANY;

    private static final String FILE_ID = "/britannica_topics.xml";


    /**
     * Facade for Querying Datastores:  XML, SQL, NoSQL, JSON
     *
     * @param resource  the pipe-delimited <code>String</code>
     *                              which conists of a method--discriminator
     *                              coupled to the search 'key'
     * @return hybrid payload consisting of both XML and JSON
     */
    public static final String query(String resource) {

        String key = resource.split("\\|")[1].trim();

        if (resource.contains("/all/{resource}")) {
            String SQL =
                "select * from publish-list";

            return handleMirror(FILE_ID, SQL);
        }
        else if (resource.contains("/topic/{resource}")) {
            String SQL =
                "select topicid, urltitle, urlclass from publish-list where  topicid = '?'";

            return handleTopicID(key, SQL);
        }
        else if (resource.contains("/class/class-name/{resource}")) {
            String SQL =
                "select topicid, urltitle, urlclass from publish-list where  urlclass = '?' ";

            return handleTopicByClassname(key, SQL);
        }

        return constructFailMessage(key);
    }

    /**
     * Create an <code>InputStream</code> from a <code>String<code> that represents the
     * XML file;  Copy the <code>InputStream</code> to a conventional <code>File</code>;
     * Read each line of the file into a <code>StringBuilder</code>, along the way (inline)
     * encode each '<' & '>' symbol into the HTML Entities [ '&lt; and  '&gt;' ] respectively --
     * this allows each of them to be evaluated by a browser, which resullts in their actual
     * representation being depicted/rendered; Similarily, insert a newline character as  a
     * plaeholder inline (at the end of each line, as each line is read), later when all lines
     * in the file have been aggregated, the newline character is replaced with a '<br>' tag --
     * this allows each of the '<br>' tags to be evaluated by a browser, resulting in their actual
     * purpose/function, namely, a breaking space
     * <p>
     * Along the way, I had to be cognizant of differences in Servlet Container's (Catalina)
     * ClassLoader hierarchy with respect to files, etc - specifically I used a custom method
     * to discover the actual location of sundry binary (and other) artifacts; this is done  by
     * leveraging the capabilities of the ClassLoader for the Servlet Container's Java Runtime --
     * along with two of the classes in the canonical <code>java.security</code>  package -
     * <code>ProtectionDomain.java</code> and <code>CodeSource.java</code>
     *
     * @param fileName the name of the XML file being modeled
     * @param SQL the posited SQL statement corresponding to the XML file's structure
     * @return the <code>String</code> representation of the compound XML/JSON payload
     * @see #discoverContext()
     */
    private static final String handleMirror(String  fileName, String SQL) {

        InputStream streamXML =
            DAO.class.getResourceAsStream(fileName);

       String line = "";

        StringBuilder content = new StringBuilder();
        StringBuilder contentPlain = new StringBuilder();
        BufferedReader  reader =null;

        try {

            File tempFile =
                new File(
                    discoverContext() + "/FILE.TMP");

            Files.copy(
                streamXML,
                tempFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

            IOUtils.closeQuietly(streamXML);

            reader =
                new BufferedReader(
                    new FileReader(tempFile));

	          while (null != (line = reader.readLine())) {
                contentPlain.append(line);
                content.append(line + "\n");
  		      }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally {
            try {  reader.close();  }
              catch(IOException e) {
                  e.printStackTrace();
            }
        }

        String finalResult =
            content.toString().replace("<", "&lt;").
                replace("<", "&lt;").replace("\n", "<br>");

        JSONObject resultAtRestJSON =
            XML.toJSONObject(contentPlain.toString());

        return
            finalResult +
            RULE +
            resultAtRestJSON +
            RULE + SQL;
    }

    /**
     * Perform TopicID Lookup.
     * http://localhost:8088/encycbrit/rest/eb/topic/4144
     * /topic/{resource} | 4144
     *
     * @param key the lookup key
     * @param SQL the posited SQL statement corresponding to the XML file's structure
     * @return the <code>String</code> representation of the compound XML/JSON payload
     */
    private static String handleTopicID(String key, String SQL) {

        boolean matchFound = false;

        StringBuilder result =
            new StringBuilder(SYNTHETIC_RESPONSE_DIRECTIVE + "\n");

        try {

            NodeList nodeList =
                DOCUMENT.getElementsByTagName("*");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    if (node.getTextContent().trim().equalsIgnoreCase(key)) {

                        matchFound = true;

                        Node parentNode = node.getParentNode();
                        String parentEntityOpen = "<" + parentNode.getNodeName() + ">";
                        String parentEntityClose = "</" + parentNode.getNodeName() + ">";

                        result.append(parentEntityOpen + "\n");

                        NodeList nodeList2 = node.getParentNode().getChildNodes();

                        for (int ndx = 0; ndx <  nodeList2.getLength(); ndx++) {

                            if ( ! "#text".equals(nodeList2.item(ndx).getNodeName())) {

                                String entityOpen = "<" + nodeList2.item(ndx).getNodeName() + ">";
                                String entityClose = "</" + nodeList2.item(ndx).getNodeName() + ">";
                                result.append(entityOpen);
                                result.append( nodeList2.item(ndx).getTextContent().trim());
                                result.append(entityClose + "\n");
                            }
                        }

                        result.append(parentEntityClose + "\n");
                    }
                }
            }
        }
        catch (DOMException e) {
            e.printStackTrace();
            return constructFailMessage(key);
        }

        if ( ! matchFound) {
            return constructFailMessage(key);
        }

        String finalResult =
            result.toString().replace("<", "&lt;").
                replace("<", "&lt;").replace("\n", "<br>");

        JSONObject resultAtRestJSON =
            XML.toJSONObject(result.toString());

        return finalResult.toString() + RULE +  resultAtRestJSON + RULE + SQL + RULE;
    }

    /**
      * Handler for Path:  eb/class/class-name
      * Corresponds to W/S:  useCaseTwoTopicByClassname()
      *
      * Valid class-names are:
      *   <ul>
      *     <li> animal         </li>
      *     <li> art               </li>
      *     <li> biography    </li>
      *     <li> event           </li>
      *     <li> place            </li>
      *     <li> plant            </li>
      *     <li> science        </li>
      *     <li> sports           </li>
      *     <li> technology   </li>
      *     <li> topic            </li>
      *   </ul>
      *
      * @param key the lookup key
      * @param SQL the posited SQL statement corresponding to the XML file's structure
      * @return the <code>String</code> representation of the compound XML/JSON payload
      */
    private static String handleTopicByClassname(String key, String SQL) {

        boolean matchFound = false;

        StringBuilder result =
            new StringBuilder(SYNTHETIC_RESPONSE_DIRECTIVE + "\n");

        try {

            NodeList nodeList =
                DOCUMENT.getElementsByTagName("*");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    if (node.getTextContent().trim().equalsIgnoreCase(key)) {

                        matchFound = true;

                        Node parentNode = node.getParentNode();
                        String parentEntityOpen = "<" + parentNode.getNodeName() + ">";
                        String parentEntityClose = "</" + parentNode.getNodeName() + ">";

                        result.append(parentEntityOpen + "\n");

                        NodeList nodeList2 = node.getParentNode().getChildNodes();

                        for (int ndx = 0; ndx <  nodeList2.getLength(); ndx++) {

                            if ( ! "#text".equals(nodeList2.item(ndx).getNodeName())) {

                                String entityOpen = "<" + nodeList2.item(ndx).getNodeName() + ">";
                                String entityClose = "</" + nodeList2.item(ndx).getNodeName() + ">";
                                result.append(entityOpen);
                                result.append( nodeList2.item(ndx).getTextContent().trim());
                                result.append(entityClose + "\n");
                            }
                        }

                        result.append(parentEntityClose + "\n");
                    }
                }
            }
        }
        catch (DOMException e) {
            e.printStackTrace();
            return constructFailMessage(key);
        }

        if ( ! matchFound) {
            return constructFailMessage(key);
        }

        String finalResult =
            result.toString().replace("<", "&lt;").
                replace("<", "&lt;").replace("\n", "<br>");

        JSONObject resultAtRestJSON =
            XML.toJSONObject(result.toString());

        return finalResult.toString() + RULE +  resultAtRestJSON + RULE + SQL + RULE;
    }

    /**
      * Convert XML to JSON.
      *
      * @param fileName the name of the XML file being modeled  as JSON
      */
    private static void modelAtRestJSON(String fileID) {

        try {

            // class-level attribute
            modelAtRestJSON =
                XML.toJSONObject(IOUtils.toString(
                    DAO.class.getResourceAsStream(fileID)));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A means to quickly dump the contents of an XML file to console.
     */
    public static final void stringifyXML() {

        try {

            // class-level attribute
            stringifiedXML =
                IOUtils.toString(
                    DAO.class.getResourceAsStream(FILE_ID));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ßuild XML to be published by a RESTful Web Service ???
     * ßetter to convert everything to JSON
     *
     * @param value the lookup key
     * @return the <code>String</code> representation of the compound XML/JSON payload
     */
    private static String constructFailMessage(String value) {

        final String SPACE = "&nbsp;&nbsp;&nbsp;";

        return
            new StringBuilder(SYNTHETIC_RESPONSE_DIRECTIVE).
            append("<br>&lt;url-publish&gt;").
            append("<br>" + SPACE + "&lt;topicid&gt;").
            append(value).
            append("&lt;/topicid&gt;").
            append("<br>" + SPACE + "&lt;error&gt; URL Not Found &lt;/error&gt;").
            append("<br>" + SPACE + "&lt;cause&gt;topic ").
            append(value).
            append("&nbsp; not in database &lt;/cause&gt;").
            append("<br>&lt;/url-publish&gt;").
        toString();
    }

    /**
      * Discovers the actual location for sundry binary (file, and other) artifacts by leveraging
      * the capabilities of the ClassLoader for the Java Runtime en effect; this location is subject
      * to the usual ClassLoader ancestry rules, which nonetheless are different for "app servers"
      * (such as Catalina); this method resolves the class path in a portable manner (i.e., it works)
      * on stand-alone applications and on application servers alike); for portability, the resolved
      * value for the <code>Path</code> is a computed value; for efficiency/safety, this
      * computation done exactly once (for this class).
      *   <ul>
      *       <li>
      *           <code>getProtectionDomain()</code> - <br><br>
      *               If a security manager is present, this method first calls<br>
      *               the security manager's <code>checkPermission()</code> method<br>
      *       </li>
      *       <li>
      *           <code>getCodeSource()</code> - <br><br>
      *               Extends the concept of a code base; encapsulates any certificate
      *               chains used to verify signed code originating from a URL; this
      *               call can result in a <code>null</code> value<br>
      *       </li>
      *       <lt>
      *           <code>getLocation()</code> - <br><br>
      *           The location associated with this <code>java.security.CodeSource</code><br>
      *       </li>
      *   </ul>
      *
      * @return   the <code>Path</code> which equates to the
      *                 class path of the installed/running code base
      */
    public static Path discoverContext() {

        /*
         * The canonical location for artifacts which have to be discovered
         * by the Java Runtime - this is the the resolved class path (which
         * this method discovers in a portable manner).
         */
        final String CLASSES_EDGE = "classes";

        String edge = "";

        if ( ! System.getProperty("os.name").contains("indow")) {
            edge = System.getProperty("file.separator");
        }

        try {

            java.security.CodeSource codeSource =
                DAO.class.getProtectionDomain().getCodeSource();

            if (null == codeSource) {  return null;  }

            edge += codeSource.getLocation().getPath().substring(1);

            edge =
                edge.substring(0, edge.indexOf(CLASSES_EDGE) +
                CLASSES_EDGE.length());
        }
        catch (SecurityException e) {
            throw new DAOStrategyException(e);
        }

        return Paths.get(edge);
    }

    private static final List <String> validClasses = new ArrayList<>();

    private static final String RULE =
        "<HR><br>";

    private static final String SYNTHETIC_RESPONSE_DIRECTIVE =
        "&lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;";

    /** JSON Representation  - supply to MongoDB module */
    private static JSONObject modelAtRestJSON;
    public static final JSONObject getModelAtRestJSON() { return modelAtRestJSON; }

    /** Defect resolution */
    private static String stringifiedXML;
    public static String getStringifiedXML() { return  stringifiedXML; }

    /** Opaque  XML Document */
    private static Document DOCUMENT;

    static {
        evaluateXMLDoc(FILE_ID);
        modelAtRestJSON(FILE_ID);
        stringifyXML();
    }

    /**
     * A precursor to all DOM machinations.
     * @param fileID
     * @return   the <code>Path</code> which equates to the
     *                 class path of the installed/running code base
     */
    private static void evaluateXMLDoc(String fileID) {

        try {

            DOCUMENT =
                DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().
                        parse(new File(discoverContext() + fileID));
        }
        catch (IOException |
                  SAXException |
                  ParserConfigurationException e) {
            new DAOStrategyException(e).printStackTrace();
        }
    }

    static {
        validClasses.add("animal");
        validClasses.add("art");
        validClasses.add("biography");
        validClasses.add("event");
        validClasses.add("place");
        validClasses.add("plant");
        validClasses.add("science");
        validClasses.add("sports");
        validClasses.add("technology");
        validClasses.add("topic");
    }
}
