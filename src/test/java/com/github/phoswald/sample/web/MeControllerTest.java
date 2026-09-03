package com.github.phoswald.sample.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.github.phoswald.sample.config.DummyClientConfig;
import com.github.phoswald.sample.config.SecurityConfig;

@WebMvcTest(MeController.class)
@Import({ SecurityConfig.class, DummyClientConfig.class })
class MeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getMe_authenticated_returnsUsername() throws Exception {
		mockMvc.perform(get("/api/me")
				.with(oidcLogin().idToken(token -> token.claim("preferred_username", "sample-user"))))
			.andExpect(status().isOk())
			.andExpect(content().json("{ \"username\": \"sample-user\" }"));
	}

	@Test
	void getMe_notAuthenticated_redirectsToLogin() throws Exception {
		mockMvc.perform(get("/api/me"))
			.andExpect(status().is3xxRedirection());
	}
}
