package net.sf.sysrev.gui.zk.i18n;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

public class CustomThemeProvider implements ThemeProvider
{

	public Collection getThemeURIs(Execution exec, List uris)
	{
		for (Iterator it = uris.iterator(); it.hasNext();) {
			if ("~./zul/css/norm*.css.dsp*".equals(it.next()))
				it.remove();
		}
		uris.add("~./zul/css/norm*.css.dsp*");
		return uris;
	}

}
