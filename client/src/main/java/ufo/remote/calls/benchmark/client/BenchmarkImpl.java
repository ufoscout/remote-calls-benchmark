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
package ufo.remote.calls.benchmark.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufo.remote.calls.benchmark.client.ascii.ASCIITable;
import ufo.remote.calls.benchmark.client.ascii.ASCIITableHeader;
import ufo.remote.calls.benchmark.client.ascii.ASCIITableImpl;
import ufo.remote.calls.benchmark.client.caller.ExcecutionResult;
import ufo.remote.calls.benchmark.client.caller.Tester;
import ufo.remote.calls.benchmark.client.caller.TesterExecutor;
import ufo.remote.calls.benchmark.client.caller.activemq.ActiveMQTester;
import ufo.remote.calls.benchmark.client.caller.hornetq.HornetQTester;
import ufo.remote.calls.benchmark.client.caller.resttemplate.AsyncRestTemplateTester;

@Service
public class BenchmarkImpl implements Benchmark {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TesterExecutor testerExecutor;
	@Autowired
	private BenchmarkConfig config;
	@Autowired
	private ActiveMQTester activeMQTester;
	@Autowired
	private HornetQTester hornetQTester;

	@Override
	public void start() {
		final List<ExcecutionResult> results = new ArrayList<ExcecutionResult>();

		String servletAsyncPath = "/test/asyncEcho";
		String servletSyncPath = "/test/syncEcho";

		int port_spring_boot_tomcat = 8180;
		int port_spring_boot_jetty = 8181;
		//int port_spring_boot_undertow = 8182;
		int port_vertx3_tcp = 8183;
		int port_jetty = 8184;

		int[] rampUpMessages = new int[]{100, 1000, 10000};

		for (int messages : rampUpMessages) {
			//doBenchmark("Spring-Boot-Undertow", port_spring_boot_undertow, parallelThreads, callsPerThread);

			doBenchmark("Vertx 3 TCP Server", results, messages, new AsyncRestTemplateTester(port_vertx3_tcp, config.getHost(), servletAsyncPath));

			doBenchmark("Jetty Async Servlet", results, messages, new AsyncRestTemplateTester(port_jetty, config.getHost(), servletAsyncPath));
			doBenchmark("Jetty Sync Servlet", results, messages, new AsyncRestTemplateTester(port_jetty, config.getHost(), servletSyncPath));

			doBenchmark("SpringBoot Jetty Async", results, messages, new AsyncRestTemplateTester(port_spring_boot_jetty, config.getHost(), servletAsyncPath));
			doBenchmark("SpringBoot Jetty Sync", results, messages, new AsyncRestTemplateTester(port_spring_boot_jetty, config.getHost(), servletSyncPath));

			doBenchmark("SpringBoot Tomcat Async", results, messages, new AsyncRestTemplateTester(port_spring_boot_tomcat, config.getHost(), servletAsyncPath));
			doBenchmark("SpringBoot Tomcat Sync", results, messages, new AsyncRestTemplateTester(port_spring_boot_tomcat, config.getHost(), servletSyncPath));

			doBenchmark("ActiveMQ TCP", results, messages, activeMQTester);

			doBenchmark("HornetQ", results, messages, hornetQTester);
		}

		printResults(results);
	}


	private void doBenchmark(final String testDescriptio, final List<ExcecutionResult> results, final int calls, final Tester tester) {
		logger.info("------------------------------------------------------------------------");
		logger.info("Start [{}] benchmark", testDescriptio);

		results.add( testerExecutor.execute(testDescriptio, Tester.BYTE_32, calls, tester) );

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}

		results.add( testerExecutor.execute(testDescriptio, Tester.BYTE_128, calls, tester) );

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}

		results.add( testerExecutor.execute(testDescriptio, Tester.BYTE_512, calls, tester) );
		logger.info("------------------------------------------------------------------------");
	}

	private void printResults(final List<ExcecutionResult> results) {

		ASCIITableHeader[] headers = {
				new ASCIITableHeader("TYPE", ASCIITable.ALIGN_LEFT),
				new ASCIITableHeader("MESSAGES", ASCIITable.ALIGN_RIGHT),
				new ASCIITableHeader("BYTES_PM", ASCIITable.ALIGN_RIGHT),
				new ASCIITableHeader("FAIL", ASCIITable.ALIGN_RIGHT),
				new ASCIITableHeader("TOT_TIME", ASCIITable.ALIGN_RIGHT),
				new ASCIITableHeader("MPS", ASCIITable.ALIGN_RIGHT)
		};

		List<String[]> data = new ArrayList<>();
		results.forEach(result -> {
			data.add( new String[]{
					result.testDescription,
					"" + result.totalMessages,
					"" + result.testerResult.message.length(),
					"" + result.testerResult.failures,
					result.execMillis + "ms",
					"" + result.messagesPerSecond });
		});

		System.out.println(new ASCIITableImpl().getTable(headers, data.toArray(new String[][]{})));
	}
}
