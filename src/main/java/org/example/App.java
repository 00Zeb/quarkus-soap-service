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
        System.out.println("🚀 Quarkus SOAP Service with HTTPS is starting...");
        System.out.println("📡 SOAP Service available at: https://localhost:8443/soap");
        System.out.println("📋 WSDL available at: https://localhost:8443/soap/HelloWorldService?wsdl");
        System.out.println("🔒 Using HTTPS with self-signed certificate");

        Quarkus.waitForExit();
        return 0;
    }
}
