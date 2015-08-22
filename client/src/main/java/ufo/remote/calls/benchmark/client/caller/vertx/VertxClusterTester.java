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

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ufo.remote.calls.benchmark.client.caller.Tester;
import ufo.remote.calls.benchmark.client.caller.TesterResult;

import com.hazelcast.config.Config;

@Service
public class VertxClusterTester extends Tester {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Vertx vertx;
	//	private final int port;
	//	private final String hostname;
	//	private final String path;

	//	public VertxClusterTester(final int port, final String hostname, final String path) {
	//		this.port = port;
	//		this.hostname = hostname;
	//		this.path = path;
	//	}

	@PostConstruct
	public void init() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final VertxOptions options = new VertxOptions();
		final Config conf = new Config();
		Vertx.clusteredVertx(options.setClusterHost("localhost").setClusterPort(0).setClustered(true)
				.setClusterManager(new HazelcastClusterManager(conf)), ar -> {
					if (ar.failed()) {
						logger.error("Error starting Vertx cluster", ar.cause());
					}
					logger.info("Vertx cluster node started [{}]");
					vertx = ar.result();
					logger.info("Initialising vertx verticles...");
					latch.countDown();
				});
		latch.await();
	}

	@Override
	protected void startTest(final TesterResult result) {

		EventBus bus = vertx.eventBus();
		CountDownLatch latch = new CountDownLatch(result.totalCalls);
		AtomicInteger failures = new AtomicInteger(0);

		for (int i=0; i<result.totalCalls; i++) {
			bus.send("echo", result.message, (AsyncResult<Message<String>> response) -> {

				if (response.failed()) {
					failures.incrementAndGet();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Received [{}]", response.result().body());
				}
				latch.countDown();
			});

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
		return "Vertx Cluster Tester";
	}

}
