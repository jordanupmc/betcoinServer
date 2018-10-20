package schedulers;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import services.BetPoolService;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

public class PoolCreationScheduler {

    public static void main(String[] args) throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobDetail = newJob(HelloJob.class).build();

        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(repeatSecondlyForever(10))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static class HelloJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("EXECUTE BEGIN  !");
            System.out.println(
            BetPoolService.messagePool("mick", "1" , "1935024627", "LOG :"+ System.currentTimeMillis())   );
            System.out.println("EXECUTE END!");

        }
    }
}

