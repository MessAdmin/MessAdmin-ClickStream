/**
 * 
 */
package clime.messadmin.clickstream;

import java.io.Serializable;
import java.util.Date;

import clime.messadmin.model.ErrorData;

/**
 * @author C&eacute;drik LIME
 */
public class HttpRequestInfo implements Serializable {
	public String url;
	public long requestDate = -1;// java.util.Date;
	public int usedTime = -1;//milliseconds
	public ErrorData error;

	/**
	 * 
	 */
	public HttpRequestInfo(String url) {
		super();
		this.url = url;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getURL() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 */
	public ErrorData getError() {
		return error;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getRequestDate() {
		return new Date(requestDate);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getUsedTime() {
		return usedTime;
	}

}
