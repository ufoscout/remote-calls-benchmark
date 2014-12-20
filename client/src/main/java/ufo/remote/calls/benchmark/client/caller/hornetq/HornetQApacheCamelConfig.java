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

import ufo.remote.calls.benchmark.client.BenchmarkConfig;

@Configuration
public class HornetQApacheCamelConfig {

	public final static int BROKER_PORT = 5495;
	public final static String JMS_NAME = "HORNETQ-JMS";
	@Autowired
	private ApplicationContext context;
	@Autowired
	private BenchmarkConfig config;

	@Bean(name=JMS_NAME)
	public CamelContext getCamelContext() throws Exception {
		CamelContext camelContext = new SpringCamelContext(context);
		camelContext.addComponent(JMS_NAME, JmsComponent.jmsComponentAutoAcknowledge(getPooledConnectionFactory()));
		camelContext.start();
		return camelContext;
	}

	public ConnectionFactory getPooledConnectionFactory() {
		Map<String, Object> connectionParams = new HashMap<String, Object>();
		connectionParams.put(TransportConstants.PORT_PROP_NAME, BROKER_PORT);
		connectionParams.put(TransportConstants.HOST_PROP_NAME, config.getHost());

		TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionParams);

		return HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);

	}

}
