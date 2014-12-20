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
package ufo.remote.calls.benchmark.client.caller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TesterExecutorImpl implements TesterExecutor {

	private final static DecimalFormat TIME_FORMAT = new DecimalFormat("####,###.###", new DecimalFormatSymbols(Locale.US));
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public ExcecutionResult execute(final String testDescription, final String message, final int totalCalls, final Tester tester) {
		Date startTime = new Date();

		logger.info("Start testing [{}] with [{}] messages of [{}] bytes", new Object[]{tester.getName(), totalCalls, message.length()});
		TesterResult result = tester.start(message, totalCalls);

		ExcecutionResult execResult = new ExcecutionResult();
		execResult.testDescription = testDescription;
		execResult.testerResult = result;
		execResult.totalMessages = totalCalls;
		execResult.execMillis = new Date().getTime() - startTime.getTime();
		execResult.messagesPerSecond = ((result.totalCalls * 1000) / execResult.execMillis);

		logger.info("Time spent to send/receive {} messages of {} bytes: {} ms", new Object[]{ result.totalCalls, result.message.length(), TIME_FORMAT.format(execResult.execMillis) });
		logger.info("Failures: {}", result.failures);
		logger.info("Messages per second: {}", execResult.messagesPerSecond);

		return execResult;

	}

}
