package com.github.phoswald.sample.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MeController {

	record UserInfo(String username) { }

	@GetMapping("/me")
	UserInfo getMe(@AuthenticationPrincipal OidcUser user) {
		return new UserInfo(user.getPreferredUsername());
	}
}
