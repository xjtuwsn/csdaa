package com.csdaa.wsn.server.utils;

import org.hyperledger.fabric.gateway.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

@Component
public class FabricFactory {

    private static String channelName;
    private static String networkConfigPath;
    private static String contractName;
    private static String certificatePath;
    private static String privateKeyPath;
    public static X509Certificate certificate;
    public static PrivateKey privateKey;
    public static Network network;
    public static String path;
    static {

        try {
            Properties properties = new Properties();
            InputStream inputStream =FabricFactory.class.getResourceAsStream("/fabric.config.properties");
            properties.load(inputStream);
            path = FabricFactory.class.getClassLoader().getResource("").toURI().toString();
            System.out.println(path);
            networkConfigPath = properties.getProperty("networkConfigPath");
            channelName = properties.getProperty("channelName");
            contractName = properties.getProperty("contractName");
            //使用org1中的user1初始化一个网关wallet账户用于连接网络
            certificatePath = properties.getProperty("certificatePath");

            certificate = readX509Certificate(Paths.get(certificatePath));
            privateKeyPath = properties.getProperty("privateKeyPath");
            privateKey = getPrivateKey(Paths.get(privateKeyPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Bean
    public Contract getContract(){

        try {
            Wallet wallet = Wallets.newInMemoryWallet();
            wallet.put("user1", Identities.newX509Identity("Org1MSP",certificate,privateKey));
            //根据connection.json 获取Fabric网络连接对象
            Gateway.Builder builder = null;
            builder = Gateway.createBuilder()
                    .identity(wallet, "user1")
                    .networkConfig(Paths.get(networkConfigPath));
            //连接网关
            Gateway gateway = builder.connect();
            //获取通道
            network = gateway.getNetwork(channelName);
            //获取合约对象
            Contract contract = network.getContract(contractName);
            return contract;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }
}
