/*
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Copyright (C) 2007 Marco Aurelio Graciotto Silva <magsilva@gmail.com>
 */

package net.sf.sysrev.engines.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Map;

import net.sf.sysrev.credentials.Credential;
import net.sf.sysrev.credentials.KeyStoreCredential;
import net.sf.sysrev.credentials.PasswordCredential;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.PublicKeyDecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFDocument
{
	public static final String DEFAULT_ENCODING = "UTF-8";

	public static final String DEFAULT_PASSWORD = "";

	private File file;

	private String cachedText;

	private String[] cachedTextLines;

	private PDDocument document;

	private String encoding;


	/**
	 * Decrypts the document using the key stored into the certificate wallet (and the
	 * related alias and password), a plain password or the default password.
	 */
	public boolean decrypt(Credential credential)
	{
		boolean result = false;

		if (credential == null) {
			return decryptUsingDefaultPassword();
		}

		if (! result && credential instanceof KeyStoreCredential) {
			Map<String, Object> map = (Map<String, Object>) credential;
			result |= decryptUsingCertificate(
							(KeyStore) map.get(KeyStoreCredential.KEYSTORE),
							(String) map.get(KeyStoreCredential.ALIAS),
							(String) map.get(KeyStoreCredential.PASSWORD));
		}

		if (! result && credential instanceof PasswordCredential) {
			result |= decryptUsingPassword(
							(String) credential.get());
		}

		if (! result) {
			result |= decryptUsingDefaultPassword();
		}

		return result;
	}

	/**
	 * Decrypt the document using the default password (DEFAULT_PASSWORD).
	 *
	 * It is common for users to pass in a document that is encrypted with
	 * an empty password (such a document appears to not be encrypted by
	 * someone viewing the document, thus the confusion). We will attempt to
	 * decrypt with the empty password to handle this case.
	 */
	public boolean decryptUsingDefaultPassword()
	{
		DecryptionMaterial decryptionMaterial = new StandardDecryptionMaterial(DEFAULT_PASSWORD);
		return decryptUsingDecryptionMaterial(decryptionMaterial);
	}

	/**
	 * Decrypt the document using a plain password.
	 */
	public boolean decryptUsingPassword(String password)
	{
		DecryptionMaterial decryptionMaterial = new StandardDecryptionMaterial(password);
		return decryptUsingDecryptionMaterial(decryptionMaterial);
	}

	/**
	 * Decrypt the document using a certificate stored in the filesystem.
	 */
	public boolean decryptUsingCertificate(KeyStore keyStore, String alias, String password)
	{
		DecryptionMaterial decryptionMaterial = new PublicKeyDecryptionMaterial(keyStore, alias, password);
		return decryptUsingDecryptionMaterial(decryptionMaterial);
	}

	/**
	 * Decrypt the document.
	 */
	private boolean decryptUsingDecryptionMaterial(DecryptionMaterial dm)
	{
		try {
			document.openProtection(dm);
		} catch (Exception e) {
			return false;
		}

		AccessPermission ap = document.getCurrentAccessPermission();
		if (! ap.canExtractContent()) {
			return false;
		}

		return true;
	}


	private void setDocument(PDDocument document)
	{
		this.document = document;
		this.cachedText = null;
		this.cachedTextLines = null;
	}

	private void setFile(File file) throws FileNotFoundException
	{
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		this.file = file;
		this.cachedText = null;
		this.cachedTextLines = null;
		this.document = null;
	}

	private void refreshCache() throws IOException
	{
		PDFTextStripper stripper = new PDFTextStripper();
		cachedText = stripper.getText(document);
		cachedTextLines = cachedText.split("\n");
	}

	public PDFDocument(String filename) throws IOException
	{
		this(new File(filename));
	}

	public PDFDocument(File file) throws IOException
	{
		setFile(file);
		setDocument(PDDocument.load(file));
		setEncoding(DEFAULT_ENCODING);
		refreshCache();
	}

	private void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public PDFDocument(URL url) throws IOException
	{
		try {
			document = PDDocument.load(url, true);
			String fileName = url.getFile();
		} catch (MalformedURLException e) {
		}
	}

	public String getCachedText()
	{
		return cachedText;
	}

	public PDDocument getDocument()
	{
		return document;
	}

	public File getFilename()
	{
		return file;
	}

	public String[] getCachedTextLines()
	{
		return cachedTextLines;
	}

	public void close()
	{
		if (document != null) {
			try {
				document.close();
			} catch (IOException e) {
			}
		}
		cachedText = null;
		cachedTextLines = null;
		document = null;
		file = null;
	}

	public boolean isEncrypted()
	{
		return document.isEncrypted();
	}
}
