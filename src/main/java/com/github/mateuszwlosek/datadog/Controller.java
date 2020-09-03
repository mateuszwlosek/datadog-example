package com.github.mateuszwlosek.datadog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

	@GetMapping("test-200")
	public void test200() {
		log.info("test 200");
	}

	@GetMapping("test-500")
	public void test500() {
		log.info("test 500");
		throw new RuntimeException();
	}
}