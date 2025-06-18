package org.example;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Main Quarkus application class
 */
@QuarkusMain
@ApplicationScoped
public class App implements QuarkusApplication {

    public static void main(String[] args) {
        Quarkus.run(App.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        System.out.println("🚀 Quarkus SOAP & REST Service with Mutual TLS is starting...");
        System.out.println("📡 SOAP Service available at: https://localhost:8444/soap");
        System.out.println("📋 WSDL available at: https://localhost:8444/soap/HelloWorldService?wsdl");
        System.out.println("🌐 REST Service available at: https://localhost:8444/api/hello");
        System.out.println("🔒 Using Mutual TLS (mTLS) with client certificate authentication");
        System.out.println("🔑 Client certificate required for all connections");

        Quarkus.waitForExit();
        return 0;
    }
}
