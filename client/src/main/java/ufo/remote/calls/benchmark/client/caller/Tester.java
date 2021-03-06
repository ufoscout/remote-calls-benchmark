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


public abstract class Tester {

	public static final String BYTE_4    = "ABCD";
	public static final String BYTE_16   = "ABCD" + "EFGH" + "ILMN" + "OPQR";
	public static final String BYTE_32   = BYTE_16 + BYTE_16;
	public static final String BYTE_128  = BYTE_32 + BYTE_32 + BYTE_32 + BYTE_32;
	public static final String BYTE_512  = BYTE_128 + BYTE_128 + BYTE_128 + BYTE_128;


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
