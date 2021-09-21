package com.example.fabric2.sdk.v2x;

import io.vavr.control.Try;
import lombok.Getter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.ECPrivateKey;

@Component
public class FabricConfig {

    @Value("${org}")
    private String org;
    @Value("${domain}")
    private String domain;

    @Value("${ENROLL_SECRET:adminpw}")
    private String enrollSecret;

    @Value("${crypto_config_dir}")
    private String cryptoConfigDir;

    @Getter
    private Enrollment adminEnrollment;

//    private final PrivateKey pk = X509EncodedKeySpec

//    @PostConstruct
    public void init() throws IllegalAccessException, InvocationTargetException, InvalidArgumentException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException, TransactionException, ProposalException {
        HFClient orgClient = HFClient.createNewInstance();
        orgClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        //TODO: experimental code
        Path signCertPemFile = Path.of(cryptoConfigDir, "peerOrganizations", org + "." + domain, "users", "Admin@" + org + "." + domain, "msp", "signcerts", "Admin@" + org + "." + domain + "-cert.pem");
        Path privateKeyDir = Path.of(cryptoConfigDir, "peerOrganizations", org + "." + domain, "users", "Admin@" + org + "." + domain, "msp", "keystore");

        ECPrivateKey rsaPrivateKey = Try.of(() -> Files.list(privateKeyDir).findFirst()).get().map(
                privateKyeFile ->
                        Try.withResources(() -> new FileReader(privateKyeFile.toFile())).of(
                                keyReader -> {
                                    PEMParser pemParser = new PEMParser(keyReader);
                                    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                                    PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());

                                    return (ECPrivateKey) converter.getPrivateKey(privateKeyInfo);
                                }).get()
        ).orElseThrow(() -> new RuntimeException("Private key is not found in " + privateKeyDir));

        adminEnrollment = Try.of(()->new X509Enrollment(rsaPrivateKey, Files.readString(signCertPemFile))).get();


        FabricUser adminUser = new FabricUser(org, "admin", enrollSecret, adminEnrollment);

        User user = orgClient.setUserContext(adminUser);// TODO: move to network init class

/*
        Channel test = orgClient.newChannel("test");
        test.initialize();

        Channel channel = test.joinPeer(orgClient.newPeer("peer0.org1.example.com:7051", "grpcs://localhost:7051"));
*/


    }
}
