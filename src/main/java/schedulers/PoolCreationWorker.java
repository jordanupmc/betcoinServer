package schedulers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
public class PoolCreationWorker {

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(System.getenv("CLOUDAMQP"));
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = "work-queue-1";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x-ha-policy", "all");
        channel.queueDeclare(queueName, true, false, false, params);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, false, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery != null) {
                String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Message Received: " + msg);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }

    }
}
