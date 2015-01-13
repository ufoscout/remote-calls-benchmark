/*******************************************************************************
 * Copyright 2015 Francesco Cina'
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
package ufo.remote.calls.benchmark.server.vertx.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.test.core.VertxTestBase;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import ufo.remote.calls.benchmark.server.vertx.Application;
import ufo.remote.calls.benchmark.server.vertx.VertxService;

public class EchoVerticleTest extends VertxTestBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ConfigurableApplicationContext context = SpringApplication.run(Application.class);

	@Test
	public void testGetEchoServer() throws InterruptedException {
		Vertx vertx = context.getBean(VertxService.class).vertx();

		final String message = UUID.randomUUID().toString();
		vertx.eventBus().send("echo", message, (AsyncResult<Message<String>> response) -> {
			assertTrue(response.succeeded());
			logger.info("expected text [{}], received text [{}]", message, response.result().body());
			assertEquals(message, response.result().body());
			testComplete();
		});

		await(30, TimeUnit.SECONDS);
	}

}
