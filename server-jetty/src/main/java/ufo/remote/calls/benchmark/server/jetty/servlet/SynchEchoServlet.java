package ufo.remote.calls.benchmark.server.jetty.servlet;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchEchoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
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
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		logger.debug("Sync Servlet called");
		ServletInputStream bodyStream = request.getInputStream();
		String text = IOUtils.toString(bodyStream);
		logger.debug("Received message [{}]", text);
		response.getWriter().print(text);
		bodyStream.close();
	}
}