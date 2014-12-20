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
package ufo.remote.calls.benchmark.client.caller.hornetq;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ufo.remote.calls.benchmark.client.caller.Tester;
import ufo.remote.calls.benchmark.client.caller.TesterResult;

@Service
public class HornetQTester extends Tester {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier(HornetQApacheCamelConfig.JMS_NAME)
	private CamelContext camelContext;

	@Override
	protected void startTest(final TesterResult result) {

		ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		producerTemplate.setExecutorService(Executors.newFixedThreadPool(20));

		String url = HornetQApacheCamelConfig.JMS_NAME + ":queue:echo?deliveryPersistent=false&replyToDeliveryPersistent=false";
		AtomicInteger failures = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(result.totalCalls);

		for (int i=0; i<result.totalCalls; i++) {
			producerTemplate.asyncCallbackRequestBody(url, result.message, new Synchronization() {

				@Override
				public void onFailure(final Exchange exchange) {
					failures.incrementAndGet();
					latch.countDown();
				}

				@Override
				public void onComplete(final Exchange exchange) {
					if (logger.isDebugEnabled()) {
						logger.debug("Received message [{}]", exchange.getIn().getBody());
					}
					latch.countDown();
				}
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
		return "HornetQTester";
	}

}
