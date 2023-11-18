package sit.int221.sastt3.controller;

import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.DTO.DetailDTO;
import sit.int221.sastt3.DTO.MultipleSub;
import sit.int221.sastt3.DTO.SubscribeDTO;
import sit.int221.sastt3.entities.Email;
import sit.int221.sastt3.entities.SaveOTP;
import sit.int221.sastt3.entities.Subscribe;
import sit.int221.sastt3.jwt.JwtTokenUtil;
import sit.int221.sastt3.repositories.SubscribeRepo;
import sit.int221.sastt3.services.SendEmailService;
import sit.int221.sastt3.services.SubscribeService;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SubscribeController {
    @Autowired
    private SubscribeService subscribeService;
@Autowired
private JwtTokenUtil jwtTokenUtil;
@Autowired
private SendEmailService sendEmailService;

@Autowired
    SubscribeRepo subscribeRepo;
    @Autowired
    private ModelMapper modelMapper;

    // Sending a simple Email
    @PostMapping("/sendOTP")
    public String sendOTP(@RequestBody Email details)
    {
        String generateOTP =subscribeService.generateOTP();
        String otp =  jwtTokenUtil.generateOTPToken(details,generateOTP);
        String status =  sendEmailService.sendOTPMail(details.getEmail(),generateOTP);
        System.out.println(status);
        if(status.equals("200")){
            return otp;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Send OTP");
        }

    }

    // เช็ค otp
    @PostMapping("/verify")
    public ResponseEntity<?> subData(@RequestBody SaveOTP verify ,@RequestHeader("Verify") String token )
    {
        return subscribeService.verifyEmail(verify,token);

    }

    // ส่งemail sub แล้ว
    @PostMapping("/sendMail")
    public List<Subscribe> sendMail(@RequestBody MultipleSub details)
    {
        List<Subscribe> status = subscribeService.addNewSub(details);
            for (Subscribe subscribe : status){
                subscribeRepo.refresh(subscribe);
            }
//        subscribeRepo.refresh(status);
//        SubscribeDTO subDTO = modelMapper.map(status, SubscribeDTO.class);
       return status;
    }

// เอาข้อมูลสำหรับคำunsub
    @PostMapping("/all")
    public List<Subscribe> subData(@RequestBody Email email)
    {
        List<Subscribe> data = subscribeService.getSubscribeByEmail(email.getEmail());
        return data;
    }





//    @PostMapping("/OTP")
//    public String OTP(@RequestBody Subscribe details)
//    {
//        String generateOTP =emailService.generateOTP();
//        String otp =  jwtTokenUtil.generateOTPToken(details,generateOTP);
//        String status =  sendEmailService.sendOTPMail(details.getEmail(),generateOTP);
//return otp;
//
//    }

//@PutMapping("/update")
//    public
    // Sending email with attachment
//    @PostMapping("/sendMailWithAttachment")
//    public String sendMailWithAttachment(
//            @RequestBody SendEmail details)
//    {
//        String status
//                = emailService.sendMailWithAttachment(details);
//
//        return status;
//    }


    @DeleteMapping("/unsub")
    public List<Subscribe> Unsub(@RequestBody MultipleSub subscribe)
    {
      return subscribeService.Unsub(subscribe);
    }


    @PostMapping("allEmail")
    public List<Subscribe> All (@RequestBody Email email){
        return subscribeService.getSubscribeByEmail(email.getEmail());
    }

    @GetMapping("allCategory/{id}")
    public List<Subscribe> All (@PathVariable Integer id){
        return subscribeService.getSubscribeByCateId(id);
    }


    @GetMapping("allCategory")
    public List<Subscribe> AllData (){
        return subscribeRepo.findAll();
    }

}
