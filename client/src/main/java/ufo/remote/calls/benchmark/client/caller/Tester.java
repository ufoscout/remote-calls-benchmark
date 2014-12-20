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

import org.apache.commons.lang3.RandomUtils;

public abstract class Tester {

	public static final String BYTE_4    = new String(RandomUtils.nextBytes(4));
	public static final String BYTE_16   = new String(RandomUtils.nextBytes(16));
	public static final String BYTE_32   = new String(RandomUtils.nextBytes(32));
	public static final String BYTE_64   = new String(RandomUtils.nextBytes(64));
	public static final String BYTE_128  = new String(RandomUtils.nextBytes(128));
	public static final String BYTE_256  = new String(RandomUtils.nextBytes(256));
	public static final String BYTE_512  = new String(RandomUtils.nextBytes(512));
	public static final String BYTE_1K   = new String(RandomUtils.nextBytes(1024));
	public static final String BYTE_2K   = new String(RandomUtils.nextBytes(2*1024));
	public static final String BYTE_4K   = new String(RandomUtils.nextBytes(4*1024));
	public static final String BYTE_8K   = new String(RandomUtils.nextBytes(8*1024));
	public static final String BYTE_16K  = new String(RandomUtils.nextBytes(16*1024));
	public static final String BYTE_32K  = new String(RandomUtils.nextBytes(32*1024));
	public static final String BYTE_64K  = new String(RandomUtils.nextBytes(64*1024));
	public static final String BYTE_128K = new String(RandomUtils.nextBytes(128*1024));
	public static final String BYTE_256K = new String(RandomUtils.nextBytes(256*1024));
	public static final String BYTE_512K = new String(RandomUtils.nextBytes(512*1024));
	public static final String BYTE_1M   = new String(RandomUtils.nextBytes(1024*1024));


	public TesterResult start(final String message, final int totalCalls) {
		TesterResult result = new TesterResult();
		result.message = message;
		result.totalCalls = totalCalls;
		startTest( result );
		return result;
	}

	protected abstract void startTest(TesterResult result);

	public abstract String getName();

}
