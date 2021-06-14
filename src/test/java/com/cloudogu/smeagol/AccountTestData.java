package com.cloudogu.smeagol;

public final class AccountTestData {

    private AccountTestData() {
    }

    public static final Account TRILLIAN = new Account(
            "trillian",
            "jwttrillian",
            "Tricia McMillan",
            "trillian@hitchhiker.com"
    );

    public static final Account SLARTI = new Account(
            "slarti",
            "jwtslarti",
            "Slartibartfaß",
            "slarti@hitchhiker.com"
    );
}
