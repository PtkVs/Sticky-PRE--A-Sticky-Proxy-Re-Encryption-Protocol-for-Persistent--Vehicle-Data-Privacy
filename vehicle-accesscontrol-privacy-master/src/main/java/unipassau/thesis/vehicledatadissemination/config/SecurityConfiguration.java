package unipassau.thesis.vehicledatadissemination.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("alice").password(passwordEncoder().encode("alice")).roles("data_owner").and()
                .withUser("bob").password(passwordEncoder().encode("bob")).roles("data_consumer").and()
                .withUser("carlos").password(passwordEncoder().encode("carlos")).roles("data_consumer");
    }

    /**
     * This section defines the security policy for the app.
     * - BASIC authentication is supported (enough for this REST-based demo)

     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            //  Change to /authorize or /benchmark according to the requirement i.e authorize for real work and benchmark for just benchmarking.
        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/authorize").hasRole("data_consumer").and()
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}