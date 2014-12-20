package ufo.remote.calls.benchmark.server.springboot.rest;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ufo.remote.calls.benchmark.server.springboot.BaseIntegrationTest;

public class EchoControllerTest extends BaseIntegrationTest {

	@Test
	public void testPostSyncEchoReply() {
		doTest("/test/syncEcho");
	}

	@Test
	public void testPostAsyncEchoReply() {
		doTest("/test/asyncEcho");
	}

	private void doTest(final String path) {
		RestTemplate rest = new TestRestTemplate();
		String message = UUID.randomUUID().toString();

		ResponseEntity<String> response = rest.postForEntity(serverUrl + path, message, String.class);
		assertEquals( HttpStatus.OK, response.getStatusCode());

		logger.info("Received: [{}]", response.getBody());

		assertEquals(message, response.getBody());

	}
}
