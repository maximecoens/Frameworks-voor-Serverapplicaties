package iii.blog;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Value("${users.admin.username}")
    String adminUsername;

    @Value("${users.admin.password}")
    String adminPassword;

    @Value("${users.admin.roles}")
    String adminRoles;

    @Autowired
    Environment environment;

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder, DataSource dataSource) {
        LoggerFactory.getLogger(SecurityConfig.class).info("Encoded password: " + passwordEncoder.encode(adminPassword));
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.createUser(User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles(adminRoles)
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder encoder() {
        //return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests->
                        requests.requestMatchers(new AntPathRequestMatcher("/admin.html")).hasRole("ADMIN")
                                .anyRequest().permitAll())
                .httpBasic(withDefaults())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/h2-console/*") // geen CSRF voor de h2-console
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //CSRF-token in headers as cookie
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())) // Disable BREACH
                .headers(h -> h.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin())); // This so embedded frames in h2-console are working
        if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].trim().equalsIgnoreCase("test")) {
            http.csrf(csrf -> csrf.disable());
        }
        return http.build();
    }

}
