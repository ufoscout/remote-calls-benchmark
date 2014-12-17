package ufo.remote.calls.benchmark.server.springboot.rest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ufo.remote.calls.benchmark.server.springboot.BaseIntegrationTest;

public class EchoControllerTest extends BaseIntegrationTest {

	@Test
	public void testGetEchoReply() {
		RestTemplate rest = new TestRestTemplate();
		ResponseEntity<String> response = rest.getForEntity(serverUrl + "/test/echo/helloGet", String.class);
		assertEquals( HttpStatus.OK, response.getStatusCode());

		logger.info("Received: [{}]", response.getBody());

		assertTrue(response.getBody().equals("helloGet"));

	}

}
