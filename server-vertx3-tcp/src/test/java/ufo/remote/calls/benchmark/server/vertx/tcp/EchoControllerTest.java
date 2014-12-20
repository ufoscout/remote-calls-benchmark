package ufo.remote.calls.benchmark.server.vertx.tcp;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import ufo.remote.calls.benchmark.server.vertx.Application;

public class EchoControllerTest extends VertxTestBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ConfigurableApplicationContext context = SpringApplication.run(Application.class);

	@Test
	public void testGetEchoServer() throws InterruptedException {
		WebServerVerticle webServerVerticle = context.getBean(WebServerVerticle.class);

		String text = UUID.randomUUID().toString();

		int port = webServerVerticle.getPort();
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());

		HttpClientRequest req = client.request(HttpMethod.GET, port, "localhost", "/test/echo/" + text, resp -> {
			resp.bodyHandler(buffer -> {
				String receivedText = buffer.toString();
				logger.info("expected text [{}], received text [{}]", text, receivedText);
				logger.info("Status [{}]", resp.statusCode());
				assertEquals(200, resp.statusCode());
				assertEquals(text, receivedText);
				testComplete();
			});
		});

		req.setChunked(true);
		req.end();

		await(30, TimeUnit.SECONDS);
	}

	@Test
	public void testPostEchoServer() throws InterruptedException {
		WebServerVerticle webServerVerticle = context.getBean(WebServerVerticle.class);

		String text = UUID.randomUUID().toString();

		int port = webServerVerticle.getPort();
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());

		HttpClientRequest req = client.request(HttpMethod.POST, port, "localhost", "/test/asyncEcho", resp -> {
			resp.bodyHandler(buffer -> {
				String receivedText = buffer.toString();
				logger.info("expected text [{}], received text [{}]", text, receivedText);
				logger.info("Status [{}]", resp.statusCode());
				assertEquals(200, resp.statusCode());
				assertEquals(text, receivedText);
				testComplete();
			});
		});

		req.setChunked(true);
		req.end(text);

		await(30, TimeUnit.SECONDS);
	}
}
