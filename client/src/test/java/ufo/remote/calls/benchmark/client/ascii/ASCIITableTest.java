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
package ufo.remote.calls.benchmark.client.ascii;

import org.junit.Test;

public class ASCIITableTest {

	@Test
	public void basicTests() {

		String [] header = { "User Name",
				"Salary", "Designation",
				"Address", "Lucky#"
		};

		String[][] data = {
				{ "Ram", "2000", "Manager", "#99, Silk board", "1111"  },
				{ "Sri", "12000", "Developer", "BTM Layout", "22222" },
				{ "Prasad", "42000", "Lead", "#66, Viaya Bank Layout", "333333" },
				{ "Anu", "132000", "QA", "#22, Vizag", "4444444" },
				{ "Sai", "62000", "Developer", "#3-3, Kakinada"  },
				{ "Venkat", "2000", "Manager"   },
				{ "Raj", "62000"},
				{ "BTC"},
		};

		//ASCIITable.getInstance().printTable(header, ASCIITable.ALIGN_RIGHT, data, ASCIITable.ALIGN_LEFT);
		//ASCIITable.getInstance().printTable(header, data, ASCIITable.ALIGN_LEFT);

		ASCIITableHeader[] headerObjs = {
				new ASCIITableHeader("User Name", ASCIITable.ALIGN_LEFT),
				new ASCIITableHeader("Salary"),
				new ASCIITableHeader("Designation", ASCIITable.ALIGN_CENTER),
				new ASCIITableHeader("Address", ASCIITable.ALIGN_LEFT),
				new ASCIITableHeader("Lucky#", ASCIITable.ALIGN_RIGHT),
		};

		ASCIITable asciiTable = new ASCIITableImpl();

		System.out.println(asciiTable.getTable(headerObjs, data));
		System.out.println(asciiTable.getTable(header, data));
	}

}
