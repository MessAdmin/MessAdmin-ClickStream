/**
 *
 */
package clime.messadmin.providers.userdata;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import clime.messadmin.clickstream.HttpRequestInfo;
import clime.messadmin.clickstream.Utils;
import clime.messadmin.i18n.I18NSupport;
import clime.messadmin.model.Server;
import clime.messadmin.model.Session;
import clime.messadmin.providers.spi.SessionDataProvider;
import clime.messadmin.providers.spi.SizeOfProvider;
import clime.messadmin.utils.BytesFormat;
import clime.messadmin.utils.DateUtils;

/**
 * @author C&eacute;drik LIME
 */
public class ClickStreamDisplayer implements SessionDataProvider {
	private static final String BUNDLE_NAME = ClickStreamDisplayer.class.getName();

	/**
	 *
	 */
	public ClickStreamDisplayer() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPriority() {
		return 10;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSessionDataTitle(HttpSession httpSession) {
		ClassLoader cl = I18NSupport.getClassLoader(httpSession);
		NumberFormat numberFormatter = NumberFormat.getNumberInstance(I18NSupport.getAdminLocale());
		NumberFormat bytesFormatter = BytesFormat.getBytesInstance(I18NSupport.getAdminLocale(), true);
		Session session = Server.getInstance().getSession(httpSession);
		List data = new ArrayList(Utils.getPluginData(session));
        long currentItemSize = SizeOfProvider.Util.getObjectSize(data, cl);
		String result = I18NSupport.getLocalizedMessage(BUNDLE_NAME, cl, "title",//$NON-NLS-1$
					bytesFormatter.format(currentItemSize),
					numberFormatter.format(data.size())
				);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getXHTMLSessionData(HttpSession httpSession) {
		Session session = Server.getInstance().getSession(httpSession);
		final List data = new ArrayList(Utils.getPluginData(session));
		StringBuffer buffer = new StringBuffer(Math.max(128, data.size() * 128));
		int hiddenRequests = session.getSessionInfo().getHits() - data.size();
		// 'ol' 'start' attribute is deprecated in HTML4, but not anymore in HTML5
		buffer.append("<ol start=\"").append(Math.max(1, hiddenRequests + 1)).append("\">\n");//$NON-NLS-1$//$NON-NLS-2$
//		for (int i=0; i < hiddenRequests; ++i) {
//			buffer.append("<li style=\"display: hidden; list-style-type: none;\"></li>\n");//$NON-NLS-1$
//		}
		NumberFormat numberFormatter = NumberFormat.getNumberInstance(I18NSupport.getAdminLocale());
		Iterator iter = data.iterator();
		while (iter.hasNext()) {
			HttpRequestInfo request = (HttpRequestInfo) iter.next();
			boolean error = (request.getError() != null);
			buffer.append("<li title=\"");//$NON-NLS-1$
			buffer.append(numberFormatter.format(request.getUsedTime())).append(" ms");//$NON-NLS-1$
			buffer.append("\">");//$NON-NLS-1$
			if (error) {
				buffer.append("<span style=\"color: red; font-weight: bolder;\">");//$NON-NLS-1$
			}
			buffer.append(DateUtils.dateToFormattedDateTimeString(request.getRequestDate().getTime(), "yyyy-MM-dd HH:mm:ss")).append("&nbsp;&nbsp;");//$NON-NLS-1$//$NON-NLS-2$
			buffer.append(request.getURL());
			if (error) {
				buffer.append("</span>");//$NON-NLS-1$
			}
			buffer.append("</li>\n");//$NON-NLS-1$
		}
		buffer.append("</ol>\n");//$NON-NLS-1$

		return buffer.toString();
	}

}
