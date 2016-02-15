package de.uks.webengineering.twitter.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * SecurityConfiguration class for accessing security availability.
 * local usage:   - csrf disabled
 * - inMemory user:foo
 *
 * @author Jan-Christopher Hess (jan-chr.hess@student.uni-kassel.de)
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
   private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

   @Autowired
   private DataSource dataSource;

   @Autowired
   private Environment environment;

   /*
    * global user login and security preferences.
    */
   @Override
   protected void configure(HttpSecurity http) throws Exception
   {
      /* CSRF token disabled for local usage */
      if (environment.acceptsProfiles("heroku") == false)
      {
         LOG.debug("CSRF disabled!");
         http.csrf().disable();
      }

      http.authorizeRequests()
            .antMatchers("/**").permitAll()
            .and()
            .formLogin()
            .loginPage("/login")

            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
      ;
   }

   /*
    * global user authentication preferences.
    */
   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception
   {
      /* local inMemory user user:foo for local usage */
      if (environment.acceptsProfiles("heroku") == false)
      {
         LOG.warn("Adding default user:foo");
         auth
               .inMemoryAuthentication()
               .withUser("user")
               .password("foo")
               .roles("USER")
         ;
      }

      auth
            .jdbcAuthentication()
            .passwordEncoder(new ShaPasswordEncoder(256))
            .dataSource(dataSource)
            .usersByUsernameQuery("select username,password,'true' from users where username = ?")
            .authoritiesByUsernameQuery("select username,'ROLE_USER' from users where username = ?")
      ;
   }
}