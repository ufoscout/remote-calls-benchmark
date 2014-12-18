package ufo.remote.calls.benchmark.server.jetty.servlet;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynchServlet extends HttpServlet {

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
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		final AsyncContext aCtx = request.startAsync(request, response);
		logger.debug("Servlet called");
		executor.execute(()-> {
			try {
				String path = request.getPathInfo();
				logger.debug("ServletPath [{}]", path);
				String echo = path.substring( path.lastIndexOf("/") + 1 );
				logger.debug("echo [{}]", echo);
				aCtx.getResponse()
				.getWriter()
				.print(echo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				aCtx.complete();
			}
		});
	}
}