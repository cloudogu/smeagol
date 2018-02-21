package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.repository.domain.Description;
import com.cloudogu.smeagol.repository.domain.Name;
import com.cloudogu.smeagol.repository.domain.Repository;
import com.cloudogu.smeagol.repository.domain.RepositoryId;

import java.time.Instant;

public final class RepositoryTestData {

    private RepositoryTestData() {
    }

    public static Repository createHeartOfGold() {
        return new Repository(
            RepositoryId.valueOf("4xQfahsId3"),
            Name.valueOf("hitchhiker/heartOfGold"),
            Description.valueOf("Heart Of Gold"),
            Instant.parse("1985-04-09T10:15:30.00Z")
        );
    }

    public static Repository createRestaurantAtTheEndOfTheUniverse() {
        return new Repository(
            RepositoryId.valueOf("30QQIOlg42"),
            Name.valueOf("hitchhiker/restaurantAtTheEndOfTheUniverse"),
            Description.valueOf(""),
            Instant.parse("2018-01-28T16:58:42.00Z")
        );
    }

}
