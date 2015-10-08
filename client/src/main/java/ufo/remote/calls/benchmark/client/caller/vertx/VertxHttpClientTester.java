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
package ufo.remote.calls.benchmark.client.caller.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ufo.remote.calls.benchmark.client.caller.Tester;
import ufo.remote.calls.benchmark.client.caller.TesterResult;

public class VertxHttpClientTester extends Tester {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Vertx vertx = Vertx.vertx();
	private final int port;
	private final String hostname;
	private final String path;

	public VertxHttpClientTester(final int port, final String hostname, final String path) {
		this.port = port;
		this.hostname = hostname;
		this.path = path;
	}


	@Override
	protected void startTest(final TesterResult result) {

		HttpClient client = vertx.createHttpClient(new HttpClientOptions().setIdleTimeout(5).setConnectTimeout(5000));
		CountDownLatch latch = new CountDownLatch(result.totalCalls);
		AtomicInteger failures = new AtomicInteger(0);

		for (int i=0; i<result.totalCalls; i++) {
			int count = i+1;
			HttpClientRequest req = client.request(HttpMethod.POST, port, hostname, path);

			req.handler(resp -> {
				//logger.info(count + " - Status [{}]", resp.statusCode());
				//assertEquals(200, resp.statusCode());
				if (resp.statusCode()!=200) {
					failures.incrementAndGet();
				}
				if (logger.isDebugEnabled()) {
					resp.bodyHandler(buffer -> {
						logger.debug("Received [{}]", buffer.toString());
					});
				}

			});
			req.endHandler(handler -> {
				latch.countDown();
				//logger.info(count + " - endHandler called");
			});
			req.exceptionHandler(error -> {
				logger.error(count + " - exceptionHandler called [{}]", error.getMessage());
				failures.incrementAndGet();
				latch.countDown();
			});

			req.setChunked(true);
			req.write(Buffer.buffer(result.message));
			req.end();

		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		result.failures = failures.get();

	}

	@Override
	public String getName() {
		return "Vertx HttpClient Tester";
	}

}
