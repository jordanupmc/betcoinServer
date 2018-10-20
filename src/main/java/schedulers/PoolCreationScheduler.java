package schedulers;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

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
            bd.PoolTools.messagePool("mick", "1" , "LOG :"+ System.currentTimeMillis());
        }
    }
}

