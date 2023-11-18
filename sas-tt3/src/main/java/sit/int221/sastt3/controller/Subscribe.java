//package sit.int221.sastt3.controller;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import sit.int221.sastt3.entities.Subscribe;
//import sit.int221.sastt3.jwt.JwtTokenUtil;
//import sit.int221.sastt3.repositories.AnnouncementRepo;
//import sit.int221.sastt3.repositories.SubscribeRepo;
//import sit.int221.sastt3.repositories.UserRepo;
//import sit.int221.sastt3.services.AnnouncementService;
//import sit.int221.sastt3.services.CategoryService;
//import sit.int221.sastt3.utils.ListMapper;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin
//public class SubscribeController {
//    @Autowired
//    public SubscribeService service;
//    @Autowired
//    public SubscribeRepo repo;
//    @Autowired
//    private AnnouncementService serviceAnnounce;
//    @Autowired
//    private CategoryService categoryService;
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private ListMapper listMapper;
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//    @Autowired
//    private AnnouncementRepo repoAnnounce;
//    @Autowired
//    private UserRepo userRepo;
//    @GetMapping("sub")
//    public List<Subscribe> getAllUser(@RequestHeader (name = "Authorization", required = false) String token) {
//        String jwtToken = token.substring(7);
//        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
////        if (tokenRole.equals("admin")) {
//            List<Subscribe> user = service.getAllSubscribe();
//            List<Subscribe> subDTO = user.stream().map(c -> modelMapper.map(c, Subscribe.class)).collect(Collectors.toList());
//            return subDTO;
////        }
////        else {
////            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
////        }
//    }
//}
