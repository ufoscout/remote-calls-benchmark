/* ----------------------------------------------------------------------------
 *     PROJECT : EURES
 *
 *     PACKAGE : eu.europa.ec.empl.eures.jms.broker
 *        FILE : BrokerService.java
 *
 *  CREATED BY : ARHS Developments
 *          ON : Nov 21, 2014
 *
 * MODIFIED BY : ARHS Developments
 *          ON : $LastChangedDate
 *     VERSION : $LastChangedRevision
 *
 * ----------------------------------------------------------------------------
 * Copyright (c) 2011 European Commission - DG EMPL
 * ----------------------------------------------------------------------------
 */
package ufo.remote.calls.benchmark.server.jms.hornetq;

import javax.annotation.PostConstruct;

import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * <class_description>
 * <p><b>notes</b>:
 * <p>ON : Nov 21, 2014
 *
 * @author ARHS Developments - Francesco Cina
 * @version $Revision
 */
@Configuration
public class HornetQBrokerConfig {

	public static final int BROKER_PORT = 5495;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private Environment env;
	@Value("${eures.jms.broker.memory.limit.MB:128}")
	private long memoryLimit;

	@PostConstruct
	private void init() throws Exception {

		logger.info("Starting Broker at port [{}]", BROKER_PORT);

		FileConfiguration configuration = new FileConfiguration();
		configuration.setConfigurationUrl("hornetq-configuration.xml");
		configuration.start();

		HornetQServer server = HornetQServers.newHornetQServer(configuration);
		JMSServerManager jmsServerManager = new JMSServerManagerImpl(server, "hornetq-jms.xml");
		//if you want to use JNDI, simple inject a context here or don't call this method and make sure the JNDI parameters are set.
		jmsServerManager.setContext(null);
		jmsServerManager.start();

		logger.info("Broker started.");
	}

}
