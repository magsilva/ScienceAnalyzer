package br.usp.icmc.lattes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringEscapeUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.ironiacorp.http.HttpJob;
import com.ironiacorp.http.HttpJobRunner;
import com.ironiacorp.http.HttpMethod;
import com.ironiacorp.http.HttpMethodResult;
import com.ironiacorp.http.impl.httpclient4.HttpJobRunnerHttpClient4;
import com.ironiacorp.io.IoUtil;

public class Extractor
{
	private HttpJobRunner runner;

	private WebClient browser;

	
	private static final int MAX_CAPTHA_TRIES = 3;
	
	private static Pattern PATTERN_LATTES_MIXED_ID = Pattern.compile("javascript:abreDetalhe\\('(.*)','.*','.*'\\)");
	
	private static Pattern PATTERN_LATTES_NUMID_EN = Pattern.compile("<span class=\"texto\">Address to access this CV: <br>http://lattes.cnpq.br/(\\d+)</span>");

	private static Pattern PATTERN_LATTES_NUMID_PT = Pattern.compile("<span class=\"texto\">Endere&ccedil;o para acessar este CV: <br>http://lattes.cnpq.br/(\\d+)</span>");

	public Extractor()
	{
		 runner = new HttpJobRunnerHttpClient4();
		 browser = new WebClient(BrowserVersion.FIREFOX_3_6); //, "127.0.0.1", 8888);
		 // browser = new WebClient(BrowserVersion.FIREFOX_3_6, "127.0.0.1", 8888);
	}

	public File runConvert(File input)
	{
		CommandLine cmdLine = new CommandLine("/usr/bin/convert");
		File output = new File(IoUtil.replaceExtension(input.getAbsolutePath(), ".ppm"));
		cmdLine.addArgument(input.getAbsolutePath());
		cmdLine.addArgument(output.getAbsolutePath());
		
		try {
			Executor executor = new DefaultExecutor();
			executor.setExitValue(0);
			executor.execute(cmdLine);
		} catch (Exception e) {
			output = null;
		}
		
		return output; 
	}
	
	public String runOCR(File input)
	{
		CommandLine cmdLine = new CommandLine("/usr/bin/ocrad");
		cmdLine.addArgument("-q");
		cmdLine.addArgument("-l");
		cmdLine.addArgument(input.getAbsolutePath());
		
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		Executor executor = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdout, stderr);
		executor.setStreamHandler(streamHandler);
		try {
			executor.setExitValue(0);
			executor.execute(cmdLine);
			
			BufferedReader reader = new BufferedReader(new StringReader(stdout.toString()));
			String line;
			while ((line = reader.readLine()) != null) {
				String word = line.trim();
				word = word.replace("\'", "");
				word = word.replace("\\", "");
				word = word.replace("/", "");
				word = word.replace(",", "");
				word = word.replace("_", "");
				word = word.replace(" ", "");
				word = word.replace(":", "");
				word = word.replace(";", "");
				word = word.replace(".", "");
				word = word.replace("\"", "");
				if (word.length() >= 4) {
					return word.substring(0, 4);
				}
			}
		} catch (Exception e) {
		}
		
