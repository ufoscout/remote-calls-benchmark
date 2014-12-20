package ufo.remote.calls.benchmark.server.jetty.servlet;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynchEchoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Executor executor = Executors.newFixedThreadPool(4);

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void init() {
		logger.info("Servlet started");
	}

	@Override
	public void destroy() {
		logger.info("Servlet destroyed");
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		logger.debug("Async Servlet called");

		final AsyncContext aCtx = request.startAsync(request, response);
		executor.execute(()-> {
			try {
				ServletInputStream bodyStream = request.getInputStream();
				String text = IOUtils.toString(bodyStream);
				logger.debug("Received message [{}]", text);
				aCtx.getResponse().getWriter().print(text);

				//ServletOutputStream responseStream = aCtx.getResponse().getOutputStream();
				//IOUtils.copy(bodyStream, responseStream);
				//responseStream.close();

				bodyStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				aCtx.complete();
			}
		});
	}
}