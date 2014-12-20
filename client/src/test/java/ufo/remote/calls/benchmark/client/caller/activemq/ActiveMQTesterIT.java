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
package ufo.remote.calls.benchmark.client.caller.activemq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ufo.remote.calls.benchmark.client.BenchmarkBaseTest;
import ufo.remote.calls.benchmark.client.caller.ExcecutionResult;
import ufo.remote.calls.benchmark.client.caller.TesterExecutor;

public class ActiveMQTesterIT extends BenchmarkBaseTest {

	@Autowired
	private TesterExecutor testerExecutor;
	@Autowired
	private ActiveMQTester activeMQTester;


	@Test
	public void testPostEchoReply() throws InterruptedException {

		int totalCalls = 500;

		//jetty asynch
		//echoBenchmarkService.testEcho(8184, parallelThreads, callsPerThread);

		//vertx
		ExcecutionResult result = testerExecutor.execute("", "helloWorld", totalCalls, activeMQTester);

		assertEquals(totalCalls, result.totalMessages);
		assertEquals(0 , result.testerResult.failures);
		//Spring boot jetty
		//echoBenchmarkService.testEcho(8182, parallelThreads, callsPerThread);

	}

}
