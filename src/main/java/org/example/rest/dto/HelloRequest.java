package org.example.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for REST Hello World service calls
 * Mirrors the SOAP service functionality but uses JSON
 */
public class HelloRequest {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("message")
    private String message;

    public HelloRequest() {
    }

    public HelloRequest(String name) {
        this.name = name;
    }

    public HelloRequest(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "HelloRequest{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
