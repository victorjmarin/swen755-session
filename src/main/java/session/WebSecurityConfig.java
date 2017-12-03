package session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
    .authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/expired").permitAll()
        .antMatchers("/css/*").permitAll()
        .antMatchers("/js/*").permitAll()
        .antMatchers("/logout-users").hasRole("ADMIN")
        .anyRequest().authenticated()
        .and()
    .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
    .logout()
        .permitAll()
        .and()
    .exceptionHandling()
        .accessDeniedPage("/denied")
        .and()
    .sessionManagement()
        .maximumSessions(1)
              .sessionRegistry(sessionRegistry())
              .and()
        .invalidSessionUrl("/expired");

  }

  @Override
  public void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("user").password("user").roles("USER")
    .and().withUser("admin").password("admin").roles("ADMIN");
    ;
  }
  
  @Bean
  public SessionRegistry sessionRegistry() {
      return new SessionRegistryImpl();
  }
}
