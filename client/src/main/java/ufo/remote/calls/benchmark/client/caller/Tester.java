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

	public static final byte[] BYTE_4    = RandomUtils.nextBytes(4);
	public static final byte[] BYTE_16   = RandomUtils.nextBytes(16);
	public static final byte[] BYTE_32   = RandomUtils.nextBytes(32);
	public static final byte[] BYTE_64   = RandomUtils.nextBytes(64);
	public static final byte[] BYTE_128  = RandomUtils.nextBytes(128);
	public static final byte[] BYTE_256  = RandomUtils.nextBytes(256);
	public static final byte[] BYTE_512  = RandomUtils.nextBytes(512);
	public static final byte[] BYTE_1K   = RandomUtils.nextBytes(1024);
	public static final byte[] BYTE_2K   = RandomUtils.nextBytes(2*1024);
	public static final byte[] BYTE_4K   = RandomUtils.nextBytes(4*1024);
	public static final byte[] BYTE_8K   = RandomUtils.nextBytes(8*1024);
	public static final byte[] BYTE_16K  = RandomUtils.nextBytes(16*1024);
	public static final byte[] BYTE_32K  = RandomUtils.nextBytes(32*1024);
	public static final byte[] BYTE_64K  = RandomUtils.nextBytes(64*1024);
	public static final byte[] BYTE_128K = RandomUtils.nextBytes(128*1024);
	public static final byte[] BYTE_256K = RandomUtils.nextBytes(256*1024);
	public static final byte[] BYTE_512K = RandomUtils.nextBytes(512*1024);
	public static final byte[] BYTE_1M   = RandomUtils.nextBytes(1024*1024);


	public TesterResult start(final byte[] message, final int totalCalls) {
		TesterResult result = new TesterResult();
		result.message = message;
		result.totalCalls = totalCalls;
		startTest( result );
		return result;
	}

	protected abstract void startTest(TesterResult result);

	public abstract String getName();

}
