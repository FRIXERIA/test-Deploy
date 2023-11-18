//package sit.int221.sastt3.utils;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import sit.int221.sastt3.entities.Announcement;
//import sit.int221.sastt3.services.AnnouncementService;
//import sit.int221.sastt3.services.SendEmailService;
//
//public class EmailJob implements Job, ApplicationContextAware {
//
//    private ApplicationContext applicationContext;
//
//public AnnouncementService announcementService = new AnnouncementService();
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        // Get the EmailService bean from the application context
//        SendEmailService emailService = applicationContext.getBean(SendEmailService.class);
//
//
//        // Get announcement ID from the job data map
//        Integer announcementId = context.getMergedJobDataMap().getInt("announcementId");
//
//        // Retrieve announcement by ID and send email
//        Announcement announcement =announcementService.getAnnouncementById(announcementId,false);
//                emailService.sendnewAnnounceMail(announcement);
//                // Retrieve announcement by ID from the database
//
//    }
//}