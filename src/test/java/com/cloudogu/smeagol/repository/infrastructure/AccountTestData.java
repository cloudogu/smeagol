package com.cloudogu.smeagol.repository.infrastructure;

import com.cloudogu.smeagol.Account;

public final class AccountTestData {

    private AccountTestData() {
    }

    public static final Account TRILLIAN = new Account(
            "trillian",
            "trillian123".toCharArray(),
            "Tricia McMillan",
            "trillian@hitchhiker.com"
    );
}
