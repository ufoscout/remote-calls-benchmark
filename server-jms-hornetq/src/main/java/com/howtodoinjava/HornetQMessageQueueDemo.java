package com.howtodoinjava;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;

public class HornetQMessageQueueDemo {

	static void startServer()
	{
		try
		{
			FileConfiguration configuration = new FileConfiguration();
			configuration.setConfigurationUrl("hornetq-configuration.xml");
			configuration.start();

			HornetQServer server = HornetQServers.newHornetQServer(configuration);
			JMSServerManager jmsServerManager = new JMSServerManagerImpl(server, "hornetq-jms.xml");
			//if you want to use JNDI, simple inject a context here or don't call this method and make sure the JNDI parameters are set.
			jmsServerManager.setContext(null);
			jmsServerManager.start();
			System.out.println("Server started !!");
		}
		catch (Throwable e)
		{
			System.out.println("Damn it !!");
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) throws Exception
	{
		//Start the server
		startServer();

		Connection connection = null;
		try
		{
			// Step 1. Directly instantiate the JMS Queue object.
			Queue queue = HornetQJMSClient.createQueue("exampleQueue");

			// Step 2. Instantiate the TransportConfiguration object which
			// contains the knowledge of what transport to use,
			// The server port etc.

			Map<String, Object> connectionParams = new HashMap<String, Object>();
			connectionParams.put(TransportConstants.PORT_PROP_NAME, 5445);

			TransportConfiguration transportConfiguration = new TransportConfiguration(
					NettyConnectorFactory.class.getName(), connectionParams);

			// Step 3 Directly instantiate the JMS ConnectionFactory object
			// using that TransportConfiguration
			ConnectionFactory cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);

			// Step 4.Create a JMS Connection
			connection = cf.createConnection();

			// Step 5. Create a JMS Session
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

			// Step 6. Create a JMS Message Producer
			MessageProducer producer = session.createProducer(queue);

			// Step 7. Create a Text Message
			TextMessage message = session.createTextMessage("How to do in java dot com");

			System.out.println("Sent message: " + message.getText());

			// Step 8. Send the Message
			producer.send(message);

			// Step 9. Create a JMS Message Consumer
			MessageConsumer messageConsumer = session.createConsumer(queue);

			// Step 10. Start the Connection
			connection.start();

			// Step 11. Receive the message
			TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);

			System.out.println("Received message: " + messageReceived.getText());
		}
		finally
		{
			if (connection != null) {
				connection.close();
			}
		}
	}
}
