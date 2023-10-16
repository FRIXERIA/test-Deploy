package sit.int221.sastt3.services;

import java.util.ArrayList;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//import sit.int221.sastt3.entities.CustomUserDetails;
//import sit.int221.sastt3.repositories.UserRepo;
//
//@Service
//public class JwtUserDetailsService implements UserDetailsService {
////    @Autowired
////    private AuthenticationManager authenticationManager;
//    @Autowired
//    private UserRepo repo;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        sit.int221.sastt3.entities.User user = repo.findByUsername(username);
//        System.out.println(username);
//        System.out.println(user);
//        if(user==null){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, " Username is not correct");
//        }
//        if (user.getUsername().equals(username)) {
//            return new CustomUserDetails(user);
//        }
//        else {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//    }
////    public void authenticate(String username, String password) throws Exception {
////        try {
////            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
////        } catch (DisabledException e) {
////            throw new Exception("USER_DISABLED", e);
////        } catch (BadCredentialsException e) {
////            throw new Exception("INVALID_CREDENTIALS", e);
////        }
////    }
//}


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sit.int221.sastt3.repositories.UserRepo;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo repo;
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        sit.int221.sastt3.entities.User userData = repo.findByUsername(username);

        if (userData == null) {
            throw new UsernameNotFoundException("User Not found with username:" + username);
        }
        else {
            return new User(userData.getUsername(), userData.getPassword(), new ArrayList<>());
        }
    }


}
