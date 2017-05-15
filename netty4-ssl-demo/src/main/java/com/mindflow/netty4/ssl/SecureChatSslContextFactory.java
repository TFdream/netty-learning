package com.mindflow.netty4.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-16 23:36
 */
public class SecureChatSslContextFactory {
    private static final String PROTOCOL = "TLS";
    private static SSLContext SERVER_CONTEXT;
    private static SSLContext CLIENT_CONTEXT;

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    public static SSLContext getServerContext(String tlsMode, String pkPath,
                                              String caPath) {
        if (SERVER_CONTEXT == null) {
            InputStream in = null;
            InputStream tIN = null;
            try {
                // Set up key manager factory to use our key store
                KeyManagerFactory kmf = null;
                if (pkPath != null) {
                    KeyStore ks = KeyStore.getInstance("JKS");
                    in = new FileInputStream(pkPath);
                    ks.load(in, "sNetty".toCharArray());
                    kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(ks, "sNetty".toCharArray());
                }
                TrustManagerFactory tf = null;
                if (caPath != null) {
                    KeyStore tks = KeyStore.getInstance("JKS");
                    tIN = new FileInputStream(caPath);
                    tks.load(tIN, "sNetty".toCharArray());
                    // tks.load(tIN, "123456".toCharArray());
                    tf = TrustManagerFactory.getInstance("SunX509");
                    tf.init(tks);
                }
                // Initialize the SSLContext to work with our key managers.
                SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
                if (SSLMODE.CA.toString().equals(tlsMode))
                    SERVER_CONTEXT.init(kmf.getKeyManagers(), null, null);
                else if (SSLMODE.CSA.toString().equals(tlsMode)) {
                    SERVER_CONTEXT.init(kmf.getKeyManagers(),
                            tf.getTrustManagers(), null);
                } else {
                    throw new Error(
                            "Failed to initialize the server-side SSLContext"
                                    + tlsMode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error(
                        "Failed to initialize the server-side SSLContext", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                in = null;
                if (tIN != null)
                    try {
                        tIN.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                tIN = null;
            }
        }
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    public static SSLContext getClientContext(String tlsMode, String pkPath,
                                              String caPath) {
        if (CLIENT_CONTEXT == null) {
            InputStream in = null;
            InputStream tIN = null;
            try {
                // Set up key manager factory to use our key store
                KeyManagerFactory kmf = null;
                if (pkPath != null) {
                    KeyStore ks = KeyStore.getInstance("JKS");
                    in = new FileInputStream(pkPath);
                    ks.load(in, "cNetty".toCharArray());
                    // ks.load(in, "123456".toCharArray());
                    kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(ks, "cNetty".toCharArray());
                    // kmf.init(ks, "123456".toCharArray());
                }

                // Set up trust manager factory to use our key store
                // TrustManagerFactory tmf = TrustManagerFactory
                // .getInstance("SunX509");
                // tmf.init(ks);
                TrustManagerFactory tf = null;
                if (caPath != null) {
                    KeyStore tks = KeyStore.getInstance("JKS");
                    tIN = new FileInputStream(caPath);
                    tks.load(tIN, "cNetty".toCharArray());
                    tf = TrustManagerFactory.getInstance("SunX509");
                    tf.init(tks);
                }
                // Initialize the SSLContext to work with our key managers.
                CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
                if (SSLMODE.CA.toString().equals(tlsMode))
                    CLIENT_CONTEXT.init(null,
                            tf == null ? null : tf.getTrustManagers(), null);
                else if (SSLMODE.CSA.toString().equals(tlsMode)) {
                    CLIENT_CONTEXT.init(kmf.getKeyManagers(),
                            tf.getTrustManagers(), null);
                } else {
                    throw new Error(
                            "Failed to initialize the client-side SSLContext"
                                    + tlsMode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error(
                        "Failed to initialize the client-side SSLContext", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                in = null;
            }
        }
        return CLIENT_CONTEXT;
    }
}
