
package com.example;

import java.util.*;
import javax.servlet.http.*;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.client.web.reactive.function.client.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.csrf.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.*;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.*;


@SpringBootApplication
@Controller
public class App extends WebSecurityConfigurerAdapter {


	@Bean
	public WebClient rest(
			ClientRegistrationRepository clients,
			OAuth2AuthorizedClientRepository authz) {

		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);

		return WebClient.builder().filter(oauth2).build();
	}


	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {

		return request -> {

			OAuth2User user =
				new DefaultOAuth2UserService().loadUser(request);

			if ( ! "github".equals(request.getClientRegistration().getRegistrationId())) {
				return user;
			}

			OAuth2AuthorizedClient client =
			  new OAuth2AuthorizedClient(
					request.getClientRegistration(),
					user.getName(),
					request.getAccessToken());

			String url =
			  user.getAttribute("organizations_url");

			List<Map<String, Object>> orgs = rest
					.get().uri(url)
					.attributes(oauth2AuthorizedClient(client))
					.retrieve()
					.bodyToMono(List.class)
					.block();

			if (orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
				return user;
			}

			throw
				new OAuth2AuthenticationException(
					new OAuth2Error("invalid_token", "Not in Spring Team", ""));
		};
	}


	@GetMapping("/user")
	@ResponseBody
	public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
		return Collections.singletonMap("name", principal.getAttribute("name"));
	}


	@GetMapping("/error")
	@ResponseBody
	public String error(HttpServletRequest request) {

		String message = (String)
			request.getSession().getAttribute("error.message");

		request.getSession().removeAttribute("error.message");

		return message;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		SimpleUrlAuthenticationFailureHandler handler =
		  new SimpleUrlAuthenticationFailureHandler("/");

		http.antMatcher("/**")
			  .authorizeRequests(a -> a
				.antMatchers("/", "/error", "/webjars/**").permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(e -> e
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			)
			.csrf(c -> c
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			)
			.logout(l -> l
				.logoutSuccessUrl("/").permitAll()
			)
			.oauth2Login(o -> o
				.failureHandler((request, response, exception) -> {
					request.getSession().setAttribute("error.message", exception.getMessage());
					handler.onAuthenticationFailure(request, response, exception);
				})
			);
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
