package com.cloudogu.smeagol;

import com.google.common.base.MoreObjects;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * Http response of a scm-manager request.
 *
 * @param <T> type of body
 */
public class ScmHttpClientResponse<T> {

    private final HttpStatus status;
    private final T body;

    private ScmHttpClientResponse(HttpStatus status, T body) {
        this.status = status;
        this.body = body;
    }

    public boolean isSuccessful() {
        return status.is2xxSuccessful();
    }

    public boolean isNotFound() {
        return status == HttpStatus.NOT_FOUND;
    }

    public Optional<T> getBody() {
        return Optional.ofNullable(body);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("body", body)
                .toString();
    }

    public static <T> ScmHttpClientResponse<T> of(HttpStatus status, T body) {
        return new ScmHttpClientResponse<>(status, body);
    }

    public static <T> ScmHttpClientResponse<T> of(HttpStatus status) {
        return new ScmHttpClientResponse<>(status, null);
    }
}
