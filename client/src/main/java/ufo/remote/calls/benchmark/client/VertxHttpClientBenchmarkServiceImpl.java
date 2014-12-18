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
package ufo.remote.calls.benchmark.client;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VertxHttpClientBenchmarkServiceImpl implements EchoBenchmarkService {

	private final static DecimalFormat TIME_FORMAT = new DecimalFormat("####,###.###", new DecimalFormatSymbols(Locale.US));

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Vertx vertx = Vertx.vertx();

	@Override
	public void testEcho(final int port, final int parallelThreads, final int callsPerThread) {

		String path = "/test/echo/helloGet";
		String url = "http://localhost:" + port + path;

		logger.info("Start benchmark of [{}] with {} threads and {} calls per thread", new Object[]{url, parallelThreads, callsPerThread});
		HttpClient client = vertx.createHttpClient(new HttpClientOptions().setIdleTimeout(5000).setConnectTimeout(5000));
		int totalCallsNumber = parallelThreads*callsPerThread;
		CountDownLatch latch = new CountDownLatch(totalCallsNumber);
		AtomicInteger failures = new AtomicInteger(0);
		Date startTime = new Date();

		for (int i=0; i<totalCallsNumber; i++) {
			int count = i+1;
			HttpClientRequest req = client.request(HttpMethod.GET, port, "localhost", path);

			req.handler(resp -> {
				//logger.info(count + " - Status [{}]", resp.statusCode());
				//assertEquals(200, resp.statusCode());
				if (resp.statusCode()!=200) {
					failures.incrementAndGet();
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

			//req.setChunked(true);
			req.end();

		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		long executionTime = new Date().getTime() - startTime.getTime();

		logger.info("Time spent to send {} request/reply messages: {} ms", totalCallsNumber, TIME_FORMAT.format(executionTime));
		logger.info("Failures: {}", failures.get());
		long messagesPerSecond = ((totalCallsNumber * 1000) / executionTime);
		logger.info("Messages per second: {}", messagesPerSecond);
	}

}
