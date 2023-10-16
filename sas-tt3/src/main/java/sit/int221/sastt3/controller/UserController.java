package sit.int221.sastt3.controller;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.sastt3.DTO.JwtRequest;
import sit.int221.sastt3.DTO.MatchPass;
import sit.int221.sastt3.DTO.UserDTO;
import sit.int221.sastt3.DTO.UserUpdateDTO;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.User;
import sit.int221.sastt3.exception.ItemNotFoundException;
import sit.int221.sastt3.jwt.JwtRefresh;
import sit.int221.sastt3.jwt.JwtResponse;
import sit.int221.sastt3.jwt.JwtTokenUtil;
import sit.int221.sastt3.repositories.AnnouncementRepo;
import sit.int221.sastt3.repositories.UserRepo;
import sit.int221.sastt3.services.AnnouncementService;
import sit.int221.sastt3.services.JwtUserDetailsService;
import sit.int221.sastt3.services.UserService;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//@CrossOrigin(origins= {"http://intproj22.sit.kmutt.ac.th/tt3","http://localhost:5173/"})
@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {
    @Autowired
    public UserService service;
    @Autowired
    public UserRepo repo;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AnnouncementRepo announcementRepo;

    @Autowired
    private AnnouncementService announcementService;
    @GetMapping("/users")
    public List<UserDTO> getAllUser(@RequestHeader ("Authorization") String token) {
        String jwtToken = token.substring(7);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        if (tokenRole.equals("admin")) {
            List<User> user = service.getAllUser();
            List<UserDTO> userDTO = user.stream().map(c -> modelMapper.map(c, UserDTO.class)).collect(Collectors.toList());
            return userDTO;
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
    }
    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Integer id,@RequestHeader ("Authorization") String token){
        String jwtToken = token.substring(7);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        if (tokenRole.equals("admin")) {
            User user = service.getUserById(id);
            UserDTO userDTO = modelMapper.map(user,UserDTO.class);
            return userDTO;
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

    }
    @PostMapping("/users")
    public UserDTO createUser(@RequestBody User newUser,@RequestHeader ("Authorization") String token) {
        String jwtToken = token.substring(7);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        if (tokenRole.equals("admin")) {
            User user =service.createUser(newUser);
            repo.refresh(user);
            UserDTO userDTO = modelMapper.map(user,UserDTO.class);
            return userDTO;
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
    }

    @PostMapping("/users/match")
    public ResponseStatusException matchUser(@RequestBody MatchPass user,@RequestHeader ("Authorization") String token) {
        String jwtToken = token.substring(7);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        if (tokenRole.equals("admin")) {
            return service.matchPassword(user);
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

    }


    @DeleteMapping("/users/{id}")
    public void removeUser(@PathVariable Integer id,@RequestHeader ("Authorization") String token){
        String jwtToken = token.substring(7);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        String tokenName = jwtTokenUtil.getAllClaimNameFromToken(jwtToken);
        User name =repo.findByUsername(tokenName);
        if (tokenRole.equals("admin")) {
            User deletedUser = service.getUserById(id);
            if (deletedUser != null) {
//                if(tokenName.equals(deletedUser.getUsername())){
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot delete your own account.");
//                }
                Integer idUser = repo.findByUsername(tokenName).getId();
                List<Announcement> adminUser = announcementRepo.findByAnnouncementOwner(id);
//                System.out.println(adminUser);
//                    if (adminUser.size()==0){
//                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//                    }
                for(Announcement announcement:adminUser){
                    announcement.setAnnouncementOwner(idUser);
                    announcement.setAnnouncementOwnerUser(name);
                    announcementRepo.saveAndFlush(announcement);
                }

                service.deleteUser(id);

            }

            else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

    }
    @PutMapping("/users/{id}")
    public UserUpdateDTO updateUser(@PathVariable Integer id,@RequestBody User newUser,@RequestHeader ("Authorization") String token){
        String jwtToken = token.substring(7);
        String tokenRole = jwtTokenUtil.getAllClaimsRoleFromToken(jwtToken);
        if (tokenRole.equals("admin")) {
            User update =service.updateUser(id,newUser);
            repo.refresh(update);
            UserUpdateDTO updateDTO = modelMapper.map(update, UserUpdateDTO.class);
            return updateDTO;
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

    }

    @GetMapping("/token")
    public ResponseEntity<JwtRefresh> getAuthToken(@RequestHeader("Authorization") String refresh) {
        if (refresh != null && refresh.startsWith("Bearer ")) {
            String jwtToken = refresh.substring(7);
            try {
                Boolean isRefreshTokenExpired = jwtTokenUtil.isTokenExpired(jwtToken);
                if (isRefreshTokenExpired) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
                } else {
                    String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    UserDetails userDetails = userDetailsService
                            .loadUserByUsername(username);
                    final String token = jwtTokenUtil.generateToken(userDetails);
                    JwtRefresh response = new JwtRefresh(token);
                    return ResponseEntity.ok(response);
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Authorization header");
        }
}

@PostMapping("/token")
public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
    User username= repo.findByUsername(authenticationRequest.getUsername());
    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);

    if(username==null){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found");
    }
    boolean passwordMatches = argon2.verify(username.getPassword(),authenticationRequest.getPassword().toCharArray());
    if(!passwordMatches){
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, " Password is not correct");
    }
    else
    authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

     UserDetails userDetails = userDetailsService
            .loadUserByUsername(authenticationRequest.getUsername());

    if(passwordMatches) {
        final String token = jwtTokenUtil.generateToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        JwtResponse response = new JwtResponse(token,refreshToken);
        return ResponseEntity.ok(response);
    }
    else {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, " Username is not correct");
    }
}
@PostMapping
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

}

@GetMapping("/check")
    public ResponseEntity<?> checkValid(HttpServletRequest request){
        String requestAuthen = request.getHeader("Authorization");
    String username = null;
    String jwtToken = null;
    // JWT Token is in the form "Bearer token". Remove Bearer word and get
    // only the Token

    if (requestAuthen != null && requestAuthen.startsWith("Bearer ")) {
        jwtToken = requestAuthen.substring(7);
        try {
            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to get JWT");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token has expired");
        }

    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization is fail!!");
    }
    return ResponseEntity.ok().body("Token is Valid");
}

}
