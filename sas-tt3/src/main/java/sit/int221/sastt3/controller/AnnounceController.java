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


    public List<?> getAllAnnouncements(@RequestParam(defaultValue = "admin") String mode, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        List<?> response;
        String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        Integer idUser = repo.findByUsername(tokenName).getId();
        List<Announcement> announcements = service.getAllAnnouncement();

        if (tokenRole.equals("announcer")) {
            List<Announcement> test = repo.findByAnnouncementOwner(idUser);
            List<AnnouncerDTO> testDto = test.stream()
                    .map(a -> modelMapper.map(a, AnnouncerDTO.class))
                    .collect(Collectors.toList());
            return response = testDto;

        }
        else {
            List<UserAllModeDTO> announcementDTOList = announcements.stream()
                    .map(a -> modelMapper.map(a, UserAllModeDTO.class))
                    .collect(Collectors.toList());
            return response = announcementDTOList;
        }

    }

    @GetMapping("/announcements/{id}")
    public DetailDTO getDetail(@PathVariable Integer id, @RequestParam(defaultValue = "false") Boolean count, @RequestHeader("Authorization") String token) {
        Announcement announcement = service.getAnnouncementById(id, count);
        if (announcement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        if(token.equals("null")){
            return modelMapper.map(announcement, DetailDTO.class);
        }else {
            String jwtToken = token.substring(7);
            String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
            String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
            Integer idUser = repo.findByUsername(tokenName).getId();
            // If the user is an admin or the announcer of the announcement, allow access
            if (!idUser.equals(announcement.getAnnouncementOwner())) {
                if (tokenRole.equals("admin")) {
                    return modelMapper.map(announcement, DetailDTO.class);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
                }
            }

            if (idUser.equals(announcement.getAnnouncementOwner())) {
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

        Integer announcementOwnerId = announcement.getAnnouncementOwner();

        // Check if the user is an admin or the announcement owner
        if (tokenRole.equals("admin") || idUser.equals(announcementOwnerId)) {
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
       Integer idUserSet = repo.findById(id).get().getAnnouncementOwner();
        if (announcement.getAnnouncementOwner() == null) {
            announcement.setAnnouncementOwner(idUserSet);
        }
       if (!idUser.equals(announcement.getAnnouncementOwner())) {
           if(tokenRole.equals("admin")){
               return service.updateAnnouncement(id, announcement);
           }
           else {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
           }
//           else {
//               throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                       "Announcement user id must be the same as the announcer's user id");
//           }
        }
        if (idUser.equals(announcement.getAnnouncementOwner())) {
            return service.updateAnnouncement(id, announcement);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
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