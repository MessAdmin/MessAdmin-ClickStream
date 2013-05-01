/**
 * 
 */
package clime.messadmin.providers.lifecycle;

import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clime.messadmin.clickstream.HttpRequestInfo;
import clime.messadmin.clickstream.Utils;
import clime.messadmin.filter.MessAdminThreadLocal;
import clime.messadmin.model.ErrorData;
import clime.messadmin.model.Server;
import clime.messadmin.model.Session;
import clime.messadmin.providers.spi.RequestExceptionProvider;
import clime.messadmin.providers.spi.RequestLifeCycleProvider;
import clime.messadmin.utils.SessionUtils;

/**
 * Collects statistics on Servlets, like the <a href="http://www.opensymphony.com/clickstream/">ClickStream</a> monitoring utility
 * @author C&eacute;drik LIME
 */
public class ClickStreamGatherer implements RequestLifeCycleProvider, RequestExceptionProvider {
	/**
	 * Maximum # of URLs to keep in memory. Default is {@value}.
	 */
	public static final int MAX_SIZE = 100; //TODO externalize in .properties

	/**
	 * 
	 */
	public ClickStreamGatherer() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestInitialized(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, ServletContext servletContext) {
		if (httpRequest.getSession(false) != null) {
			final String servletPath = SessionUtils.getRequestURLWithMethodAndQueryString(httpRequest);
			Session session = Server.getInstance().getSession(httpRequest.getSession(false));
			HttpRequestInfo requestInfo = new HttpRequestInfo(servletPath);
			LinkedList list = Utils.getPluginData(session);
			if (list.size() >= MAX_SIZE) {
				list.removeFirst();
			}
			list.addLast(requestInfo);
			//request.requestInitialized(requestInfo, httpRequest, servletContext);
			requestInfo.requestDate = MessAdminThreadLocal.getStartTime().getTime();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestDestroyed(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, ServletContext servletContext) {
		if (httpRequest.getSession(false) != null) {
			Session session = Server.getInstance().getSession(httpRequest.getSession(false));
			LinkedList list = Utils.getPluginData(session);
			HttpRequestInfo requestInfo;
			if (list.isEmpty()) {
				// session was created with this hit
				requestInfo = new HttpRequestInfo(SessionUtils.getRequestURLWithMethodAndQueryString(httpRequest));
				list.addLast(requestInfo);
				requestInfo.requestDate = MessAdminThreadLocal.getStartTime().getTime();
			} else {
				requestInfo = (HttpRequestInfo) list.getLast();
			}
			//request.requestDestroyed(requestInfo, (MessAdminRequestWrapper)httpRequest, (MessAdminResponseWrapper)httpResponse, servletContext);
			requestInfo.usedTime = MessAdminThreadLocal.getUsedTime();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestException(Exception e, HttpServletRequest httpRequest, HttpServletResponse httpResponse, ServletContext servletContext) {
		requestDestroyed(httpRequest, httpResponse, servletContext);
		if (httpRequest.getSession(false) != null) {
			Session session = Server.getInstance().getSession(httpRequest.getSession(false));
			HttpRequestInfo requestInfo = (HttpRequestInfo) Utils.getPluginData(session).getLast();
			//request.requestException(requestInfo, e, (MessAdminRequestWrapper)httpRequest, (MessAdminResponseWrapper)httpResponse, servletContext);
			requestInfo.error = new ErrorData(httpRequest, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPriority() {
		// no need for a priority, really
		return 10;
	}

}
