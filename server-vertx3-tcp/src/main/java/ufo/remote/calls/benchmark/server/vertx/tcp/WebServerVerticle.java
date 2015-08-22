package ufo.remote.calls.benchmark.server.vertx.tcp;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@Service
public class WebServerVerticle extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${server.port:8080}")
	private int port = 8183;

	//private final String webRoot = "web";

	@PostConstruct
	public void init() {
		if (port<1) {
			port = 8183;
		}
	}

	@Override
	public void start(final Future<Void> startFuture) {
		logger.info("Starting web server with port {}", port);

		// Create the HTTP server
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setHost("localhost").setPort(port));

		// Setup the Router - this is used to route requests on different paths to different handlers
		Router rm = Router.router(vertx);

		rm.route().handler(BodyHandler.create());

		rm.get("/test/echo/:text").handler(this::handleEchoRequest);

		rm.post("/test/asyncEcho").handler(this::handlePostEchoRequest);

		rm.route().handler( this::handleWebRequest );

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
	private void handleWebRequest(final RoutingContext context) {
		logger.debug("No handler available for request received");
		HttpServerRequest request = context.request();
		request.response().setStatusCode(403).end();
	}

	private void handleEchoRequest(final RoutingContext context) {
		HttpServerRequest request = context.request();
		String text = request.params().get("text");
		logger.debug("Received parameter [{}]", text);
		//request.bodyHandler(buff -> {
			request.response().end(text);
			logger.debug("Response sent");
		//});
	}

	private void handlePostEchoRequest(final RoutingContext context) {
		logger.debug("Request received");
		HttpServerRequest request = context.request();
		//request.bodyHandler(buff -> {
			//String text = buff.toString();
			String text = context.getBodyAsString();
			logger.debug("Received post body [{}]", text);
			request.response().end(text);
			logger.debug("Response sent");
		//});
	}

	public int getPort() {
		return port;
	}

}
