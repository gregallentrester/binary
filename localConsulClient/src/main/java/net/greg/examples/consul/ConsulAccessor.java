package net.greg.examples.consul;

import com.ecwid.consul.v1.*;
import com.ecwid.consul.v1.agent.model.*;
import com.ecwid.consul.v1.kv.model.*;


/**
 * Programmatic Consul Access
 */
public final class ConsulAccessor {

  /**
   * The ßuilder Pattern
   */
  public static void main(String[] args) {

    System.err.println(
      "ConsulClient - The value of the 12-Factor App KEE:\n" +
      System.getenv(args[0]));


    KEE = System.getenv(args[0]);

    new ConsulAccessor().
      listCatalogDatacenters().
      encodeBinaryKVPairs().
      publishKVPairs().
      retrieveKVPairs(OPAQUE_KEY1).
      retrieveKVPairs(OPAQUE_KEY2).
      retrieveKVPairs(OPAQUE_KEY3).
      retrieveKVPairs(OPAQUE_KEY4).
      retrieveBinaryKVPair().
      listKVPairs(NET_LOSS_SUBKEY).
      listKVPairs(NET_GAIN_SUBKEY);
  }

  /**
   *
   */
  private ConsulAccessor encodeBinaryKVPairs() {

    byte[] binaryAlphabits =
      new byte[] { 94,77,69,73,124,81,100,120,125,126 };

    consulClient.setKVBinaryValue(KEE, binaryAlphabits);

    return this;
  }

  /**
   *
   */
  private ConsulAccessor publishKVPairs() {

    consulClient.setKVValue(OPAQUE_KEY1, DESIGN);
    consulClient.setKVValue(OPAQUE_KEY2, STRATEGY);

    consulClient.setKVValue(OPAQUE_KEY3, DOMAIN);
    consulClient.setKVValue(OPAQUE_KEY4, FRITTER);

    return this;
  }

  /**
   *
   */
  private ConsulAccessor retrieveKVPairs(String key) {

    Response<GetValue> response =
      consulClient.getKVValue(key);

    System.err.println("\n\n" +
      key.substring(key.lastIndexOf(".")+1).toUpperCase() + "\n  key " +
      response.getValue().getKey() + "\nvalue " +
      response.getValue().getDecodedValue() +
      END);

    return this;
  }

  /**
   * Iteratively list of KVs for a key prefix.
   */
  private ConsulAccessor listKVPairs(String prefix) {

    Response<java.util.List<GetValue>> response =
      consulClient.getKVValues(prefix);

    System.err.println("\n\n" + prefix);

    response.getValue().
      forEach(value ->
        System.err.println(
          value.getKey() + ": " +
          value.getDecodedValue()));

    System.err.println(END);

    return this;
  }

  /**
   *
   */
  private ConsulAccessor retrieveBinaryKVPair() {

    Response<GetValue> response =
      consulClient.getKVValue(KEE);

    System.err.println(
      "\n\nretrieveBinaryKVPair()" +
      "\nKEE " + "\n" +
      response.getValue().getKey() +
      "\n\nDecoded Value \n" +
      response.getValue().getDecodedValue() +
      END);

    return this;
  }

  /**
   *
   */
  private ConsulAccessor listCatalogDatacenters() {

    Response<java.util.List<String>> response =
      consulClient.getCatalogDatacenters();

    System.out.println(
      "\n\nDatacenters: " +
      response.getValue() +
      END);

    return this;
  }


  private static String KEE;

  /** Class-Level Attr **/
  private static ConsulClient consulClient;

  static {
    consulClient = new ConsulClient("127.0.0.1");
  }

  private static final String OPAQUE_KEY1 = "net.loss.app.foo";
  private static final String OPAQUE_KEY2 = "net.loss.app.fighters";
  private static final String OPAQUE_KEY3 = "net.gain.apple.cidr";
  private static final String OPAQUE_KEY4 = "net.gain.apple.scruffs";

  private static final String DESIGN = "DESIGN";
  private static final String STRATEGY = "STRATEGY";
  private static final String DOMAIN = "DOMAIN";
  private static final String FRITTER = "FRITTER";

  private static final String NET_GAIN_SUBKEY = "net.gain";
  private static final String NET_LOSS_SUBKEY = "net.loss";

  private static final String END = "\n  ...  ...  ...";
}

  /*
  // TODO


  // import com.ecwid.consul.v1.health.*;
  // import com.ecwid.consul.v1.health.model.*;

  private ConsulAccessor any() {

    // register new service
    NewService newService = new NewService();

    newService.setId("myapp_01");
    newService.setName("myapp");
    newService.setTags(Arrays.asList("EU-West", "EU-East"));
    newService.setPort(8080);

    client.agentServiceRegister(newService);

    // register new service with associated health check
    NewService newService2 = new NewService();

    newService2.setId("myapp_02");
    newService2.setTags(Collections.singletonList("EU-East"));
    newService2.setName("myapp");
    newService2.setPort(8080);
  }
*/
