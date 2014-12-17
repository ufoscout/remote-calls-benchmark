package ufo.remote.calls.benchmark.server.vertx.tcp;

import org.springframework.beans.factory.annotation.Value;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.routematcher.RouteMatcher;
import static io.vertx.ext.routematcher.RouteMatcher.*;

public class WebServer extends AbstractVerticle {

	@Value("${server.port}")
	private int port;

	private final String webRoot = "web";

	@Override
	public void start(final Future<Void> startFuture) {

		// Create the HTTP server
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setHost("localhost").setPort(port));

		// Setup the routematcher - this is used to route requests on different paths to different handlers
		RouteMatcher rm = routeMatcher();

		// Requests to /api/* correspond to our (minimal) REST API
		rm.matchMethod(HttpMethod.POST, "/test/echo/{text}", this::handleEchoRequest);

		// We'll consider anything else to be a web request
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

		System.out.println("Server is listening");
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
		System.out.println("In handler order");
		// When the entire request body is read call processOrder with it
		request.bodyHandler(buff -> processOrder(buff, request.response()));
	}


}
