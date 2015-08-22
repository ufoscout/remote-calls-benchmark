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
package ufo.remote.calls.benchmark.server.vertx;

import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.config.Config;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import ufo.remote.calls.benchmark.server.vertx.tcp.WebServerVerticle;

@Service
public class VertxServiceImpl implements VertxService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Vertx vertx;

	@Autowired
	private WebServerVerticle webServerVerticle;

	@Override
	public Vertx vertx() {
		return vertx;
	}

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
					vertx.deployVerticle(WebServerVerticle.class.getName(), new DeploymentOptions().setInstances(4));
					latch.countDown();
				});
		latch.await();
	}

}
