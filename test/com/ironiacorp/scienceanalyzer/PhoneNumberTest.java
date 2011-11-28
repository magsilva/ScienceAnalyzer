package com.ironiacorp.scienceanalyzer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.scienceanalyzer.PhoneNumber;

public class PhoneNumberTest
{
	private PhoneNumber phone;
	
	@Before
	public void setUp() throws Exception
	{
		phone = new PhoneNumber();
	}

	@Test
	public void testHashCode_Equal()
	{
		phone.setInternationalCodeArea((short) 55);
		phone.setLocalCodeArea((short) 16);
		phone.setNumber(3373765);
		phone.setExtension((short) 3675);
		
		PhoneNumber phone2 = new PhoneNumber();
		phone2.setInternationalCodeArea((short) 55);
		phone2.setLocalCodeArea((short) 16);
		phone2.setNumber(3373765);
		phone2.setExtension((short) 3675);
		
		assertEquals(phone.hashCode(), phone2.hashCode());
	}

	@Test
	public void testHashCode_Different()
	{
		phone.setInternationalCodeArea((short) 55);
		phone.setLocalCodeArea((short) 16);
		phone.setNumber(3373765);
		phone.setExtension((short) 3675);
		
		PhoneNumber phone2 = new PhoneNumber();
		phone2.setInternationalCodeArea((short) 55);
		phone2.setLocalCodeArea((short) 16);
		phone2.setNumber(3373765);
		
		assertFalse(phone.hashCode() == phone2.hashCode());
	}
	
	@Test
	public void testParsePhoneNumberString()
	{
		phone.parsePhoneNumber("3211-5816");
		assertEquals(PhoneNumber.DEFAULT_COUNTRY_CODE, phone.getInternationalCodeArea());
		assertEquals(PhoneNumber.DEFAULT_STATE_AREA, phone.getInternationalCodeArea());
		assertEquals(PhoneNumber.DEFAULT_EXTENSION, phone.getExtension());
		assertEquals("32115816", phone.getIntlNumber());
	}

	@Test
	public void testPhoneNumberString()
	{
		phone.parsePhoneNumber("3211-5816");
		assertEquals("32115816", phone.toString());
	}
}
