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
package ufo.remote.calls.benchmark.server.jms.activemq;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
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
public class ActiveMqBrokerConfig {

	public static final int BROKER_PORT = 31313;
	public static final String BROKER_URL = "tcp://0.0.0.0:" + BROKER_PORT;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private BrokerService broker;
	@Autowired
	private Environment env;
	@Value("${eures.jms.broker.memory.limit.MB:128}")
	private long memoryLimit;

	@PostConstruct
	private void init() throws Exception {

		logger.info("Starting Broker at url [{}]", BROKER_URL);

		broker = new BrokerService();
		broker.setUseJmx(false);
		broker.setPersistent(false);
		broker.setDedicatedTaskRunner(false);

		//		KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
		//		persistenceAdapter.setDirectory(new File(persistencePath));
		//		broker.setPersistenceAdapter(persistenceAdapter);
		broker.addConnector(BROKER_URL);

		// TODO put in a separate method. Add configuration entry to
		// enable/disable it
		// {
		// BrokerPlugin[] plugins = new BrokerPlugin[1];
		// TimeStampingBrokerPlugin timeStampingBrokerPlugin = new
		// TimeStampingBrokerPlugin();
		// timeStampingBrokerPlugin.setProcessNetworkMessages(true);
		// timeStampingBrokerPlugin.setFutureOnly(true);
		// plugins[0] = timeStampingBrokerPlugin;
		// broker.setPlugins(plugins);
		// }

		setDefaultPolicy(broker);

		broker.start();

		logger.info("Broker started.");
	}

	private void setDefaultPolicy(final BrokerService brokerService) {
		PolicyMap policyMap = new PolicyMap();
		List<PolicyEntry> entries = new ArrayList<>();
		PolicyEntry policy = new PolicyEntry();

		// NB: ensure queue cursor limit is below the default 70% usage that the
		// destination will use
		// if they are the same, the queue memory limit and flow control will kick
		// in first
		// policy.setCursorMemoryHighWaterMark(20);

		// on by default
		// policy.setProducerFlowControl(true);

		policy.setQueue(">");

		policy.setOptimizedDispatch(true);
		policy.setMemoryLimit(memoryLimit * 1024 * 1024);
		policy.setLazyDispatch(false);
		policy.setStrictOrderDispatch(true);
		policy.setProducerFlowControl(false);
		policy.setUseCache(false);

		// policy that will spool references to disk
		// policy.setPendingQueuePolicy(new
		// FilePendingQueueMessageStoragePolicy());

		entries.add(policy);
		policyMap.setPolicyEntries(entries);
		brokerService.setDestinationPolicy(policyMap);
	}

}
