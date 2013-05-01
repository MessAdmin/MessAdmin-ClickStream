/**
 * 
 */
package clime.messadmin.clickstream;

import java.util.LinkedList;

import clime.messadmin.model.Session;
import clime.messadmin.providers.lifecycle.ClickStreamGatherer;

/**
 * @author C&eacute;drik LIME
 */
public class Utils {
	private static final String USER_DATA_KEY = ClickStreamGatherer.class.getName();

	private Utils() {
	}

	public static LinkedList getPluginData(Session session) {
		LinkedList result = (LinkedList) session.getUserData().get(USER_DATA_KEY);
		if (result == null) {
			result = new LinkedList();
			session.getUserData().put(USER_DATA_KEY, result);
		}
		return result;
	}
}
