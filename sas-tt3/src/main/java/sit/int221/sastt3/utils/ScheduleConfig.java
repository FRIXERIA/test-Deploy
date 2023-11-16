package sit.int221.sastt3.utils;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.Subscribe;
import sit.int221.sastt3.repositories.AnnouncementRepo;
import sit.int221.sastt3.repositories.SubscribeRepo;
import sit.int221.sastt3.services.AnnouncementService;
import sit.int221.sastt3.services.SendEmailService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleConfig {
    @Autowired
private AnnouncementRepo announcementRepo;

    @Autowired
    private SendEmailService sendEmailService;


@Scheduled(cron = "0 */1 * * * *")
public void CheckSendEmail( )
{
    // Try block to check for exceptions
    ZonedDateTime now =ZonedDateTime.now();
    String  newNow = now.toString().substring(0,16);
   List<Announcement>  announcement = announcementRepo.findAll();

    for (int i = 0 ; i< announcement.size();i++){

        if(announcement.get(i).getPublishDate()!=null) {
            String newPublish = announcement.get(i).getPublishDate().toString().substring(0,16);
            if (newNow.equals(newPublish)) {
                System.out.println("sendEmail");
                sendEmailService.sendnewAnnounceMail(announcement.get(i));
            }
        }

    }
}






}

