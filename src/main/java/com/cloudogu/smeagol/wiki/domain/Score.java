package com.cloudogu.smeagol.wiki.domain;

import com.google.common.base.Preconditions;

/**
 * Score is a numeric value, which describes how good a search result is.
 */
public final class Score {

    private float value;

    private Score(float value) {
        this.value = value;
    }

    /**
     * Returns float representation.
     *
     * @return float representation.
     */
    public float getValue() {
        return value;
    }

    /**
     * Create score from its float representation.
     *
     * @param value float representation
     *
     * @return score from float representation
     */
    public static Score valueOf(float value) {
        Preconditions.checkArgument(value >= 0f, "only positive values allowed");
        return new Score(value);
    }
}
