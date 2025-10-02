package com.kaist.api.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
class HealthController {
	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("OK");
	}

	@GetMapping("/")
	public RedirectView index() {
		return new RedirectView("/swagger-ui/index.html");
	}
}



	

