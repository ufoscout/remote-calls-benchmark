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
package ufo.remote.calls.benchmark.server.jetty.servlet;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import ufo.remote.calls.benchmark.server.jetty.Application;
import ufo.remote.calls.benchmark.server.jetty.JettyServer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)   // 2
public class AsynchServletTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JettyServer jettyServer;

	@Test
	public void testGetEchoReply() {
		RestTemplate rest = new TestRestTemplate();
		ResponseEntity<String> response = rest.getForEntity(jettyServer.getBaseWebUrl() + "/test/echo/helloGet", String.class);
		assertEquals( HttpStatus.OK, response.getStatusCode());

		logger.info("Received: [{}]", response.getBody());

		assertTrue(response.getBody().equals("helloGet"));

	}

}
