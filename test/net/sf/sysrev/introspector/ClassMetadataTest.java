package net.sf.sysrev.introspector;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ClassMetadataTest
{
	private class TestClass<T> {
	}

	@Before
	public void setUp() throws Exception
	{
		
	}

	@Test
	public void testGetType()
	{
		TestClass<String> t = new TestClass<String>();
		ClassMetadata metadata = new ClassMetadata(t.getClass());
		assertEquals(String.class, metadata.getGenericTypes()[0]);
	}
	
	@Test
	public void testStringType()
	{
		Class clazz = "Teste".getClass();
		ClassMetadata metadata = new ClassMetadata(clazz);
	}
	
	@Test
	public void testSimpleGenericType()
	{
		TestClass<String> test = new TestClass<String>();
		Class clazz = test.getClass();
		ClassMetadata metadata = new ClassMetadata(clazz);
		assertEquals(String.class, metadata.getGenericTypes()[0]);
	}
}
