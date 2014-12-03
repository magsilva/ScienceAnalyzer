package net.sf.sysrev.gui.zk.i18n;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.util.RequestInterceptor;

public class LanguageDetector implements RequestInterceptor
{
	public void request(org.zkoss.zk.ui.Session sess, Object request, Object response)
	{
		final Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		if (cookies != null) {
			for (int j = cookies.length; --j >= 0;) {
				if (cookies[j].getName().equals("my.locale")) {
					// determine the locale
					String val = cookies[j].getValue();
					Locale locale = org.zkoss.util.Locales.getLocale(val);
					sess.setAttribute(Attributes.PREFERRED_LOCALE, locale);
				    // session.setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE, preferredLocale); 
				    // session.setAttribute(org.zkoss.web.Attributes.PREFERRED_TIME_ZONE, preferredTimeZone);
					return;
				}
			}
		}
	}
}
