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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.Synchronization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)   // 2
public class ActiveMqBrokerServiceTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ActiveMqBrokerService brokerService;
	@Autowired
	private CamelContext camelContext;

	@Test
	public void testBrokerNotNull() {
		assertNotNull(brokerService);
		assertNotNull(camelContext);
	}

	@Test
	public void brokerShouldReplyToAQueue() throws InterruptedException {

		ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		String message = "HelloMyLittleWorld" + UUID.randomUUID().toString();

		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);

		producerTemplate.asyncCallbackSendBody(EchoQueueConfig.ECHO_QUEUE_URL, message, new Synchronization() {

			@Override
			public void onFailure(final Exchange exchange) {
				logger.error("Failure!!");
				try {
					queue.put("FAILED!");
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void onComplete(final Exchange exchange) {
				String message = (String) exchange.getIn().getBody();
				logger.info("Reply received [{}]", message);
				try {
					queue.put(message);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

		});

		assertEquals( message, queue.poll(1000, TimeUnit.SECONDS) );
	}

}
