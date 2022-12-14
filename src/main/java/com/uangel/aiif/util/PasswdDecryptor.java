/*
 * Copyright (C) 2019. Uangel Corp. All rights reserved.
 *
 */

/**
 * PasswdDecryptor
 *
 * @file PasswdDecryptor.java
 * @author Tony Lim
 */

package com.uangel.aiif.util;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswdDecryptor {
    StandardPBEStringEncryptor crypto;

    public PasswdDecryptor(String key,
                           String alg) {
        crypto = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword(key);
        config.setAlgorithm(alg);
        crypto.setConfig(config);
    }

    public static void main(String[] args) {
        PasswdDecryptor decryptor = new PasswdDecryptor("skt_acs", "PBEWITHMD5ANDDES");
        decryptor.encrypt("acs.123");
        // tt, decryptor.decrypt0(tt) 확인
    }

    public String decrypt0(String encrypted) {
        return crypto.decrypt(encrypted);
    }

    public String encrypt(String pass) {
        return crypto.encrypt(pass);
    }

    public String decrypt(String f) {
        Pattern p = Pattern.compile("(ENC\\((.+?)\\))");
        Matcher m = p.matcher(f);
        String g = f;
        while (m.find()) {
            String enc = m.group(1);
            String encryptedPass = m.group(2);
            String pass = decrypt0(encryptedPass);
            g = StringUtils.replace(g, enc, pass);
        }

        return g;
    }
}
