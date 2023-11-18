//package sit.int221.sastt3.utils;
//
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class QuartzConfig {
//
//    @Bean
//    public JobDetail emailJobDetail() {
//        return JobBuilder.newJob(EmailJob.class)
//                .withIdentity("emailJob")
//                .storeDurably()
//                .build();
//    }
//
//    @Bean
//    public Trigger emailJobTrigger() {
//        return TriggerBuilder.newTrigger()
//                .forJob(emailJobDetail())
//                .withIdentity("emailTrigger")
//                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(12, 0)) // Set your desired schedule here
//                .build();
//    }
//}
