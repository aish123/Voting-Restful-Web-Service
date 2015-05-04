package VotingAPI;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.*;

import java.util.Properties;

//import org.apache.log4j.*;
//import org.apache.log4j.ConsoleAppender;

public class KafkaProducer{

    //
    // public static final Logger log = Logger.getLogger(KafkaProducer.class);
    private static Producer<Integer, String> producer;
    private final Properties properties = new Properties();


     KafkaProducer() {

         //Logger.getRootLogger().setLevel(Level.OFF);

         /*Logger rootLogger = Logger.getRootLogger();
         rootLogger.setLevel(Level.DEBUG);
         PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");
         rootLogger.addAppender(new ConsoleAppender(layout));*/


        properties.put("metadata.broker.list", "54.68.83.161:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        producer = new Producer(new ProducerConfig(properties));
    }

    public void sendMessage(String pollResult)
    {
        String topic = "cmpe273-new-topic";

        KeyedMessage<Integer,String> data = new KeyedMessage<Integer, String>(topic,pollResult);
        System.out.println(pollResult);
        producer.send(data);
        producer.close();
    }
}