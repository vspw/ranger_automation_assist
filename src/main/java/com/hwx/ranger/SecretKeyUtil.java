package com.hwx.ranger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.SecretKeyEntry;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class SecretKeyUtil {


	private SecretKeyFactory factory;

	private KeyStore ks;

	private Path keystoreLocation;

	private char[] keystorePassword;

	public SecretKeyUtil(Path keystoreLocation, char[] keystorePassword, boolean loadExisting)
			throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {

		this.keystoreLocation = keystoreLocation;
		this.keystorePassword = keystorePassword;

		ks = KeyStore.getInstance("JCEKS");
		if (loadExisting) {
			ks.load(Files.newInputStream(keystoreLocation), keystorePassword);
		} else {
			if (Files.exists(keystoreLocation)) {
				throw new IOException("Cannot create new keystore, keystore file " + keystoreLocation
						+ " already exists");
			}
			ks.load(null, keystorePassword);
		}

		factory = SecretKeyFactory.getInstance("PBE");
	}

	public void createKeyEntry(String alias, char[] password) throws KeyStoreException, NoSuchAlgorithmException,
	CertificateException, IOException,
	InvalidKeySpecException {

		SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password));

		ks.setEntry(alias, new SecretKeyEntry(generatedSecret), new PasswordProtection(keystorePassword));

		ks.store(Files.newOutputStream(keystoreLocation), keystorePassword);

	}

	public char[] retrieveEntryPassword(String alias) throws NoSuchAlgorithmException, UnrecoverableEntryException,
	KeyStoreException, InvalidKeySpecException {

		SecretKeyEntry entry = (SecretKeyEntry) ks.getEntry(alias, new PasswordProtection(keystorePassword));
		PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(entry.getSecretKey(), PBEKeySpec.class);

		return keySpec.getPassword();

	}



}