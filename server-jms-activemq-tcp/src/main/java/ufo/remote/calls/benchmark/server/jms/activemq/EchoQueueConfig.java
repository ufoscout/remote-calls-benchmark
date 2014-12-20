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
package ufo.remote.calls.benchmark.server.jms.activemq;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EchoQueueConfig {

	public static final String ECHO_QUEUE_URL = ApacheCamelConfig.JMS_NAME + ":queue:echo";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private CamelContext camelContext;

	@PostConstruct
	public void init() {
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() {
					try {
						from(ECHO_QUEUE_URL)
						.setExchangePattern(ExchangePattern.InOut)
						.process((final Exchange exchange) -> {
							String message = (String) exchange.getIn().getBody();
							logger.debug("Received message [{}]", message);
							exchange.getOut().setBody(message, String.class);
						});
					} catch (RuntimeException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
