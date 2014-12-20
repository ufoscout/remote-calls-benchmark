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
package ufo.remote.calls.benchmark.client.caller.resttemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import ufo.remote.calls.benchmark.client.caller.Tester;
import ufo.remote.calls.benchmark.client.caller.TesterResult;

public class AsyncRestTemplateTester extends Tester {

	private final AsyncRestTemplate rest = new AsyncRestTemplate();
	private final int port;
	private final String hostname;
	private final String path;

	public AsyncRestTemplateTester(final int port, final String hostname, final String path) {
		this.port = port;
		this.hostname = hostname;
		this.path = path;
	}
	@Override
	protected void startTest(final TesterResult result) {

		String url = "http://" + hostname + ":" + port + path;

		CountDownLatch latch = new CountDownLatch(result.totalCalls);
		AtomicInteger failures = new AtomicInteger(0);
		Executor executor = Executors.newFixedThreadPool(10);
		for (int i=0; i<result.totalCalls; i++) {
			executor.execute(new Caller(url, result.message, latch, failures));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		result.failures = failures.get();
	}

	private class Caller implements Runnable {

		private final CountDownLatch latch;
		private final AtomicInteger failures;
		private final String url;
		private final byte[] message;

		Caller(final String url, final byte[] message, final CountDownLatch latch, final AtomicInteger failures) {
			this.url = url;
			this.message = message;
			this.latch = latch;
			this.failures = failures;
		}


		@Override
		public void run() {
			try {

				ListenableFuture<ResponseEntity<String>> result = rest.postForEntity(url, new HttpEntity<byte[]>(message), String.class);
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


	@Override
	public String getName() {
		return "AsyncRestTemplate";
	}

}
