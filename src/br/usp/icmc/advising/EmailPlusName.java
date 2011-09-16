package br.usp.icmc.advising;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class EmailPlusName
{
	public static void main(String[] args) throws IOException
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("Alumni/ICMC/alumni-icmc-posgrad-ccmc.csv");
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		CSVReader readerEmails = new CSVReader(new InputStreamReader(new FileInputStream("/home/magsilva/Erros")));
		String[] line;
		Map<String, String> data = new HashMap<String, String>();
		
		
		while ((line = reader.readNext()) != null) {
			String name = line[2];
			String[] emails = line[8].split(",");
			for (String email : emails) {
				data.put(email, name);
			}
		}
		
		while ((line = readerEmails.readNext()) != null) {
			String email = line[0];
			String reason;
			if (line.length > 1) {
				reason = line[1];
			} else {
				reason = "UNKNOWN_ERROR";
			}
			if (data.containsKey(email)) {
				System.out.println(data.get(email) + "," + email + "," + reason);
			} else {
				System.out.println("," + email + "," + reason);
			}
		}

	}
}
