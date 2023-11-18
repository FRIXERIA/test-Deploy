package sit.int221.sastt3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sit.int221.sastt3.DTO.JwtRequest;
import sit.int221.sastt3.jwt.JwtResponse;
import sit.int221.sastt3.jwt.JwtTokenUtil;
import sit.int221.sastt3.services.JwtUserDetailsService;

//
//@RestController
//@RequestMapping("/api/token")
//@CrossOrigin
//public class JwtAuthenticationController {
//
//
//}