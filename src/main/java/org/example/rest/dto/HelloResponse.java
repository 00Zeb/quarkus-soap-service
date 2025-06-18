package org.example.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for REST Hello World service calls
 * Mirrors the SOAP service functionality but uses JSON
 */
public class HelloResponse {
    
    @JsonProperty("result")
    private String result;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("status")
    private String status;

    public HelloResponse() {
    }

    public HelloResponse(String result) {
        this.result = result;
        this.status = "SUCCESS";
    }

    public HelloResponse(String result, String timestamp) {
        this.result = result;
        this.timestamp = timestamp;
        this.status = "SUCCESS";
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HelloResponse{" +
                "result='" + result + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
