package eu.europa.esig.dss.x509;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.utils.Utils;

public class KeyStoreCertificateSourceTest {

	private static final String KEYSTORE_PASSWORD = "dss-password";
	private static final String KEYSTORE_TYPE = "JKS";
	private static final String KEYSTORE_FILEPATH = "src/test/resources/keystore.jks";

	@Test
	public void testLoadAddAndDelete() throws IOException {
		KeyStoreCertificateSource kscs = new KeyStoreCertificateSource(new File(KEYSTORE_FILEPATH), KEYSTORE_TYPE, KEYSTORE_PASSWORD);
		assertNotNull(kscs);

		int startSize = Utils.collectionSize(kscs.getCertificates());
		assertTrue(startSize > 0);

		CertificateToken token = DSSUtils.loadCertificate(new File("src/test/resources/citizen_ca.cer"));
		kscs.addCertificateToKeyStore(token);

		int sizeAfterAdd = Utils.collectionSize(kscs.getCertificates());
		assertEquals(sizeAfterAdd,startSize + 1);
		String tokenId = token.getDSSIdAsString();

		CertificateToken certificate = kscs.getCertificate(tokenId);
		assertNotNull(certificate);

		kscs.deleteCertificateFromKeyStore(tokenId);

		int sizeAfterDelete = Utils.collectionSize(kscs.getCertificates());
		assertEquals(sizeAfterDelete,startSize);
	}

	@Test
	public void loadKeystoreAndTruststore() throws IOException {
		KeyStoreCertificateSource kscs = new KeyStoreCertificateSource(new File("src/test/resources/good-user.p12"), "PKCS12", "ks-password");
		assertTrue(kscs.getCertificates().size() > 0);

		kscs = new KeyStoreCertificateSource(new File("src/test/resources/trust-anchors.jks"), "JKS", "ks-password");
		assertTrue(kscs.getCertificates().size() > 0);
	}

	@Test
	public void testCreateNewKeystore() throws IOException {
		KeyStoreCertificateSource kscs = new KeyStoreCertificateSource(KEYSTORE_TYPE, KEYSTORE_PASSWORD);
		CertificateToken token = DSSUtils.loadCertificate(new File("src/test/resources/citizen_ca.cer"));
		kscs.addCertificateToKeyStore(token);

		kscs.store(new FileOutputStream("target/new_keystore.jks"));

		KeyStoreCertificateSource kscs2 = new KeyStoreCertificateSource("target/new_keystore.jks", KEYSTORE_TYPE, KEYSTORE_PASSWORD);
		assertEquals(1, Utils.collectionSize(kscs2.getCertificates()));
	}

	@Test(expected = DSSException.class)
	public void wrongPassword() throws IOException {
		KeyStoreCertificateSource kscs = new KeyStoreCertificateSource(new File(KEYSTORE_FILEPATH), KEYSTORE_TYPE, "wrong password");
		assertNotNull(kscs);
	}

	@Test(expected = IOException.class)
	public void wrongFile() throws IOException {
		KeyStoreCertificateSource kscs = new KeyStoreCertificateSource(new File("src/test/resources/keystore.p13"), KEYSTORE_TYPE, KEYSTORE_PASSWORD);
		assertNotNull(kscs);
	}

}
