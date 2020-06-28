package net.greg.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void configure(HttpSecurity httpSecurity) {

    System.err.println(
      "\n\nOk " + getClass() + ".configure(HttpSecurity)");

    try {

      httpSecurity.
        authorizeRequests().
        antMatchers("/", "/public/**").permitAll().
        antMatchers("/users/**").hasAuthority("ADMIN").
        anyRequest().fullyAuthenticated().
        and().
        formLogin().
        loginPage("/login").
        failureUrl("/login?error").
        usernameParameter("email").
        permitAll().
        and().
        logout().
        logoutUrl("/logout").
        deleteCookies("remember-me").
        logoutSuccessUrl("/").
        permitAll().
        and().
        rememberMe();
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void configure(AuthenticationManagerBuilder authBuilder) {

    System.err.println(
      "\n\nOk " + getClass() +
      ".configure(AuthenticationManagerBuilder)");

    try {

      authBuilder.
        userDetailsService(userDetailsService).
        passwordEncoder(new BCryptPasswordEncoder());
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
