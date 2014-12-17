package ufo.remote.calls.benchmark.server.springboot.rest;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class EchoController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value="/echo/{text}", method=RequestMethod.GET)
	public Callable<String> echo(@PathVariable final String text) {
		return () -> {
			logger.debug("Received text [{}]", text);
			return text;
		};
	}

}
