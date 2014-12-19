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
package ufo.remote.calls.benchmark.server.jms.hornetq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ufo.remote.calls.benchmark.server.jms.hornetq.HornetQBrokerConfig;
import ufo.remote.calls.benchmark.server.jms.hornetq.Application;
import ufo.remote.calls.benchmark.server.jms.hornetq.EchoQueueConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)   // 2
public class HornetQBrokerTest {

	@Autowired
	private HornetQBrokerConfig brokerService;
	@Autowired
	private CamelContext camelContext;

	@Test
	public void testBrokerNotNull() {
		assertNotNull(brokerService);
		assertNotNull(camelContext);
	}

	@Test
	public void brokerShouldReplyToAQueue() throws InterruptedException, ExecutionException {

		ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		String message = "HelloMyLittleWorld" + UUID.randomUUID().toString();

		Future<String> response = producerTemplate.asyncRequestBody(EchoQueueConfig.ECHO_QUEUE_URL, message, String.class);
		assertEquals( message, response.get() );

	}

}
