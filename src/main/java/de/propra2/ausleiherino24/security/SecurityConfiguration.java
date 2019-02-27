package de.propra2.ausleiherino24.security;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.service.SearchUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Security configuration for Spring. Includes the password encoder and configuration for : . Login
 * Page . Logout Page . Role system Also adds ResourceHandler. Its the default resource
 * destination.
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SearchUserService userDetailsService;

    @Autowired
    public SecurityConfiguration(final SearchUserService userDetailsService) {
        super();
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/index", "/login", "/signUp", "/categories",
                        "/registerNewUser", "/search", "/css/**", "/img/**", "/vendor/**", "/js/**",
                        "/Pokemon/images/**", "/Pokemon/names/**").permitAll()
                .antMatchers("/conflicts").hasRole("admin")
                .antMatchers("/**").hasAnyRole("admin", "user")
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/", true)
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout");
    }

    /**
     * password encoder to encode and check passwords.
     *
     * @return passwordEncoder Entity to be used
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(final CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(final CharSequence rawPassword, final String string) {
                return rawPassword.toString().equals(string);
            }
        };
    }
}
