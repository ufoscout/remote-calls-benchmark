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

import javax.annotation.PostConstruct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufo.remote.calls.benchmark.server.vertx.VertxService;

@Service
public class EchoVerticle  extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private VertxService vertxService;

	@PostConstruct
	public void init() {
		logger.info("Deploying verticle");
		vertxService.vertx().deployVerticle(this);
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		MessageConsumer<String> consumer = vertx.eventBus().consumer("echo");
		consumer.handler(message -> {
			message.reply(message.body());
		});
		startFuture.complete();
	}
}
