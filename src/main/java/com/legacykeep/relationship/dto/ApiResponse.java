package com.legacykeep.relationship.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for the Relationship Service.
 * 
 * Provides consistent response structure across all endpoints.
 * Follows the same API response pattern as other LegacyKeep services.
 * 
 * @param <T> The type of data being returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates if the request was successful
     */
    private boolean success;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Error details (only present when success is false)
     */
    private ErrorDetails error;

    /**
     * Timestamp of the response
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code
     */
    private int statusCode;

    /**
     * Create a successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .statusCode(200)
                .build();
    }

    /**
     * Create a successful response with data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .statusCode(200)
                .build();
    }

    /**
     * Create a successful response with message only
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(200)
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .build();
    }

    /**
     * Create an error response with error details
     */
    public static <T> ApiResponse<T> error(String message, ErrorDetails error, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .statusCode(statusCode)
                .build();
    }

    /**
     * Create a created response (201)
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .statusCode(201)
                .build();
    }

    /**
     * Create a no content response (204)
     */
    public static <T> ApiResponse<T> noContent(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(204)
                .build();
    }

    /**
     * Error details for failed requests
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String field;
        private String details;
        private Object value;
    }
}

