package com.github.phoswald.sample.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * Replaces the Keycloak client registration from application.properties, which would
 * otherwise be resolved from the issuer URI over the network when the context starts.
 */
@TestConfiguration(proxyBeanMethods = false)
public class DummyClientConfig {

	@Bean
	ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(ClientRegistration.withRegistrationId("dummy")
			.clientId("dummy-client")
			.clientSecret("dummy-secret")
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
			.scope("openid", "profile", "email")
			.authorizationUri("https://dummy.invalid/auth")
			.tokenUri("https://dummy.invalid/token")
			.userInfoUri("https://dummy.invalid/userinfo")
			.jwkSetUri("https://dummy.invalid/jwks")
			.userNameAttributeName("sub")
			.build());
	}
}
