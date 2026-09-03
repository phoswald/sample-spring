package com.github.phoswald.sample.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.web.servlet.MockMvc;

import com.github.phoswald.sample.config.SecurityConfig;

@WebMvcTest(MeController.class)
@Import({ SecurityConfig.class, MeControllerTest.DummyClientConfig.class })
class MeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getMe_authenticated_returnsUsername() throws Exception {
		mockMvc.perform(get("/api/me")
				.with(oidcLogin().idToken(token -> token.claim("preferred_username", "test"))))
			.andExpect(status().isOk())
			.andExpect(content().json("{ \"username\": \"test\" }"));
	}

	@Test
	void getMe_notAuthenticated_redirectsToLogin() throws Exception {
		mockMvc.perform(get("/api/me"))
			.andExpect(status().is3xxRedirection());
	}

	/**
	 * Replaces the Keycloak client registration from application.properties, which would
	 * otherwise be resolved from the issuer URI over the network when the context starts.
	 */
	@TestConfiguration(proxyBeanMethods = false)
	static class DummyClientConfig {

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
}
