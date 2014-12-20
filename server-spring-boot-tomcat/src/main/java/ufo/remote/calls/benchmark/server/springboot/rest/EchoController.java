package ufo.remote.calls.benchmark.server.springboot.rest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/test")
public class EchoController {

	private static final Executor executor = Executors.newFixedThreadPool(8);
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value="/asyncEcho", method=RequestMethod.POST)
	public DeferredResult<String> asyncEcho(@RequestBody final String text) {
		DeferredResult<String> result = new DeferredResult<String>();

		executor.execute(() -> {
			logger.debug("Received text [{}]", text);
			result.setResult(text);
		});

		return result;
	}

	@RequestMapping(value="/syncEcho", method=RequestMethod.POST)
	public String syncEcho(@RequestBody final String text) {
		logger.debug("Received text [{}]", text);
		return text;
	}

}
