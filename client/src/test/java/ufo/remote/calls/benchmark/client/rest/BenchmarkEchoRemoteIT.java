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

	private final int port = 8184;

	@Test
	public void testPostEchoReply() throws InterruptedException {

		int parallelThreads = 1;
		int callsPerThread = 500;

		//jetty asynch
		echoBenchmarkService.testEcho(8184, parallelThreads, callsPerThread);

		//vertx
		echoBenchmarkService.testEcho(8183, parallelThreads, callsPerThread);

		//Spring boot jetty
		echoBenchmarkService.testEcho(8182, parallelThreads, callsPerThread);

	}

}
