//package sit.int221.sastt3.services;
//
//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import sit.int221.sastt3.entities.Announcement;
//import sit.int221.sastt3.utils.EmailJob;
//
//import java.time.Duration;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.Date;
//
//@Service
//public class ScheduleService {
//
//    @Autowired
//    private Scheduler scheduler;
//    public void processAnnouncement(Announcement announcement) {
//        ZoneId zoneId = ZoneId.of("Asia/Bangkok"); // Replace with your zone ID
//        ZonedDateTime openDateTime = announcement.getPublishDate(); // Assuming getOpenDateTime returns ZonedDateTime
//
//        if (openDateTime.isAfter(ZonedDateTime.now(zoneId))) {
//            Duration delay = Duration.between(ZonedDateTime.now(zoneId), openDateTime);
//            System.out.println("1"+ announcement.getId());
//            scheduleEmailJob(announcement.getId(), delay.toMillis());
//        }
//    }
//
//    public void scheduleEmailJob(Integer announcementId, long delayInMillis) {
//        try {
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//            scheduler.start();
//            System.out.println("2"+ announcementId);
//            JobDetail job = JobBuilder.newJob(EmailJob.class)
//                    .withIdentity("emailJob_" + announcementId, "emailGroup")
//                    .usingJobData("announcementId", announcementId)
//                    .build();
//
//            Trigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("emailTrigger_" + announcementId, "emailGroup")
//                    .startAt(DateBuilder.futureDate((int) delayInMillis, DateBuilder.IntervalUnit.MILLISECOND))
//                    .build();
//
//            scheduler.scheduleJob(job, trigger);
//        } catch (SchedulerException e) {
//            // Handle exception
//        }
//    }
////    public void scheduleEmailJob(long announcementId, ZonedDateTime publishDate) {
////        JobDataMap jobDataMap = new JobDataMap();
////        jobDataMap.put("announcementId", announcementId);
////
////        JobDetail jobDetail = JobBuilder.newJob(EmailJob.class)
////                .withIdentity("emailJob_" + announcementId)
////                .usingJobData(jobDataMap)
////                .storeDurably()
////                .build();
////
////        Trigger trigger = TriggerBuilder.newTrigger()
////                .forJob(jobDetail)
////                .withIdentity("emailTrigger_" + announcementId)
////                .startAt(Date.from(publishDate.toInstant()))  // Set the start time to the publish date
////                .build();
////
////        try {
////            scheduler.scheduleJob(jobDetail, trigger);
////        } catch (SchedulerException e) {
////            // Handle scheduling exception
////            e.printStackTrace();
////        }
////    }
//}