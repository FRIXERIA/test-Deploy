package sit.int221.sastt3.jwt;

//import java.io.IOException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//
//
//import io.jsonwebtoken.ExpiredJwtException;
//
//import sit.int221.sastt3.services.JwtUserDetailsService;
//
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//if(request.getRequestURL().toString().contains("/token")&&request.getMethod().equals("POST")){
//    logger.info("pass");
//
//} else if (request.getRequestURL().toString().contains("/announcements")) {
//    logger.info("pass");
//} else {
//    final String requestTokenHeader = request.getHeader("Authorization");
//
//    String username = null;
//    String jwtToken = null;
//    // JWT Token is in the form "Bearer token". Remove Bearer word and get
//    // only the Token
//    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
//        jwtToken = requestTokenHeader.substring(7);
//        try {
//            username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//        } catch (IllegalArgumentException e) {
//            System.out.println("Unable to get JWT Token");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Unable to get JWT Token");
//            return;
//        } catch (ExpiredJwtException e) {
//            System.out.println("JWT Token has expired");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("JWT Token has expired");
//            return;
//        }
//        catch (Exception ex){
//            System.out.println("Token is not valid");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Token is not valid");
//            return;
//        }
//    }
////    else {
////        logger.info("Token received: " + requestTokenHeader);
////        logger.warn("JWT Token does not begin with Bearer String");
////        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////        return;
////    }
//
//    // Once we get the token validate it.
//
//    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        System.out.println('a');
//        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
//        // if token is valid configure Spring Security to manually set
//        // authentication
//        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
//
//            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                    userDetails, null, userDetails.getAuthorities());
//            usernamePasswordAuthenticationToken
//                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            // After setting the Authentication in the context, we specify
//            // that the current user is authenticated. So it passes the
//            // Spring Security Configurations successfully.
//            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//        }
//        chain.doFilter(request, response);
//    }
//}
////        chain.doFilter(request, response);
//    }
//
//}

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.sastt3.services.JwtUserDetailsService;


import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURL = request.getRequestURL().toString();

        // Skip filtering for certain URLs
        if (requestURL.contains("/token") || requestURL.contains("/pages") || requestURL.contains("/category") || requestURL.contains("/announcements/{id}") && request.getMethod().equals("GET")) {
            logger.info("Skipping filtering for URL: " + requestURL);
            chain.doFilter(request, response);
            return;
        }
        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println(requestTokenHeader);

        if(requestTokenHeader == null && request.getMethod().equals("GET")){
            System.out.println('1');
            logger.info("Skipping filtering for URL: " + requestURL);
            chain.doFilter(request, response);
            return;
        }

        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            System.out.println('2');
            logger.warn("Authorization header is missing or malformed");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization is fail!!");
            return;

        }

        String jwtToken = requestTokenHeader.substring(7);

        try {
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.warn("JWT Token has expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token has expired");
            return;
        } catch (Exception ex) {
            logger.error("Token validation failed: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token validation failed");
            return;
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }

}
