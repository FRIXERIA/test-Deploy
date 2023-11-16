package sit.int221.sastt3.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
//    private final AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private final AuthenticationConfiguration authConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeHttpRequests((requests) -> {
////                    try {
//                        requests
//                                .requestMatchers(OPTIONS).permitAll()
//                                .requestMatchers("/api/token").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/announcements").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/announcements/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/category").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll().and()
////                                .anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
////                    } catch (Exception ex) {
////                        throw new RuntimeException(ex);
////                    }
//                }
//                );
//        return http.build();
http.csrf().disable()
        .authorizeHttpRequests((requests)->requests
                .requestMatchers(OPTIONS).permitAll()
                .requestMatchers(HttpMethod.GET,"/api/token").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/token").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/announcements/pages").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/announcements/{id}").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/announcements/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/category").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/category/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/sendMail").permitAll()
                .requestMatchers(HttpMethod.DELETE,"/api/unsub").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/sendOTP").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/files").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/all").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/allCategory/{id}").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/verify").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/files/{filename:.+}").permitAll()
                .requestMatchers(HttpMethod.DELETE,"/api/files/{filename:.+}").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/allEmail").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/allCategory").permitAll()
                .anyRequest().authenticated())
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors();
        return  http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16,16,3,4096,1);
    }
}
