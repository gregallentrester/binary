package com.okta.preauthorize.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * OAuth 2.0 is an industry-standard authorization protocol.
 * OpenID Connect is an open standard for authentication.
 * Neither OAuth, nor OpenID provides an implementation.
 *
 * Okta has an implementation of both specifications.
 * Okta allows apps to provide login, registration,
 * SSO (or social login) services.
 *
 * This tutorial covers implementing a login function.
 *
 * Other links outline social login/registration.
 */
@SpringBootApplication
public class Any {

	public static void main(String[] a) {
		org.springframework.boot.SpringApplication.run(Any.class, a);
	}
}
