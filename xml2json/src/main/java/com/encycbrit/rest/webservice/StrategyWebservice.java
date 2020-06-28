package com.encycbrit.rest.webservice;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import  com.encycbrit.rest.model.DAO;
import com.encycbrit.rest.model.DAOStrategyException;
import com.encycbrit.rest.model.BritannicaTopicsTemplate;


/**
 *
 */
@RestController
@RequestMapping("/eb")
public class StrategyWebservice  {

    /**
     * Find XML for TopicID
     * http://localhost:8088/encycbrit/rest/eb/topic/4144
     *
     *
     * @param resource
     * @return
     */
    @RequestMapping(value = "/topic/{resource}", method = RequestMethod.GET)
    public @ResponseBody String  useCaseOneTopicIDs(@PathVariable String resource) {

        long start = System.nanoTime();

        String response = DAO.query("/topic/{resource} | " + resource);

        long elapsed = System.nanoTime() - start;

        // AsyncLog.info(
        System.err.println(
            ELAPSED_PROLOGUE + (elapsed/MILLI_CONVERSION_FACTOR) +
            LABEL_MILLIS + (elapsed/MICRO_CONVERSION_FACTOR) + LABEL_MICRO);

        return
            response +
            LABEL_EXECUTION_PATH +
            EXECUTION_PATH +
            LABEL_EXECUTION_PATH_CLOSE;
    }

    /**
     * Find Topic by Classname
     * Valid Classnames:
     *    animal, art, biography, event, place,
     *    plant, science, sports, technology, topic
     *
     * http://localhost:8088/encycbrit/rest/eb/class/class-name/
     *
     * @param resource
     * @return
     */
    @RequestMapping(value = "/class/class-name/{resource}", method = RequestMethod.GET)
    public @ResponseBody String useCaseTwoTopicByClassname(@PathVariable String resource) {

        long start = System.nanoTime();

        String response = DAO.query( "/class/class-name/{resource} | " + resource);

        long elapsed = System.nanoTime() - start;

        // AsyncLog.info(
        System.err.println(
            ELAPSED_PROLOGUE + (elapsed/MILLI_CONVERSION_FACTOR) +
            LABEL_MILLIS + (elapsed/MICRO_CONVERSION_FACTOR) + LABEL_MICRO);

        return
            response +
            LABEL_EXECUTION_PATH +
            EXECUTION_PATH +
            LABEL_EXECUTION_PATH_CLOSE;
    }

    /**
     * Entire XML file.
     *
     * @param resource
     * @return
     */
    @RequestMapping(value = "/all/{resource}", method = RequestMethod.GET)
    public @ResponseBody String useCaseThreeEntireFile(@PathVariable String resource) {

        long start = System.nanoTime();

        String response = DAO.query("/all/{resource} | " + resource);

        long elapsed = System.nanoTime() - start;

        // AsyncLog.info(
        System.err.println(
            ELAPSED_PROLOGUE + (elapsed/MILLI_CONVERSION_FACTOR) +
            LABEL_MILLIS + (elapsed/MICRO_CONVERSION_FACTOR) + LABEL_MICRO);

        return
            response +
            LABEL_EXECUTION_PATH +
            EXECUTION_PATH +
            LABEL_EXECUTION_PATH_CLOSE;
    }

    /**
      * Discovers the actual location for sundry binary (and other) artifacts
      * by leveraging the capabilities of the ClassLoader for the Java Runtime
      * en effect; this location is subject to the usual ClassLoader ancestry
      * rules, which nonetheless are different for "application servers" (such
      * as Catalina); this method resolves the class path in a portable manner
      * (i.e., it works on stand-alone applications and on application servers
      * alike); for portability, the resolved value for the <code>Path</code>
      * is a computed value; for efficiency/safety, this computation done
      * exactly once (for this class).
      * <ul>
      *    <li>
      *      <code>getProtectionDomain()</code> - <br><br>
      *
      *        If a security manager is present, this method first calls<br>
      *        the security manager's <code>checkPermission()</code> method<br>
      *    </li>
      *    <li>
      *      <code>getCodeSource()</code> - <br><br>
      *
      *        Extends the concept of a code base; encapsulates any certificate
      *        chains used to verify signed code originating from a URL; this
      *        call can result in a <code>null</code> value<br>
      *    </li>
      *    <lt>
      *      <code>getLocation()</code> - <br><br>
      *
      *        The location associated with this <code>java.security.CodeSource</code><br>
      *    </li>
      * </ul>
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
                StrategyWebservice.class.
                    getProtectionDomain().
                        getCodeSource();

            if (null == codeSource) {
              return null;
            }

            edge +=
                codeSource.getLocation().getPath().substring(1);

          edge =
              edge.substring(0, edge.indexOf(CLASSES_EDGE) +
              CLASSES_EDGE.length());
        }
        catch (SecurityException e) {
            throw new DAOStrategyException(e);
        }

        return Paths.get(edge);
    }

    private static final Path EXECUTION_PATH = discoverContext();

    private static final long MILLI_CONVERSION_FACTOR = 1_000_000;
    private static final long MICRO_CONVERSION_FACTOR = 1_000;
    private static final String ELAPSED_PROLOGUE = "\n   StrategyWebservice \n     Elapsed: \n\n       ";
    private static final String LABEL_MILLIS = " millisecond(s). \n       ";
    private static final String LABEL_MICRO = " microsecond(s). \n\t  ...\n";
    private static final String LABEL_RESOURCE = "<br>              Resource: ";
    private static final String LABEL_VERB = "<br>                  Verb: ";
    private static final String LABEL_EXECUTION_PATH =
        "<br><br><span style='background-color:rgb(255,255,153); " +
        "font-variant: small-caps;'>EXECUTION_PATH: ";
    private static final String LABEL_EXECUTION_PATH_CLOSE = "</span";
    private static final String FEEDBACK = "<br>       FEEDBACK: ";
    private static final String GET = " GET ";
    private static final String FINIS = "<br><br>&nbsp;&nbsp; ... <br><br>";
    private static final String CACHE = BritannicaTopicsTemplate.CACHE;
}
