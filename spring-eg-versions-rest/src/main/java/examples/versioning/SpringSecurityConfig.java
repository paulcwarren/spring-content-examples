package examples.versioning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

//    @Autowired
//    private AuthenticationEntryPoint authEntryPoint;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests()
//                .anyRequest().authenticated()
//                .and().httpBasic()
//                .authenticationEntryPoint(authEntryPoint);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint getBasicAuthEntryPoint() {
        return new AuthenticationEntryPoint();
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
                withUser(User.withDefaultPasswordEncoder().username("john123").password("{noop}password").roles("USER")).
                withUser(User.withDefaultPasswordEncoder().username("paul123").password("{noop}password").roles("USER").build());
    }


        protected static String REALM = "SPRING_CONTENT";

//        @Bean
//        public WebSecurityCustomizer webSecurityCustomizer() {
//            return (web) -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**");
//        }
//    }
}
