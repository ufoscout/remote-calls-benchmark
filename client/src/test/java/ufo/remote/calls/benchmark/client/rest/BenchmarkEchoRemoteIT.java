package ufo.remote.calls.benchmark.client.rest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ufo.remote.calls.benchmark.client.BenchmarkBaseTest;
import ufo.remote.calls.benchmark.client.EchoBenchmarkService;

public class BenchmarkEchoRemoteIT extends BenchmarkBaseTest {

	protected final static DecimalFormat TIME_FORMAT = new DecimalFormat("####,###.###", new DecimalFormatSymbols(Locale.US));

	@Autowired
	private EchoBenchmarkService echoBenchmarkService;

	private final String remoteUrl = "http://localhost:8180/test/echo/helloGet";

	@Test
	public void testPostEchoReply() throws InterruptedException {

		int parallelThreads = 50;
		int callsPerThread = 100;

		echoBenchmarkService.testEcho(remoteUrl, parallelThreads, callsPerThread);

	}

}
