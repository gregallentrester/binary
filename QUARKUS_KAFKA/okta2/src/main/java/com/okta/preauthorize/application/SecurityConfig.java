package com.okta.preauthorize.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Simply adding <code>@PreAuthorize("permitAll()")</code> to the home endpoint
 * will not grant access to all requests.
 *
 * This is because the default <code>HttpBuilder</code> implementation is a
 * <i>chained-Filter</i> which remains active, taking precedence over other
 * strategies - like the <code>@PreAuthorize("permitAll()")</code> annotation.
 *
 * After disabling the default <code>HttpBuilder</code> implementation, sure
 * to explicitly enable the method-level security annotations, else they’re
 * ignored as well.
 *
 * This class contains the <code>@Configuration</code> annotation, coupled
 * with the <code>@EnableGlobalMethodSecurity(prePostEnabled = true)</code>
 * annotation affords a transition to a more granular (method-level) security
 * policy by:
 *
 * <ul style="list-style: none;">
 *   <li> Disabling the default chained-Filter <code>HttpBuilder</code>
 *   <li> Enabling method-level security annotations
 * </ul>
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  protected void configure(final HttpSecurity httpSecurity) {

    try {

      httpSecurity.
        antMatcher("/**").
        authorizeRequests().
        antMatchers("/").permitAll().
        anyRequest().authenticated().
        and().oauth2Login();
    }
    catch (Throwable e) { e.printStackTrace(); }
  }
}
