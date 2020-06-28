package net.greg.examples.salient;

// more than 7 imports from same package s.b. *
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.time.*;
import java.time.format.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public final class NOAAService {

  /**
   * Produce 7-day forecast
   * @param args CLI formal argument (in accordance with Spring2)
   *             is a single, comma-separated value
   */
  public static void main(String[] args) {
    new NOAAService().accept(args);
  }

  /**
   * Coordinates (nested) calls to <code>forecast()</code>,
   * <code>locate()</code> member methods, rites results to a flat file
   *
   * @param args passed CLI formal argument (in accordance with Spring2)
   *             is a single, comma-separated value
   * @see #main(String[])
   */
  private void accept(String[] args) {

    COORS_SENTINEL = args[0];

    String forecast = forecast(locate(args));

    try (
      BufferedWriter publisher =
          new BufferedWriter(
            new FileWriter(HTML_REPRESENTATION))) { // NOAAForecast.HTML

      publisher.write(forecast);
    }
    catch(IOException e) { e.printStackTrace(); }
  }

  /**
   * Find the stringified JSON payload based upon the deciphered locale
   *
   * @param location the deciphered locale
   * @return the stringified JSON payload
   * @see #accept(String[])
   * @see #locate(String[])
   */
  public static String forecast(String location) {

    String TODAY = LocalDate.now().getDayOfWeek().name();

    System.err.println(
      report.append(
      "\n Today ").append(GRN).
      append(LocalDate.now().getDayOfWeek().name()).
      append(NC).
      append(localeDesignation).toString());

    if (TODAY.equalsIgnoreCase("MONDAY")) {
      LOWER_BOUNDS = 1;
      UPPER_BOUNDS = 10;
    }
    else if (TODAY.equalsIgnoreCase("TUESDAY")) {
      LOWER_BOUNDS = 2;
      UPPER_BOUNDS = 11;
    }
    else if (TODAY.equalsIgnoreCase("WEDNESDAY")) {
      LOWER_BOUNDS = 3;
      UPPER_BOUNDS = 12;
    }
    else if (TODAY.equalsIgnoreCase("THURSDAY")) {
      LOWER_BOUNDS = 4;
      UPPER_BOUNDS = 13;
    }
    else if (TODAY.equalsIgnoreCase("FRIDAY")) {
      LOWER_BOUNDS = 5;
      UPPER_BOUNDS = 14;
    }
    else if (TODAY.equalsIgnoreCase("SATURDAY")) {
      LOWER_BOUNDS = 6;
      UPPER_BOUNDS = 15;
    }
    else if (TODAY.equalsIgnoreCase("SUNDAY")) {
      LOWER_BOUNDS = 7;
      UPPER_BOUNDS = 16;
    }

    boolean accum = false;
    String line = "";

    StringBuilder forecasts =
      new StringBuilder(PREAMBLE);


    if (null == location) {

      System.err.println(
        RED + STOP + " " + COORS_SENTINEL + NC);

      return
        PREAMBLE + "<li><span style='color:red;'>" +
        STOP + "&nbsp;" + COORS_SENTINEL + "</span>";
    }

    try {

      Process process = Runtime.getRuntime().exec(new String[] {
        CURL, location });

      BufferedReader br =
        new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));

      int ndx = 0;

      while (null != (line = br.readLine())) {

        if (line.contains(PERIODS_TOKEN)) {
          accum = true;
        }

        if (line.contains(NUMBER_LBL)) {

          ndx =
            Integer.parseInt(
              line.substring(
                line.indexOf(":")+1,line.length()-1).trim());

          if (ndx > UPPER_BOUNDS) {
            return forecasts.toString();
          }
        }

        if (accum &&
            ! line.contains(NUMBER_LBL) &&
            ! line.contains(PERIODS_TOKEN) &&
            ndx >= LOWER_BOUNDS) {

          line = line.replace(']', ' ');
          line = line.replace('[', ' ');

          line = line.replace('}', ' ');
          line = line.replace('{', ' ');

          line = line.replace(')', ' ');
          line = line.replace('(', ' ');

          line = line.replace(',', ' ');
          line = line.replace('"', ' ');

          if (line.contains(NAME_TAG)) {
            line = line.substring(line.indexOf(":")+1);
            forecasts.append(HR + "\n<li><b><br/>" + line.trim() + "</b>");
          }
          else if (line.contains(ICON_LBL)) {

            line = line.substring(line.indexOf(":")+1);

            forecasts.append("\n<li><img src='" + line.trim());
            forecasts.append(STYLE);
          }
          else {
            forecasts.append("\n<li>" + line.trim());
          }
        }
      }
    }
    catch (IOException e) { e.printStackTrace(); }

    return forecasts.toString();
  }

  /**
   * Detect NOAA locale via this endpoint.
   *
   * @param args coordinates via CLI
   * @return the NOAA forecast endpoint
   * @see #accept(String[])
   */
  private String locate(String[] args) {

    String locale = STOP;

    ENDPOINT_POINTS.append(args[0]);

    try {

      Process process = Runtime.getRuntime().exec(new String[] {
        CURL, ENDPOINT_POINTS.toString() });

      BufferedReader br =
        new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));

      while (null != (locale = br.readLine())) {

        if (locale.contains(FORECAST_KEY)) {

          locale =
            locale.substring(
              locale.indexOf(HTTPS_TOKEN),
              locale.lastIndexOf("\""));

          localeDesignation =
            "\nLocale " + GRN + locale + NC;

          return locale;
        }
      }
    }
    catch (IOException e) { e.printStackTrace(); }

    return locale;
  }


  private static String localeDesignation;

  private static  StringBuilder report =
    new StringBuilder(100);

  private static String COORS_SENTINEL = "";

  private static int LOWER_BOUNDS;
  private static int UPPER_BOUNDS;

  private static final String STOP =
    "Invalid Coordinates";

  private static final String HTML_REPRESENTATION =
    "NOAAForecast.HTML";

  private static String CURL = "curl";

  private static StringBuilder ENDPOINT_POINTS =
    new StringBuilder("https://api.weather.gov/points/");

  private static final String FORECAST_KEY =  "\"forecast\":";
  private static final String HTTPS_TOKEN = "https:";

  private static final String PERIODS_TOKEN = "\"periods\": [";

  private static final String NUMBER_LBL = "number";
  private static final String NAME_TAG = "name";
  private static final String ICON_LBL = "icon";

  private static final String PREAMBLE =
    "<!DOCTYPE html><html><ul style='list-style:none;'>";

  private static final String STYLE =
    "' width='30' height='30' style='border-radius: 50%;'/>";

  private static final String HR =
    "<hr style='border:0; height:1px;width:95%;margin-top:1.8em;"+
    "background-image:-webkit-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);"+
    "background-image:-moz-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);"+
    "background-image:-ms-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);"+
    "background-image:-o-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);'/>";

  public static final String GRN = "\u001B[32m";
  public static final String RED = "\u001B[31m";
  public static final String NC = "\u001B[0m";
}
