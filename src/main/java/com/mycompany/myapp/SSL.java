package com.mycompany.myapp;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.FileInputStream;
import java.io.FileReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class SSL {

    private static final String PRIVATE_KEY_FILE = "path/to/your/id_rsa";
    private static final String PUBLIC_CERT_FILE = "path/to/your/public_cert.pem";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        // Load the private key from id_rsa file
        PrivateKey privateKey = loadPrivateKeyFromFile(PRIVATE_KEY_FILE);

        // Load the public key from certificate
        PublicKey publicKey = loadPublicKeyFromCert(PUBLIC_CERT_FILE);

        // Generate JWT
        String jwt = generateJwt(privateKey);
        System.out.println("Generated JWT: " + jwt);

        // Verify JWT
        boolean isValid = verifyJwt(jwt, publicKey);
        System.out.println("JWT is valid: " + isValid);
    }

    private static PrivateKey loadPrivateKeyFromFile(String filename) throws Exception {
        try (FileReader keyReader = new FileReader(filename)) {
            PemReader pemReader = new PemReader(keyReader);
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PEMParser pemParser = new PEMParser(new FileReader(filename));
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            }
            throw new IllegalArgumentException("Unsupported key type");
        }
    }

    private static PublicKey loadPublicKeyFromCert(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename)) {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(fis);
            return cer.getPublicKey();
        }
    }

    private static String generateJwt(PrivateKey privateKey) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + 3600000); // 1 hour expiration

        return Jwts.builder()
                .setSubject("user123")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .claim("role", "admin")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private static boolean verifyJwt(String jwt, PublicKey publicKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}