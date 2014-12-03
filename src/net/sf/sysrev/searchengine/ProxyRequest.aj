package net.sf.sysrev.searchengine;

import java.util.Properties;

public aspect ProxyRequest
{
	Proxy proxy = new Proxy("localhost", 3333);

	pointcut establishNetworkConnection() :
		call(void Operation.Point.setX(int));


	before(): establishNetworkConnection()
	{
		Properties props = System.getProperties();
		props.setProperty("socks.proxyHost", proxy.getHost());
		props.setProperty("socks.proxyPort", Integer.toString(proxy.getPort()));
		System.setProperty("socksProxyUserName", proxy.getUsername());
		System.setProperty("socksProxyPassword", proxy.getPassword());
	}

	after(): establishNetworkConnection()
	{
		Properties props = System.getProperties();
		props.remove("socks.proxyHost");
		props.remove("socks.proxyPort");
		System.setProperty("socksProxyUserName", null);
		System.setProperty("socksProxyPassword", null);
	}
}
