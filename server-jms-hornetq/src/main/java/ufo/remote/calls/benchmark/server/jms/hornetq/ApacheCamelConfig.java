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

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spring.SpringCamelContext;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheCamelConfig {

	public final static String JMS_NAME = "HORNETQ-JMS";
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
		Map<String, Object> connectionParams = new HashMap<String, Object>();
		connectionParams.put(TransportConstants.PORT_PROP_NAME, HornetQBrokerConfig.BROKER_PORT);

		TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionParams);

		return HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);

		//		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		//		pooledConnectionFactory.setConnectionFactory(connectionFactory);
		//		pooledConnectionFactory.setMaxConnections(50);
		//		return pooledConnectionFactory;
	}

}
