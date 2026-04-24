package ch.admin.bar.siard2.cmd.utils.ssl;

import lombok.Value;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Generates an ephemeral self-signed X.509 certificate + unencrypted PKCS#8 RSA key at test runtime.
 */
@Value
public class SelfSignedCert {

    byte[] certificatePem;
    byte[] privateKeyPem;

    public static SelfSignedCert generate(String commonName, Duration validity) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048, new SecureRandom());
            KeyPair kp = kpg.generateKeyPair();

            X500Name name = new X500Name("CN=" + commonName);
            Instant now = Instant.now();
            Date notBefore = Date.from(now.minusSeconds(60));
            Date notAfter = Date.from(now.plus(validity));
            BigInteger serial = new BigInteger(64, new SecureRandom());

            JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    name, serial, notBefore, notAfter, name, kp.getPublic());
            builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(kp.getPrivate());
            X509CertificateHolder holder = builder.build(signer);

            return new SelfSignedCert(
                    toPem("CERTIFICATE", holder.getEncoded()),
                    toPem("PRIVATE KEY", kp.getPrivate().getEncoded()));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate self-signed certificate", e);
        }
    }

    private static byte[] toPem(String type, byte[] der) throws Exception {
        StringWriter sw = new StringWriter();
        try (PemWriter writer = new PemWriter(sw)) {
            writer.writeObject(new PemObject(type, der));
        }
        return sw.toString().getBytes(StandardCharsets.UTF_8);
    }
}
