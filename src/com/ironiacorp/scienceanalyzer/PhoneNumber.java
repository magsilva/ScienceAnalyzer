package com.ironiacorp.scienceanalyzer;

public class PhoneNumber
{
	public static final short DEFAULT_COUNTRY_CODE = 0;
	
	public static final short DEFAULT_STATE_AREA = 0;
	
	public static final short DEFAULT_EXTENSION = -1;
	
	private short internationalCodeArea;
	
	private short localCodeArea;
	
	private int number;
	
	private short extension;

	public void parsePhoneNumber(String phoneNumber)
	{
		parsePhoneNumber(phoneNumber, DEFAULT_COUNTRY_CODE, DEFAULT_STATE_AREA);
	}

	public void parsePhoneNumber(String phoneNumber, short defaultCountryCode, short defaultStateArea)
	{
		String[] numbers = phoneNumber.split("\\(\\)");
		switch (numbers.length) {
			case 1:
				internationalCodeArea = defaultCountryCode;
				localCodeArea = defaultStateArea;
				number = Integer.parseInt(numbers[0].replaceAll("\\D", ""));
				extension = -1;
				break;
			case 2:
				internationalCodeArea = defaultCountryCode;
				localCodeArea = Short.parseShort(numbers[0].replaceAll("\\D", ""));
				number = Integer.parseInt(numbers[1].replaceAll("\\D", ""));
				extension = -1;
				break;
			case 3:
				internationalCodeArea = Short.parseShort(numbers[0].replaceAll("\\D", ""));
				localCodeArea = Short.parseShort(numbers[1].replaceAll("\\D", ""));
				number = Integer.parseInt(numbers[2].replaceAll("\\D", ""));
				extension = -1;
				break;
			case 4:
				internationalCodeArea = Short.parseShort(numbers[0].replaceAll("\\D", ""));
				localCodeArea = Short.parseShort(numbers[1].replaceAll("\\D", ""));
				number = Integer.parseInt(numbers[2].replaceAll("\\D", ""));
				extension = Short.parseShort(numbers[3].replaceAll("\\D", ""));
				break;
		}
	}
	
	public short getInternationalCodeArea() {
		return internationalCodeArea;
	}

	public void setInternationalCodeArea(short internationalCodeArea) {
		this.internationalCodeArea = internationalCodeArea;
	}

	public short getLocalCodeArea() {
		return localCodeArea;
	}

	public void setLocalCodeArea(short localCodeArea) {
		this.localCodeArea = localCodeArea;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public short getExtension() {
		return extension;
	}

	public void setExtension(short extension) {
		this.extension = extension;
	}
	
	public String getIntlNumber()
	{
		StringBuilder sb = new StringBuilder();
		if (internationalCodeArea > 0) {
			sb.append("+");
			sb.append(internationalCodeArea);
			sb.append(" ");
		}
		sb.append(getLocalNumber());
		
		return sb.toString();
	}
	
	public String getLocalNumber()
	{
		StringBuilder sb = new StringBuilder();
		if (localCodeArea > 0) {
			sb.append("(");
			sb.append(localCodeArea);
			sb.append(")");
			sb.append(" ");
		}
		
		if (number > 0) {
			sb.append(number);
		}
		
		if (extension > 0) {
			sb.append(" (extension ");
			sb.append(extension);
			sb.append(")");
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return getIntlNumber();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + extension;
		result = prime * result + internationalCodeArea;
		result = prime * result + localCodeArea;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { 
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		PhoneNumber other = (PhoneNumber) obj;
		
		if (extension != other.extension) {
			return false;
		}
		
		if (internationalCodeArea != other.internationalCodeArea) {
			return false;
		}
		
		if (localCodeArea != other.localCodeArea) {
			return false;
		}
		
		if (number != other.number) {
			return false;
		}
		
		return true;
	}	
}
