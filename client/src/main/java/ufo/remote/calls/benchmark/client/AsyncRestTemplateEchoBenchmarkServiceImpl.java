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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

//@Service
public class AsyncRestTemplateEchoBenchmarkServiceImpl implements EchoBenchmarkService {

	private final static DecimalFormat TIME_FORMAT = new DecimalFormat("####,###.###", new DecimalFormatSymbols(Locale.US));

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AsyncRestTemplate rest;

	@Override
	public void testEcho(final int port, final int parallelThreads, final int callsPerThread) {

		String url = "http://localhost:" + port + "/test/echo/helloGet";

		logger.info("Start benchmark of [{}] with {} threads and {} calls per thread", new Object[]{url, parallelThreads, callsPerThread});
		int totalCallsNumber = parallelThreads*callsPerThread;
		CountDownLatch latch = new CountDownLatch(totalCallsNumber);
		AtomicInteger failures = new AtomicInteger(0);
		Executor executor = Executors.newFixedThreadPool(50);
		Date startTime = new Date();
		for (int i=0; i<parallelThreads; i++) {
			executor.execute(new Caller(url, callsPerThread, latch, failures));
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

	private class Caller implements Runnable {

		private final CountDownLatch latch;
		private final int callsPerThread;
		private final AtomicInteger failures;
		private final String url;

		Caller(final String url, final int callsPerThread, final CountDownLatch latch, final AtomicInteger failures) {
			this.url = url;
			this.callsPerThread = callsPerThread;
			this.latch = latch;
			this.failures = failures;
		}


		@Override
		public void run() {
			for (int i=0; i<callsPerThread; i++) {
				try {
					ListenableFuture<ResponseEntity<String>> result = rest.getForEntity(url, String.class);
					result.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
						@Override
						public void onSuccess(final ResponseEntity<String> arg0) {
							latch.countDown();
						}
						@Override
						public void onFailure(final Throwable arg0) {
							failures.incrementAndGet();
							latch.countDown();
						}
					});
				} catch (RuntimeException e) {
					failures.incrementAndGet();
					latch.countDown();
				}
			}
		}

	}

}
