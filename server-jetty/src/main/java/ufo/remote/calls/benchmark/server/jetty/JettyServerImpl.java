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
package ufo.remote.calls.benchmark.server.jetty;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ufo.remote.calls.benchmark.server.jetty.servlet.AsynchEchoServlet;
import ufo.remote.calls.benchmark.server.jetty.servlet.SynchEchoServlet;

@Service
public class JettyServerImpl implements JettyServer {

	private static final String CONTEXT = "/";
	///private static final String TEMP_DIR = "./target/jetty-temp";
	//private static final String[] WEB_APP_DIRS = new String[] { "./target/" };

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Server server;

	@Value("${server.port}")
	private int port;
	private int realPort; // This field is initialized with the real port number used by jetty

	@PostConstruct
	private void init() {
		try {
			server = new Server(port<0 ? 0 : port);

			final WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath(CONTEXT);
			webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
			// webAppContext.setParentLoaderPriority(true);
			//webAppContext.setBaseResource(new ResourceCollection(WEB_APP_DIRS));
			//webAppContext.setTempDirectory(new File(TEMP_DIR));

			webAppContext.setConfigurations(new Configuration[] { new WebXmlConfiguration(), new AnnotationConfiguration()});

			ServletHolder asyncServletHolder = new ServletHolder();
			asyncServletHolder.setAsyncSupported(true);
			asyncServletHolder.setServlet(new AsynchEchoServlet());
			webAppContext.addServlet(asyncServletHolder, "/test/asyncEcho");

			ServletHolder syncServletHolder = new ServletHolder();
			syncServletHolder.setAsyncSupported(false);
			syncServletHolder.setServlet(new SynchEchoServlet());
			webAppContext.addServlet(syncServletHolder, "/test/syncEcho");

			server.setHandler(webAppContext);

			server.start();
			realPort = ((NetworkConnector) server.getConnectors()[0]).getLocalPort();
			logger.info("Jetty server started on port: " + realPort);
			// SERVER.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public int getPort() {
		return realPort;
	}

	@Override
	public String getBaseWebUrl() {
		return "http://localhost:" + getPort() + CONTEXT;
	}
}
