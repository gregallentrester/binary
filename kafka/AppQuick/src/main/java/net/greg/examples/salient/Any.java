package net.greg.examples.salient;

import java.util.*;

import java.util.concurrent.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.CommandLineRunner;


/**
 * Leverage the <code>KStream<code> <i>DSL</i>, to run a Java app using Kafka
 * implementing a WordCount algorithm that computes a word occurrence histogram.
 *
 * <b>NB</b>
 * To re-run this app, you must stop Kafka and follow these instructions:
 *
 *   https://bit.ly/2zE4yYE
 *
 * Additionally, running this code resets offset to earliest so that we
 * use the same pre-loaded data:
 *
 * <blockquote>
 *   props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
 *   return props;
 * </blockquote>
 *
 * <b>NB</b>
 * Before running this app, prepare input data to a Kafka Topic, which
 * is subsequently be processed by a Kafka Streams application.
 *
 * Create the input Topic and the output Topic via:
 *   {@code bin/kafka-topics.sh --create ...}
 *
 * Write data to the input Topic via:
 *   {@code bin/kafka-console-producer.sh}
 *
 * Else, you won't see any data arriving in the output Topic.
 *
 * The input stream reads from a topic named "streams-plaintext-input",
 * where the values of messages represent lines of text.
 *
 * The histogram output is written to a topic named "streams-wordcount-output",
 * where each record is an updated count of a single word.
 *
 * The Serializers/deserializers (serdes ???) support String and Long types.
 *
 * This app operates on an infinite, unbounded data stream.
 *
 * Because it must assume potentially unbounded data - it cannot
 * know when it has processed everything - the app outputs its
 * current state (word count) while continuing to process data.
 */
@SpringBootApplication
public final class Any implements CommandLineRunner {
  public void run(String...im_tech_debt) { }

  public static final String INPUT_TOPIC = "streams-plaintext-input";
  public static final String OUTPUT_TOPIC = "streams-wordcount-output";

  private final Serde<String> stringSerde = Serdes.String();
  private final Serde<Long> longSerde = Serdes.Long();


  public static void main(String [] any) {
    Any.go(true); // T/F
  }

  private static void go(boolean shouldReset) {

    final Properties props =
      getStreamsConfig(shouldReset);

    final StreamsBuilder builder =
      new StreamsBuilder();

    createWordCountStream(builder);

    final KafkaStreams streams =
      new KafkaStreams(builder.build(), props);

    final CountDownLatch latch =
      new CountDownLatch(1);

    Runtime.getRuntime().addShutdownHook(
      new Thread("streams-wordcount-shutdown-hook") {

        @Override
        public void run() {
          streams.close();
          latch.countDown();
        }
    });

    try {
      streams.start();
      latch.await();
    }
    catch (final Throwable e) { System.exit(1); }

    System.exit(0);
  }

  /**
   *
   */
  static Properties getStreamsConfig(boolean shouldReset) {

    final Properties props = new Properties();

    if ( ! shouldReset) {
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
      props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
      props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    }
    else {
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    return props;
  }

  /**
   *
   */
  static void createWordCountStream(final StreamsBuilder builder) {

    final KStream<String, String> source = builder.stream(INPUT_TOPIC);

    final KTable<String, Long> counts =
      source.
        flatMapValues(value ->
          Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "))).
            groupBy((key, value) -> value).count();

    // need to override value serde to Long type
    counts.toStream().to(
      OUTPUT_TOPIC,
      Produced.with(Serdes.String(),
      Serdes.Long()));
  }

  /**
   * Additionally, running this code resets offset to earliest so that we
   * use the same pre-loaded data:
   *
   * <blockquote>
   *   props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
   *   return props;
   * </blockquote>
   */

  public static final String RESET = "\u001B[0m";
  public static final String GREEN = "\u001B[32m";
}
