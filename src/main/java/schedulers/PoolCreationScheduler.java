package schedulers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

public class PoolCreationScheduler {

    final static ConnectionFactory factory = new ConnectionFactory();

    public static void main(String[] args) throws Exception {
        factory.setUri(System.getenv("CLOUDAMQP"));
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobDetail = newJob(HelloJob.class).build();

        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(repeatSecondlyForever(6))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static class HelloJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            try {
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                String queueName = "work-queue-1";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("x-ha-policy", "all");
                channel.queueDeclare(queueName, true, false, false, params);

                String msg = "Sent at:" + System.currentTimeMillis();
                byte[] body = msg.getBytes(StandardCharsets.UTF_8);
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, body);
                System.out.println("Message Sent: " + msg);
                connection.close();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }
    }
}

