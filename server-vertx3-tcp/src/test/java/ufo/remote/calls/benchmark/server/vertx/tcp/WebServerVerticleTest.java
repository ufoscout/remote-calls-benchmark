/*******************************************************************************
 * Copyright 2014 Francesco Cina'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ufo.remote.calls.benchmark.server.vertx.tcp;

import static org.junit.Assert.assertEquals;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ufo.remote.calls.benchmark.server.vertx.VertxBaseTest;
import ufo.remote.calls.benchmark.server.vertx.VertxService;

public class WebServerVerticleTest extends VertxBaseTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private VertxService vertx;
	@Autowired
	private WebServerVerticle webServerVerticle;

	@Test
	public void testEchoServer() throws InterruptedException {
		String text = UUID.randomUUID().toString();

		int port = webServerVerticle.getPort();


		HttpClient client = vertx.vertx().createHttpClient(new HttpClientOptions());


		BlockingQueue<String> responses = new ArrayBlockingQueue<>(10);
		HttpClientRequest req = client.request(HttpMethod.GET, port, "localhost", "/test/echo/" + text, resp -> {
			resp.bodyHandler(buffer -> {
				String receivedText = buffer.toString();
				logger.info("expected text [{}], received text [{}]", text, receivedText);
				logger.info("Status [{}]", resp.statusCode());
				assertEquals(200, resp.statusCode());
				try {
					responses.put(receivedText);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		});

		req.setChunked(true);
		req.end();

		String receivedText = responses.poll(5, TimeUnit.SECONDS);
		assertEquals(text, receivedText);
	}

}
