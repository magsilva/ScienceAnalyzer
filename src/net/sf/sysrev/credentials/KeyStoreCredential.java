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

 Copyright (C) 2009 Marco Aurelio Graciotto Silva <magsilva@gmail.com>
 */

package net.sf.sysrev.credentials;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class KeyStoreCredential implements Credential<Map<String, Object>>
{
	public static final String ALIAS = "alias";
	private String alias;

	public static final String PASSWORD = "password";
	private String password;

	public static final String KEYSTORE = "keystore";
	private KeyStore keyStore;

	private void setKeyStoreFile(File keyStoreFile)
	{
		if (!keyStoreFile.exists()) {
			throw new IllegalArgumentException(
							"Requested file does not exist or cannot be accessed by the current user");
		}

		if (!keyStoreFile.canRead()) {
			throw new IllegalArgumentException("Requested file cannot be read by the current user");
		}

		try {
			keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream(keyStoreFile), password.toCharArray());
		} catch (Exception e) {
			keyStore = null;
			throw new IllegalArgumentException("Cannot open the required key store file", e);
		}
	}

	public KeyStoreCredential(File keyStoreFile, String alias, String password)
	{
		this.alias = alias;
		this.password = password;
		setKeyStoreFile(keyStoreFile);
	}

	public Map<String, Object> get()
	{
		Map<String, Object> credential = new HashMap<String, Object>();
		credential.put(KEYSTORE, keyStore);
		credential.put(ALIAS, alias);
		credential.put(PASSWORD, password);

		return credential;
	}

}
