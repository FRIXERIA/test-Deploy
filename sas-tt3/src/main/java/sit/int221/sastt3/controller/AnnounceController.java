package sit.int221.sastt3.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.DTO.*;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.User;
import sit.int221.sastt3.jwt.JwtTokenUtil;
import sit.int221.sastt3.repositories.AnnouncementRepo;
import sit.int221.sastt3.repositories.UserRepo;
import sit.int221.sastt3.services.AnnouncementService;
import sit.int221.sastt3.services.CategoryService;
import sit.int221.sastt3.utils.ListMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//@CrossOrigin(origins= {"http://intproj22.sit.kmutt.ac.th/","http://localhost:5173/"})
@RestController
@RequestMapping("/api")
@CrossOrigin
public class AnnounceController {
    @Autowired
    private AnnouncementService service;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AnnouncementRepo repo;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/announcements")

    public List<?> getAllAnnouncements(@RequestParam(defaultValue = "admin") String mode, @RequestHeader(name = "Authorization", required = false) String token) {
        List<Announcement> announcements = service.getAllAnnouncement();
        List<Announcement> active = service.getActiveAnnouncement();
        List<Announcement> close = service.getCloseAnnouncement();

        // Check if any of the announcement lists is null
        if (announcements == null || active == null || close == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }

        if (token == null) {
            switch (mode) {
                case "active":
                    return active.stream()
                            .map(a -> modelMapper.map(a, UserAllModeDTO.class))
                            .collect(Collectors.toList());
                case "close":
                    return close.stream()
                            .map(a -> modelMapper.map(a, UserAllModeDTO.class))
                            .collect(Collectors.toList());
                default:
                    return announcements.stream()
                            .map(a -> modelMapper.map(a, UserAllModeDTO.class))
                            .collect(Collectors.toList());
            }
        } else {
            // Token is not null
            String jwtToken = token.substring(7);
            String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
            String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
            Integer idUser = repo.findByUsername(tokenName).getId();

            if ("announcer".equals(tokenRole)) {
                List<Announcement> announcerAnnouncements = repo.findByAnnouncementOwner(idUser);
                return announcerAnnouncements.stream()
                        .map(a -> modelMapper.map(a, AnnouncerDTO.class))
                        .collect(Collectors.toList());
            } else {
                return announcements.stream()
                        .map(a -> modelMapper.map(a, UserAllModeDTO.class))
                        .collect(Collectors.toList());
            }
        }
    }

    @GetMapping("/announcements/{id}")
    public DetailDTO getDetail(@PathVariable Integer id, @RequestParam(defaultValue = "false") Boolean count, @RequestHeader(name = "Authorization", required = false) String token) {
        Announcement announcement = service.getAnnouncementById(id, count);
        System.out.println(count);
        if (announcement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        if(token==null){
            return modelMapper.map(announcement, DetailDTO.class);
        }
        if(count==false){
            return modelMapper.map(announcement, DetailDTO.class);
        }
        if(token.equals("null")){
            return modelMapper.map(announcement, DetailDTO.class);
        }
        else {
            String jwtToken = token.substring(7);
            String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
            String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
            Integer idUser = repo.findByUsername(tokenName).getId();
            // If the user is an admin or the announcer of the announcement, allow access
            if (!tokenName.equals(announcement.getAnnouncementOwner())) {
                if (tokenRole.equals("admin")) {
                    return modelMapper.map(announcement, DetailDTO.class);
                }
//                else if (tokenRole.equals("announcer")) {
//                    return modelMapper.map(announcement, DetailDTO.class);
//                }
                else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
                }
            }

            if (tokenName.equals(announcement.getAnnouncementOwner())) {
                return modelMapper.map(announcement, DetailDTO.class);

            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
            }
        }
    }

    @PostMapping("/announcements")
    public Announcement createAnnouncement(@RequestBody AnnouncementInsert newAnnouncement, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
        Integer idUser = repo.findByUsername(tokenName).getId();
        if (newAnnouncement.getAnnouncementOwner() == null) {
            newAnnouncement.setAnnouncementOwner(idUser);
        }
        return categoryService.addAnnouncement(newAnnouncement);
    }

    @DeleteMapping("/announcements/{id}")
    public void removeAnnouncement(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        Integer idUser = repo.findByUsername(tokenName).getId();

        Announcement announcement = service.getAnnouncementById(id, false);
        // Check if the announcement exists
        if (announcement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }

        String announcementOwnerString = announcement.getAnnouncementOwner();
        Integer announcementOwnerId = userRepo.findByUsername(announcementOwnerString).getId();

        // Check if the user is an admin or the announcement owner
        if (tokenRole.equals("admin") || tokenName.equals(announcementOwnerString)) {
            service.deleteAnnouncement(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
    }

    @PutMapping("/announcements/{id}")
    public Announcement updateAnnouncement(@PathVariable Integer id, @RequestBody Announcement announcement, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        Integer idUser = repo.findByUsername(tokenName).getId();
       String idUserString = repo.findById(id).get().getAnnouncementOwner();
       Integer idUserSet = userRepo.findByUsername(idUserString).getId();
        if (announcement.getAnnouncementOwner() == null) {
            announcement.setAnnouncementOwner(idUserSet);
        }

       if (!tokenName.equals(idUserString)) {
           if(tokenRole.equals("admin")){
               return service.updateAnnouncement(id, announcement);
           }
           else {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized1");
           }
//           else {
//               throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                       "Announcement user id must be the same as the announcer's user id");
//           }
        }
        if (tokenName.equals(idUserString)) {
            return service.updateAnnouncement(id, announcement);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized2");
        }
    }

    @GetMapping("/announcements/pages")
    public PageDTO<AnnouncementDTO> getWithPage(@RequestParam(defaultValue = "admin") String mode,
                                                @RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "5") Integer size,
                                                @RequestParam(required = false) Integer category) {

        Page<Announcement> pageList = service.getWithPagination(mode, page, size, category);
        return listMapper.toPageDTO(pageList, AnnouncementDTO.class, modelMapper);
    }

}