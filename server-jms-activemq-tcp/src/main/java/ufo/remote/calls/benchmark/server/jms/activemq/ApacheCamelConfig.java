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

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheCamelConfig {

	public final static String JMS_NAME = "ActiveMQ-JMS";
	@Autowired
	private ApplicationContext context;

	@Bean
	public CamelContext getCamelContext() throws Exception {
		CamelContext camelContext = new SpringCamelContext(context);
		camelContext.addComponent(JMS_NAME, JmsComponent.jmsComponentAutoAcknowledge(getPooledConnectionFactory()));
		camelContext.start();
		return camelContext;
	}

	@Bean
	public ConnectionFactory getPooledConnectionFactory() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(ActiveMqBrokerConfig.BROKER_URL);
		connectionFactory.setAlwaysSessionAsync(false);
		connectionFactory.setDispatchAsync(true);
		connectionFactory.setMaxThreadPoolSize(50);
		connectionFactory.setOptimizeAcknowledge(true);
		connectionFactory.setOptimizedMessageDispatch(true);
		connectionFactory.setUseCompression(true);

		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(connectionFactory);
		pooledConnectionFactory.setMaxConnections(50);
		return pooledConnectionFactory;
	}

}
