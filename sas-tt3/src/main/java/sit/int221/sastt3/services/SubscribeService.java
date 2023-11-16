package sit.int221.sastt3.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.DTO.CategoryDTO;
import sit.int221.sastt3.DTO.MultipleSub;
import sit.int221.sastt3.DTO.SubscribeDTO;
import sit.int221.sastt3.entities.*;
import sit.int221.sastt3.jwt.JwtTokenUtil;
import sit.int221.sastt3.repositories.CategoryRepo;
import sit.int221.sastt3.repositories.SubscribeRepo;
import sit.int221.sastt3.repositories.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SubscribeService {
    @Autowired
    private SubscribeRepo subscribeRepo;
    @Autowired
    private SendEmailService sendEmailService;
@Autowired
    private UserRepo userRepo;
@Autowired
private JwtTokenUtil jwtTokenUtil;
@Autowired
private CategoryRepo categoryRepo;
//    @Value("${spring.mail.username}") private String sender;
//public String sendDataNewSub(Subscribe emailSub){
//    Subscribe sendTo = emailSub;
//    String subject = "Thank You For Subscribe";
//    String sendBody = "Thank You For You Interesting. We will send new announcement for you in this email";
//   return sendSimpleMail(sendTo,subject,sendBody,emailSub);
//}
//public Subscribe getSubscribeById(Integer id) {
//    return subscribeRepo.findById(id).orElseThrow(()->
//            new ResponseStatusException(HttpStatus.NOT_FOUND, "User id "+ id + " does not exist"));
//}
    public List<Subscribe> getSubscribeByEmail(String subscribe){
    if(subscribeRepo.findByEmail(subscribe)==null){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You not Subscriber");
    }
    else{
    return subscribeRepo.findByEmail(subscribe);
    }
    }

    public List<Subscribe> getSubscribeByCateId(Integer id){
        if(subscribeRepo.findByCategory(id)==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You not Subscriber");
        }
        else{
            return subscribeRepo.findByCategory(id);
        }
    }

    public ResponseEntity<?> verifyEmail(SaveOTP verify,String token) {
    try {
        String otp = jwtTokenUtil.getAllClaimsOTPFromToken(token);
        System.out.println(otp);
        System.out.println(verify.getSendOTP());
        if (otp.equals(verify.getSendOTP())) {
            if(subscribeRepo.findByEmail(verify.getEmail())==null){
                return ResponseEntity.ok("You not Subscriber");
            }
            else{
            return ResponseEntity.ok("Verify is Sucess");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OTP is in valid");
        }
    }
    catch (ExpiredJwtException e){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OTP is Expired");
    }

    }
public List<Subscribe> addNewSub(MultipleSub subscriptionRequest) {
    List<SubscribeDTO> dtoList = subscriptionRequest.getSubscriptions();
    List<Subscribe> savedSubscriptions = new ArrayList<>();
    String subEmail = null;

    for (SubscribeDTO subscribeDTO : dtoList) {
        if (subscribeRepo.findByEmail(subscribeDTO.getSubscriberEmail()).isEmpty()) {
            System.out.println("n");
            if (subscribeDTO.getCategoryId().equals(0)) {
                System.out.println("dd");
                dtoList.remove(subscribeDTO);
            }
            else {
                System.out.println("b");
                Category category = categoryRepo.findById(subscribeDTO.getCategoryId()).orElse(null);
                if (category != null) {
                    Subscribe newSub = new Subscribe();
                    newSub.setCategory(category);
                    newSub.setSubscriberEmail(subscribeDTO.getSubscriberEmail());
                    subEmail = subscribeDTO.getSubscriberEmail();
                    // Set other fields if required

                    savedSubscriptions.add(subscribeRepo.save(newSub));
                }
            }
        }
        else{
            System.out.println("update");
            return UpdateNewSub(subscriptionRequest);
        }
    }

    // Send email after processing all subscriptions
    sendEmailService.sendSubMail(subEmail);
    return savedSubscriptions;


}
    public List<Subscribe> UpdateNewSub(MultipleSub newSub) {
        List<SubscribeDTO> dtoList = newSub.getSubscriptions();
        List<Subscribe> savedSubscriptions = new ArrayList<>();
       String updatedEmails = null;


        for (SubscribeDTO subscribeDTO : dtoList) {
            if (!subscribeDTO.getCategoryId().equals(0)) {
                String subscriberEmail = subscribeDTO.getSubscriberEmail();
                List<Subscribe> existingSubscriptions = subscribeRepo.findByEmail(subscriberEmail);

                boolean shouldSendEmail = false;

                for (Subscribe existingSubscription : existingSubscriptions) {
                    if (!existingSubscription.getCategory().getId().equals(subscribeDTO.getCategoryId())) {
                        Category category = categoryRepo.findById(subscribeDTO.getCategoryId()).orElse(null);
                        if (category != null) {
                            Subscribe updatedSubscription = new Subscribe();
                            updatedSubscription.setCategory(category);
                            updatedSubscription.setSubscriberEmail(subscriberEmail);
                            // Set other fields if required

                            savedSubscriptions.add(subscribeRepo.save(updatedSubscription));
                            shouldSendEmail = true;
                        }
                    }
                }

                if (shouldSendEmail) {
                  updatedEmails = subscriberEmail;
                }
            }
        }

        // Send email to updated subscribers

            sendEmailService.sendSubMail(updatedEmails);


        return savedSubscriptions;


}
    public String generateOTP() {
        int otpLength = 4;

        // Generate a random 4-digit OTP
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
//}
    }
    public List<Subscribe> Unsub (MultipleSub unsubscribeRequest) {

        List<SubscribeDTO> subscriptions = unsubscribeRequest.getSubscriptions();
        List<Subscribe> updatedSubscriptions = new ArrayList<>();

        for (SubscribeDTO subscription : subscriptions) {
            String email = subscription.getSubscriberEmail();
            Integer category = subscription.getCategoryId();

            // Fetch the subscription details by email
            List<Subscribe> existingSubscription = getSubscribeByEmail(email);

            for(Subscribe subscribe : existingSubscription) {
                if (subscribe.getCategory().getId().equals(category)) {
                    // Remove the category from the subscription
                  subscribeRepo.delete(subscribe);

                    // Save the updated subscription

                }
                else {
                    updatedSubscriptions.add(subscribeRepo.saveAndFlush(subscribe));
                }
            }
        }

        return updatedSubscriptions;

    }
}
