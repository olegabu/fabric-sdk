package com.example.fabric2.sdk.v2x;

import io.vavr.control.Try;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FabricConfig {

    @Value("${org}")
    private String org;

    @Value("${ENROLL_SECRET:adminpw}")
    private String enrollSecret;

//    private final PrivateKey pk = X509EncodedKeySpec

    @PostConstruct
    public void init() {
        HFClient orgClient = HFClient.createNewInstance();
        Try.of(()->{orgClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());return true;}).get();
//        private final Identity identity = Identities.newX509Identity("msp1", credentials.getCertificate(), credentials.getPrivateKey());
//        Enrollment enrollment= new X509Enrollment()
//        User adminUser = new FabricUser(org, "admin", enrollSecret, );
//        User user = orgClient.setUserContext(adminUser);
        //TODO


    }
}
