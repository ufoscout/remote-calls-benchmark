package ufo.remote.calls.benchmark.server.vertx.tcp;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class WebServerVerticleTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Vertx vertx;

	@Before
	public void setUp(TestContext context) {
		vertx = Vertx.vertx();
		vertx.deployVerticle(WebServerVerticle.class.getName(), context.asyncAssertSuccess());
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testSyncPostEchoServer(TestContext context) throws InterruptedException {
		call(context, "/test/syncEcho");
	}

	@Test
	public void testAsyncPostEchoServer(TestContext context) throws InterruptedException {
		call(context, "/test/asyncEcho");
	}

	private void call(TestContext context, String path) {
		final Async async = context.async();

		String text = UUID.randomUUID().toString();

		int port = 8183; //webServerVerticle.getPort();
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());

		HttpClientRequest req = client.request(HttpMethod.POST, port, "localhost", path, resp -> {
			resp.bodyHandler(buffer -> {
				String receivedText = buffer.toString();
				logger.info("expected text [{}], received text [{}]", text, receivedText);
				logger.info("Status [{}]", resp.statusCode());
				context.assertEquals(200, resp.statusCode());
				context.assertEquals(text, receivedText);
				async.complete();
			});
		});

		req.setChunked(true);
		req.end(text);

	}
}
