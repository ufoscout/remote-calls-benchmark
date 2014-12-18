package ufo.remote.calls.benchmark.server.vertx.tcp;

import static io.vertx.ext.routematcher.RouteMatcher.routeMatcher;

import javax.annotation.PostConstruct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.routematcher.RouteMatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebServerVerticle extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${server.port}")
	private int port;

	private final String webRoot = "web";

	@PostConstruct
	public void init() {
		if (port<1) {
			port = 8080;
		}
	}

	@Override
	public void start(final Future<Void> startFuture) {
		logger.info("Starting web server with port {}", port);

		// Create the HTTP server
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setHost("localhost").setPort(port));

		// Setup the routematcher - this is used to route requests on different paths to different handlers
		RouteMatcher rm = routeMatcher();

		rm.matchMethod(HttpMethod.GET, "/test/echo/:text", this::handleEchoRequest);

		rm.noMatch(this::handleWebRequest);

		server.requestHandler(rm::accept);

		server.listen(res -> {
			// When the web server is listening we'll say that the start of this verticle is complete
			if (res.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(res.cause());
			}
		});

		logger.info("Web server is listening");
	}

	/*
  Handle an in-coming request to the path /web - this is used to serve standard web resources
  index.html and such-like
	 */
	private void handleWebRequest(final HttpServerRequest request) {
		if (request.path().contains("..")) {
			// Disallow urls crafted to see resources outside of the webroot - you may want to do some more sophisticated
			// checks here
			request.response().setStatusCode(403).end();
		} else if (request.path().equals("/")) {
			// Request for root, so send the index
			request.response().sendFile(webRoot + "/index.html");
		} else {
			// Send the requested resource
			request.response().sendFile(webRoot + request.path());
		}
	}

	/*
  Handle an order posted to the REST API
	 */
	private void handleEchoRequest(final HttpServerRequest request) {
		String text = request.params().get("text");
		logger.debug("Received parameter [{}]", text);
		request.bodyHandler(buff -> request.response().end(text));
	}

	public int getPort() {
		return port;
	}

}