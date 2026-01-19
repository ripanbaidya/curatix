package com.curatix.common.util;

import com.curatix.common.response.PageInfo;
import com.curatix.common.response.ResponseWrapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Utility class for building standardized API responses.
 * Simplifies response creation in controllers.
 */
public final class ResponseUtil {

    private ResponseUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Creates 200 OK response with data
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message, T data) {
        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Creates 200 OK response without data
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message) {
        return ResponseEntity.ok(ResponseWrapper.ok(message));
    }

    /**
     * Creates 201 CREATED response
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> created(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(message, data));
    }

    /**
     * Creates 202 ACCEPTED response (for async operations)
     */
    public static <T> ResponseEntity<ResponseWrapper<T>> accepted(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ResponseWrapper.accepted(message, data));
    }

    /**
     * Creates 204 NO CONTENT response
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates paginated response from Spring Data Page
     */
    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> paginated(
            String message, Page<T> page
    ) {
        Map<String, Object> data = Map.of(
                "content", page.getContent(),
                "page", PageInfo.from(page)
        );

        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Creates paginated response with filters
     */
    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> paginatedWithFilters(
            String message, Page<T> page, Map<String, Object> filters
    ) {
        Map<String, Object> data = Map.of(
                "content", page.getContent(),
                "page", PageInfo.from(page),
                "filters", filters
        );

        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Creates empty collection response
     */
    public static ResponseEntity<ResponseWrapper<Map<String, Object>>> emptyCollection(
            String message, int pageSize
    ) {
        Map<String, Object> data = Map.of(
                "content", List.of(),
                "page", new PageInfo(0, pageSize, 0, 0)
        );

        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Creates batch operation response
     */
    public static <T> ResponseEntity<ResponseWrapper<Map<String, Object>>> batch(
            String message, int total, List<T> successful, List<Map<String, Object>> failed
    ) {
        Map<String, Object> summary = Map.of(
                "total", total,
                "successful", successful.size(),
                "failed", failed.size()
        );

        Map<String, Object> data = Map.of(
                "summary", summary,
                "successful", successful,
                "failed", failed
        );

        return ResponseEntity.ok(ResponseWrapper.ok(message, data));
    }

    /**
     * Creates async operation response
     */
    public static ResponseEntity<ResponseWrapper<Map<String, Object>>> async(
            String message, String jobId, String status,
            Integer estimatedTime, String statusUrl
    ) {

        Map<String, Object> data = Map.of(
                "jobId", jobId,
                "status", status,
                "estimatedTime", estimatedTime,
                "statusUrl", statusUrl
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ResponseWrapper.accepted(message, data));
    }

}
