package com.cloudogu.smeagol;

public final class AccountTestData {

    private AccountTestData() {
    }

    public static final Account TRILLIAN = new Account(
            "trillian",
            "trillian123".toCharArray(),
            "Tricia McMillan",
            "trillian@hitchhiker.com"
    );

    public static final Account SLARTI = new Account(
            "slarti",
            "AwesomeFjorde".toCharArray(),
            "Slartibartfaß",
            "slarti@hitchhiker.com"
    );
}