		return null;
	}

	public String guessCaptcha()
	{
		try {
			HttpJob job = new HttpJob(HttpMethod.GET, new URI("http://buscatextual.cnpq.br/buscatextual/simagem"));
			runner.addJob(job);
			runner.run();

			HttpMethodResult result = job.getResult();
			File image = result.getContentAsFile();
			File convertedImage = runConvert(image);
			String captcha = runOCR(convertedImage);
			return captcha;
		} catch (URISyntaxException e) {
		}
		
		return null;
	}

	public String guessCaptcha2()
	{
		try {
			UnexpectedPage page = browser.getPage("http://buscatextual.cnpq.br/buscatextual/simagem");
			File image = new File("/home/magsilva/teste.jpg");
			image.delete();
			IoUtil.toFile(page.getInputStream(), image);
			File convertedImage = runConvert(image);
			String captcha = runOCR(convertedImage);
			return captcha;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public String getText(String url)
	{
		HttpJobRunner runner = new HttpJobRunnerHttpClient4();;
		HttpJob job;
		try {
			job = new HttpJob(HttpMethod.GET, new URI(url));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		runner.addJob(job);
		runner.run();

		HttpMethodResult result = job.getResult();
		String text = new String(result.getContentAsByteArray());
		
		return text;
	}
	
	public String getText2(String url)
	{
		try {
			HtmlPage page = browser.getPage(url);
			return page.asXml();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/*
	public String getText3(String url)
	{
		try {
			Page page = browser.getPage(url);
			File image = new File("/home/magsilva/texto.txt");
			image.delete();
			InputStreamReader isr = new InputStreamReader(page.getInputStream());
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	*/

	
	public String runQueryAtLattes(String name, String captcha)
	{
		String mixedIdPageContent = null;
		try {
			HttpJob job = new HttpJob(HttpMethod.POST, new URI("http://buscatextual.cnpq.br/buscatextual/busca.do"));
			job.addParameter("metodo", "buscar");
			job.addParameter("filtros.buscaNome", "true");
			job.addParameter("buscarDoutores", "true");
			job.addParameter("buscarDemais", "true");
			job.addParameter("textoBusca", name);
			job.addParameter("palavra", captcha);
			runner.addJob(job);
			runner.run();
			
			HttpMethodResult result = job.getResult();
			mixedIdPageContent = new String(result.getContentAsByteArray());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		return mixedIdPageContent;
	}
	
	public String runQueryAtLattes2(String name, String captcha)
	{
		String pageText = null;
		try {
			HtmlPage pageInput = browser.getPage("http://www.ironiacorp.com/Projects/Alumni/lattes2.html");
			HtmlForm form = pageInput.getFormByName("buscaForm");
			HtmlHiddenInput methodField = form.getInputByName("metodo");
			HtmlHiddenInput searchNameFilterField = form.getInputByName("filtros.buscaNome");
			HtmlHiddenInput searchPhdField = form.getInputByName("buscarDoutores");
			HtmlHiddenInput searchOthersField = form.getInputByName("buscarDemais");
			HtmlTextInput nameField = form.getInputByName("textoBusca");
			HtmlTextInput captchaField = form.getInputByName("palavra");
			HtmlSubmitInput submitButton = form.getInputByName("confirmarCaptcha");
			
			methodField.setValueAttribute("buscar");
			searchNameFilterField.setValueAttribute("true");
			searchPhdField.setValueAttribute("true");
			searchOthersField.setValueAttribute("true");
			nameField.setValueAttribute(name);
			captchaField.setValueAttribute(captcha);
			
			HtmlPage pageOutput = submitButton.click();
			pageText = pageOutput.asXml();
			pageText = StringEscapeUtils.unescapeHtml(pageText);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return pageText;
	}

	
	public String runQueryAtLattes3(String name, String captcha)
	{
		WebClient browser = new WebClient(BrowserVersion.FIREFOX_3_6, "127.0.0.1", 8888);
		String pageText = null;
		try {
			WebRequest request = new WebRequest(new URL("http://buscatextual.cnpq.br/buscatextual/busca.do"));
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new NameValuePair("metodo", "buscar"));
			parameters.add(new NameValuePair("filtros.buscaNome", "true"));
			parameters.add(new NameValuePair("buscarDoutores", "true"));
			parameters.add(new NameValuePair("buscarDemais", "true"));
			parameters.add(new NameValuePair("textoBusca", name));
			parameters.add(new NameValuePair("palavra", captcha));
			request.setHttpMethod(com.gargoylesoftware.htmlunit.HttpMethod.POST);
			request.setRequestParameters(parameters);

			// Finally, we can get the page
			HtmlPage page = browser.getPage(request);
			File outputFile = new File("/home/magsilva/teste.html");
			outputFile.delete();
			page.save(outputFile);
			pageText = page.asXml();
			browser.closeAllWindows();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return pageText;
	}

	

	
	public String getLattesId(String name)
	{
		String captcha = null;
		Random random = new Random();
		for (int i = 0; captcha == null && i < MAX_CAPTHA_TRIES; i++) {
			captcha = guessCaptcha2();
			if (captcha == null) {
		    	try {
					Thread.sleep(random.nextInt(1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (captcha == null) {
			return null;
		}
	
		try {
			String mixedIdPageContent = runQueryAtLattes2(name, captcha); 
			String numIdPageContent = null;
			String mixedId = null;
			String numId = null;
			
			Matcher matcherMixedId = PATTERN_LATTES_MIXED_ID.matcher(mixedIdPageContent);
			if (matcherMixedId.find()) {
				mixedId = matcherMixedId.group(1);
				
				numIdPageContent = getText("http://buscatextual.cnpq.br/buscatextual/visualizacv.jsp?id=" + mixedId);
				Matcher matcherNumId = PATTERN_LATTES_NUMID_PT.matcher(numIdPageContent);
				if (matcherNumId.find()) {
					numId = matcherNumId.group(1);
				}
			}
			
			return numId;
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}
	
	public void reset()
	{
		browser.closeAllWindows();
	}
	
	
	private void printLine(PrintWriter writer, String[] line, String id)
	{
		for (int i = 0; i < 6; i++) {
			writer.print("\"");
			writer.print(line[i]);
			writer.print("\"");
			writer.print(",");
		}
		if (id == null) {
			writer.println(0);
		} else {
			writer.println(id);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		Extractor extractor = new Extractor();
		CSVReader reader = new CSVReader(new FileReader("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Alumni.csv"));
	    String [] line;
	    Random random = new Random();
	    int mintime = 6000;
	    PrintWriter writer = new PrintWriter("/home/magsilva/Projects/ICMC/LattesAnalyzer/resources/Alumni-with-lattesId.csv");
	    String id = null;
	    
	    while ((line = reader.readNext()) != null) {
	    	String name = line[3];
	    	if (line.length < 7) {
	    		id = extractor.getLattesId(name);
		    	System.out.println(name + "," + id);
		    	int time = random.nextInt(2 * mintime);
		    	Thread.sleep(mintime + time);
	    	} else {
	    		id = line[6];
		    	System.out.println(name + "," + id);
	    	}
	    	extractor.printLine(writer, line, id);
		    writer.flush();
	    }
	    writer.close();
	}
}
