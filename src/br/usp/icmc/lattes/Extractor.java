package br.usp.icmc.lattes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.WebRowSet;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlFont;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.ironiacorp.http.HttpJob;
import com.ironiacorp.http.HttpJobRunner;
import com.ironiacorp.http.HttpMethod;
import com.ironiacorp.http.HttpMethodResult;
import com.ironiacorp.http.impl.httpclient4.HttpJobRunnerHttpClient4;
import com.ironiacorp.io.IoUtil;

public class Extractor
{
	private static final int MAX_CAPTHA_TRIES = 3;
	
	private static Pattern PATTERN_LATTES_MIXED_ID = Pattern.compile("javascript:abreDetalhe\\(\'(.*)\',\'.*\',\'.*\'\\)");
	
	private static Pattern PATTERN_LATTES_NUMID_EN = Pattern.compile("<span class=\"texto\">Address to access this CV: <br>http://lattes.cnpq.br/(\\d+)</span>");

	private static Pattern PATTERN_LATTES_NUMID_PT = Pattern.compile("<span class=\"texto\">Endere&ccedil;o para acessar este CV: <br>http://lattes.cnpq.br/(\\d+)</span>");

	public Extractor()
	{
		
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

	public String guessCaptchaAlternative()
	{
		CommandLine cmdLine = new CommandLine("/usr/bin/wget");
		File outputFile = IoUtil.createTempFile("lattes-captcha", ".jpg");
		cmdLine.addArgument("--output-document=" + outputFile.getAbsolutePath());
		cmdLine.addArgument("http://buscatextual.cnpq.br/buscatextual/simagem");
		
		Executor executor = new DefaultExecutor();
		try {
			executor.setExitValue(0);
			executor.execute(cmdLine);
		} catch (Exception e) {
		}
		
		File convertedImage = runConvert(outputFile);
		String captcha = runOCR(convertedImage);
		
		return captcha;
	}
	
	public String guessCaptcha()
	{
		try {
			HttpJobRunner runner = new HttpJobRunnerHttpClient4();
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

	public String getText(String url)
	{
		HttpJobRunner runner = new HttpJobRunnerHttpClient4();
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
	
	public String runQueryAtLattes_Method1(String name, String captcha)
	{
		HttpJobRunner runner = new HttpJobRunnerHttpClient4();
		String mixedIdPageContent = null;
		try {
			HttpJob job = new HttpJob(HttpMethod.POST, new URI("http://buscatextual.cnpq.br/buscatextual/busca.do"));
			job.addParameter("metodo", "buscar");
			job.addParameter("filtros.buscaNome", "true");
			job.addParameter("buscarDoutores", "true");
			job.addParameter("buscarDemais", "true");
			job.addParameter("textoBusca", name);
			job.addParameter("palavra", captcha);
			job.addParameter("confirmarCaptcha", "Confirmar");
			runner.addJob(job);
			runner.run();
			
			HttpMethodResult result = job.getResult();
			mixedIdPageContent = new String(result.getContentAsByteArray());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		return mixedIdPageContent;
	}
	
	public String runQueryAtLattes_Method2(String name, String captcha)
	{
		WebClient browser = new WebClient();
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
			pageOutput.save(new File("/home/magsilva/teste.html"));
			pageText = pageOutput.asXml();
			System.out.println(pageText);
			browser.closeAllWindows();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return pageText;
	}
	
	public String getLattesId(String name)
	{
		String captcha = null;
		for (int i = 0; captcha == null && i < MAX_CAPTHA_TRIES; i++) {
			captcha = guessCaptcha();
		}
		
		if (captcha == null) {
			return null;
		}
	
		try {
			String mixedIdPageContent = runQueryAtLattes_Method2(name, captcha); 
			String numIdPageContent = null;
			String mixedId = null;
			String numId = null;
			
			Matcher matcherMixedId = PATTERN_LATTES_MIXED_ID.matcher(mixedIdPageContent);
			if (matcherMixedId.matches()) {
				mixedId = matcherMixedId.group(1);
			}

			numIdPageContent = getText("http://buscatextual.cnpq.br/buscatextual/visualizacv.jsp?id=" + mixedId);
			Matcher matcherNumId = PATTERN_LATTES_NUMID_PT.matcher(numIdPageContent);
			if (matcherNumId.matches()) {
				numId = matcherNumId.group(1);
			}
			
			System.out.println(numId);
		} catch (Exception e) {
		}

		
		// Load the page K4774689J1
		// Grab the Lattes ID

		
		return "";
	}
}
