package net.sf.sysrev.methods;

public class BlankWorkSplitter implements WordSpliter
{
	@Override
	public String[] filter(String s)
	{
		return s.split("\\s");
	}
}
