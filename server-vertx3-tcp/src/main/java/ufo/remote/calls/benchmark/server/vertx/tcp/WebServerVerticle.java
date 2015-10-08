package ufo.remote.calls.benchmark.server.vertx.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.sync.SyncVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class WebServerVerticle extends SyncVerticle {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int port = 8183;

	@Override
	@Suspendable
	public void start(final Future<Void> startFuture) {
		logger.info("Starting web server with port {}", port);

		// Create the HTTP server
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setHost("localhost").setPort(port));

		// Setup the Router - this is used to route requests on different paths
		// to different handlers
		Router rm = Router.router(vertx);

		rm.route().handler(BodyHandler.create());

		rm.get("/test/syncEcho").handler(Sync.fiberHandler(context -> {
			HttpServerRequest request = context.request();
			String text = request.params().get("text");
			logger.debug("Received get param [{}]", text);
			Message<String> reply = Sync.awaitResult(h -> vertx.eventBus().send("syncEcho", text, h));
			request.response().end(reply.body());
			logger.debug("Response sent");
		}));
		rm.post("/test/syncEcho").handler(Sync.fiberHandler(this::handleSyncPostEchoRequest));
		vertx.eventBus().<String> consumer("syncEcho").handler(message -> {
			message.reply(message.body());
		});

		rm.get("/test/asyncEcho").handler(this::handleAsyncGetEchoRequest);
		rm.post("/test/asyncEcho").handler(this::handleAsyncPostEchoRequest);
		vertx.eventBus().<String> consumer("asyncEcho").handler(message -> {
			message.reply(message.body());
		});

		rm.route().handler(this::handleWebRequest);

		server.requestHandler(rm::accept);

		server.listen(res -> {
			// When the web server is listening we'll say that the start of this
			// verticle is complete
			if (res.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(res.cause());
			}
		});

		logger.info("Web server is listening");
	}

	/*
	 * Handle an in-coming request to the path /web - this is used to serve
	 * standard web resources index.html and such-like
	 */
	private void handleWebRequest(final RoutingContext context) {
		logger.debug("No handler available for request received");
		HttpServerRequest request = context.request();
		request.response().setStatusCode(403).end();
	}

	@Suspendable
	private void handleSyncGetEchoRequest(final RoutingContext context) {
		HttpServerRequest request = context.request();
		String text = request.params().get("text");
		logger.debug("Received get param [{}]", text);
		Message<String> reply = Sync.awaitResult(h -> vertx.eventBus().send("syncEcho", text, h));
		request.response().end(reply.body());
		logger.debug("Response sent");
	}


	@Suspendable
	private void handleSyncPostEchoRequest(final RoutingContext context) {
		HttpServerRequest request = context.request();
		String text = context.getBodyAsString();
		logger.debug("Received post body [{}]", text);
		Message<String> reply = Sync.awaitResult(h -> vertx.eventBus().send("syncEcho", text, h));
		request.response().end(reply.body());
		logger.debug("Response sent");
	}

	private void handleAsyncGetEchoRequest(final RoutingContext context) {
		HttpServerRequest request = context.request();
		String text = request.params().get("text");
		logger.debug("Received get param body [{}]", text);
		vertx.eventBus().<String> send("asyncEcho", text, handler -> {
			request.response().end(handler.result().body());
		});
		logger.debug("Response sent");
	}

	private void handleAsyncPostEchoRequest(final RoutingContext context) {
		logger.debug("Request received");
		HttpServerRequest request = context.request();
		String text = context.getBodyAsString();
		logger.debug("Received post body [{}]", text);
		vertx.eventBus().<String> send("asyncEcho", text, handler -> {
			request.response().end(handler.result().body());
		});
		logger.debug("Response sent");
	}

	public int getPort() {
		return port;
	}

}
