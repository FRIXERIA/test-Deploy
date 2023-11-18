package sit.int221.sastt3.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.Subscribe;
import sit.int221.sastt3.repositories.SubscribeRepo;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendEmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SubscribeRepo subscribeRepo;

    @Value("${spring.mail.username}") private String sender;
    public HttpStatus sendSubMail(String sendTo)
    {
//        sendOTPMail(sendTo,otp);
        String subject = "Thank You For Subscribe";
        String sendBody = "Thank You For You Interesting. We will send new announcement for you in this email";
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(sendTo);
            mailMessage.setText(sendBody);
            mailMessage.setSubject(subject);

            // Sending the mail
            javaMailSender.send(mailMessage);
            return HttpStatus.OK;
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    public String sendOTPMail(String sendTo,String otp )
    {
        String subject = "Your OTP";
        String sendBody = otp;
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(sendTo);
            mailMessage.setText(sendBody);
            mailMessage.setSubject(subject);

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "200";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "400";
        }
    }
    public String sendnewAnnounceMail(Announcement newAnnounce)
    {
        String title = newAnnounce.getAnnouncementTitle();
        String desc = newAnnounce.getAnnouncementDescription();
        String header ="New Announcement";
//        String link = "https://intproj22.sit.kmutt.ac.th/tt3/announcement/" + newAnnounce.getId();
        String link = "https://intproj22.sit.kmutt.ac.th/tt3/announcement/"+ newAnnounce.getId();
        String linkUnsub = "https://intproj22.sit.kmutt.ac.th/tt3/verify";
//        String sendBody = "from (sender)" +  + "<br>Description: " + desc + "<br>If you want to see more details, <a href=\"" + link + "\">click here</a>" + "<br>If you want to unsubscribe this category, <a href=\"" + linkUnsub + "\">click here</a>" ;
        List<String> email = new ArrayList<>();;

        // Try block to check for exceptions

//        System.out.println("222");
//        System.out.println(newNow + "Z[UTC]");
//        System.out.println(newAnnounce.getPublishDate());
//        System.out.println(newNow + "Z[UTC]".equals(newAnnounce.getPublishDate()));
//        if(now.equals(newAnnounce.getPublishDate())){
//            System.out.println("aaa");
//            System.out.println(newAnnounce.getPublishDate());
//            System.out.println(now);
//        }
        try {
            // Creating a simple mail message
//            SimpleMailMessage mailMessage
//                    = new SimpleMailMessage();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mailMessage = new MimeMessageHelper(message, true);

            List<Subscribe> allSub = subscribeRepo.findAll();

            for (int i = 0 ; i< allSub.size();i++){
                if(newAnnounce.getCategoryId().equals(1) && newAnnounce.getAnnouncementDisplay().equals("Y")){

                    List<Subscribe> allCate = subscribeRepo.findByCategory(1);
                    for(Subscribe subscribe : allCate) {
                        String sendBody = "from (sender)" +" "+ sender + "<br>to (subscriber)" +" "+ subscribe.getSubscriberEmail() +"<br> title" +" "+ "[Your mail subscription @ SAS]" +" "+ title + "<br> body" +" "+ desc

                               + "Announcement link :" +link + "<br> To unsubscribe:" +"<br>If you no longer wish for"+" "+ subscribe.getSubscriberEmail() +" "+ "to receive any email announcement messges from SAS, please click the following <a href=\"" + linkUnsub + "\">click here</a>"  ;
                        mailMessage.setFrom(sender);
                        mailMessage.setTo(subscribe.getSubscriberEmail());
                        mailMessage.setText(sendBody, true);
                        mailMessage.setSubject(header);

                        // Sending the mail
                        javaMailSender.send(message);
//                     email.add(allSub.get(i).getEmail());
//                     for(int l =0 ; l<email.size();l++){
                    }
//                     }
                }

                if(newAnnounce.getCategoryId().equals(2) && newAnnounce.getAnnouncementDisplay().equals("Y")){
                    List<Subscribe> allCate = subscribeRepo.findByCategory(2);
                    for(Subscribe subscribe : allCate) {
                        String sendBody = "from (sender)" +" "+ sender + "<br>to (subscriber)" +" "+ subscribe.getSubscriberEmail() +"<br> title" +" "+ "[Your mail subscription @ SAS]" +" "+ title + "<br> body" +" "+ desc

                                + "Announcement link :" +link + "<br> To unsubscribe:" +"<br>If you no longer wish for"+" "+ subscribe.getSubscriberEmail() +" "+ "to receive any email announcement messges from SAS, please click the following <a href=\"" + linkUnsub + "\">click here</a>"  ;
                        mailMessage.setFrom(sender);
                        mailMessage.setTo(subscribe.getSubscriberEmail());
                        mailMessage.setText(sendBody, true);
                        mailMessage.setSubject(header);

                        // Sending the mail
                        javaMailSender.send(message);
//                     email.add(allSub.get(i).getEmail());
//                     for(int l =0 ; l<email.size();l++){
                    }
//                     }
                }

                if(newAnnounce.getCategoryId().equals(3) && newAnnounce.getAnnouncementDisplay().equals("Y")){
                    List<Subscribe> allCate = subscribeRepo.findByCategory(3);
                    for(Subscribe subscribe : allCate) {
                        String sendBody = "from (sender)" +" "+ sender + "<br>to (subscriber)" +" "+ subscribe.getSubscriberEmail() +"<br> title" +" "+ "[Your mail subscription @ SAS]" +" "+ title + "<br> body" +" "+ desc

                                + "Announcement link :" +link + "<br> To unsubscribe:" +"<br>If you no longer wish for"+" "+ subscribe.getSubscriberEmail() +" "+ "to receive any email announcement messges from SAS, please click the following <a href=\"" + linkUnsub + "\">click here</a>"  ;
                        mailMessage.setFrom(sender);
                        mailMessage.setTo(subscribe.getSubscriberEmail());
                        mailMessage.setText(sendBody, true);
                        mailMessage.setSubject(header);

                        // Sending the mail
                        javaMailSender.send(message);
//                     email.add(allSub.get(i).getEmail());
//                     for(int l =0 ; l<email.size();l++){
                    }
//                     }
                }
                if(newAnnounce.getCategoryId().equals(4) && newAnnounce.getAnnouncementDisplay().equals("Y")){
                    List<Subscribe> allCate = subscribeRepo.findByCategory(4);
                    for(Subscribe subscribe : allCate) {
                        String sendBody = "from (sender)" +" "+ sender + "<br>to (subscriber)" +" "+ subscribe.getSubscriberEmail() +"<br> title" +" "+ "[Your mail subscription @ SAS]" +" "+ title + "<br> body" +" "+ desc

                                + "Announcement link :" +link + "<br> To unsubscribe:" +"<br>If you no longer wish for"+" "+ subscribe.getSubscriberEmail() +" "+ "to receive any email announcement messges from SAS, please click the following <a href=\"" + linkUnsub + "\">click here</a>"  ;
                        mailMessage.setFrom(sender);
                        mailMessage.setTo(subscribe.getSubscriberEmail());
                        mailMessage.setText(sendBody, true);
                        mailMessage.setSubject(header);

                        // Sending the mail
                        javaMailSender.send(message);
//                     email.add(allSub.get(i).getEmail());
//                     for(int l =0 ; l<email.size();l++){
                    }
//                     }
                }
//
            }
            // Setting up necessary details
//            mailMessage.setFrom(sender);
//            mailMessage.setTo("porcha.por@mail.kmutt.ac.th");
//            mailMessage.setText(sendBody);
//            mailMessage.setSubject(subject);
//
//            // Sending the mail
//            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

//    public String sendupdateAnnounceMail(Announcement newAnnounce)
//    {
//
//        String title = newAnnounce.getAnnouncementTitle();
//        String desc = newAnnounce.getAnnouncementDescription();
//        String subject = "Update Annoucements";
//        String link = "https://intproj22.sit.kmutt.ac.th/tt3/announcement";
//        String linkUnsub = "https://intproj22.sit.kmutt.ac.th/tt3/announcement";
//        String sendBody = "Title: " + title + "<br>Description: " + desc + "<br>If you want to see more details, <a href=\"" + link + "\">click here</a>" + "<br>If you want to unsubscribe this category, <a href=\"" + linkUnsub + "\">click here</a>" ;
//        List<String> email = new ArrayList<>();;
//
//        // Try block to check for exceptions
//        ZonedDateTime now =ZonedDateTime.now();
//        String  newNow = now.toString().substring(0,16);
////        System.out.println("222");
////        System.out.println(newNow + "Z[UTC]");
////        System.out.println(newAnnounce.getPublishDate());
////        System.out.println(newNow + "Z[UTC]".equals(newAnnounce.getPublishDate()));
////        if(now.equals(newAnnounce.getPublishDate())){
////            System.out.println("aaa");
////            System.out.println(newAnnounce.getPublishDate());
////            System.out.println(now);
////        }
//        try {
//            // Creating a simple mail message
////            SimpleMailMessage mailMessage
////                    = new SimpleMailMessage();
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper mailMessage = new MimeMessageHelper(message, true);
//
//            List<Subscribe> allSub = subscribeRepo.findAll();
//
//            for (int i = 0 ; i< allSub.size();i++){
//                System.out.println(allSub.get(i).getGeneral());
//                if(newAnnounce.getCategoryId().equals(1) && allSub.get(i).getGeneral().equals(true) && newAnnounce.getAnnouncementDisplay().equals("Y")){
//                    mailMessage.setFrom(sender);
//                    mailMessage.setTo(allSub.get(i).getEmail());
//                    mailMessage.setText(sendBody,true);
//                    mailMessage.setSubject(subject);
//
//                    // Sending the mail
//                    javaMailSender.send(message);
////                     email.add(allSub.get(i).getEmail());
////                     for(int l =0 ; l<email.size();l++){
////
////                     }
//                }
//
//                if(newAnnounce.getCategoryId().equals(2)&& allSub.get(i).getScholarship().equals(true)&& newAnnounce.getAnnouncementDisplay().equals("Y")){
////                    email.add(allSub.get(i).getEmail());
//                    mailMessage.setFrom(sender);
//                    mailMessage.setTo(allSub.get(i).getEmail());
//                    mailMessage.setText(sendBody,true);
//                    mailMessage.setSubject(subject);
//
//                    // Sending the mail
//                    javaMailSender.send(message);
//
//                }
//
//                if(newAnnounce.getCategoryId().equals(3)&&allSub.get(i).getFindJobs().equals(true)&& newAnnounce.getAnnouncementDisplay().equals("Y")){
////                    email.add(allSub.get(i).getEmail());
//                    mailMessage.setFrom(sender);
//                    mailMessage.setTo(allSub.get(i).getEmail());
//                    mailMessage.setText(sendBody,true);
//                    mailMessage.setSubject(subject);
//
//                    // Sending the mail
//                    javaMailSender.send(message);
//
//                }
//                if(newAnnounce.getCategoryId().equals(4)&& allSub.get(i).getInternship().equals(true)&& newAnnounce.getAnnouncementDisplay().equals("Y")){
////                    email.add(allSub.get(i).getEmail());
//                    mailMessage.setFrom(sender);
//                    mailMessage.setTo(allSub.get(i).getEmail());
//                    mailMessage.setText(sendBody,true);
//                    mailMessage.setSubject(subject);
//
//                    // Sending the mail
//                    javaMailSender.send(message);
//
//                }
////
//            }
//            // Setting up necessary details
////            mailMessage.setFrom(sender);
////            mailMessage.setTo("porcha.por@mail.kmutt.ac.th");
////            mailMessage.setText(sendBody);
////            mailMessage.setSubject(subject);
////
////            // Sending the mail
////            javaMailSender.send(mailMessage);
//            return "Mail Sent Successfully...";
//        }
//
//        // Catch block to handle the exceptions
//        catch (Exception e) {
//            return "Error while Sending Mail";
//        }
//    }
    // Method 2
    // To send an email with attachment
//    public String
//    sendMailWithAttachment(SendEmail details)
//    {
//        // Creating a mime message
//        MimeMessage mimeMessage
//                = javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper;
//
//        try {
//
//            // Setting multipart as true for attachments to
//            // be send
//            mimeMessageHelper
//                    = new MimeMessageHelper(mimeMessage, true);
//            mimeMessageHelper.setFrom(sender);
//            mimeMessageHelper.setTo(details.getRecipient());
//            mimeMessageHelper.setText(details.getMsgBody());
//            mimeMessageHelper.setSubject(
//                    details.getSubject());
//
//            // Adding the attachment
//            FileSystemResource file
//                    = new FileSystemResource(
//                    new File(details.getAttachment()));
//
//            mimeMessageHelper.addAttachment(
//                    file.getFilename(), file);
//
//            // Sending the mail
//            javaMailSender.send(mimeMessage);
//            return "Mail sent Successfully";
//        }
//
//        // Catch block to handle MessagingException
//        catch (MessagingException e) {
//
//            // Display message when exception occurred
//            return "Error while sending mail!!!";
//        }
//    }
}
