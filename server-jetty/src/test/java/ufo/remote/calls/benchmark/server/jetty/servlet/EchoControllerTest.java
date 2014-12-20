package ufo.remote.calls.benchmark.server.jetty.servlet;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import ufo.remote.calls.benchmark.server.jetty.Application;
import ufo.remote.calls.benchmark.server.jetty.JettyServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)   // 2
public class EchoControllerTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JettyServer jettyServer;

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

		ResponseEntity<String> response = rest.postForEntity(jettyServer.getBaseWebUrl() + path, message, String.class);
		assertEquals( HttpStatus.OK, response.getStatusCode());

		logger.info("Received: [{}]", response.getBody());

		assertEquals(message, response.getBody());

	}
}
