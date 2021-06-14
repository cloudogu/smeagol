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

    public static final String LONG_LASTING_JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImp0aSI6IjZSU2FKVmdWdkgiLCJpYXQiOjE1MjM2NzU2ODAsImV4cCI6OTUyMzY3OTI4MCwic2NtLW1hbmFnZXIucmVmcmVzaEV4cGlyYXRpb24iOjE2MjM3MTg4ODAyODQsInNjbS1tYW5hZ2VyLnBhcmVudFRva2VuSWQiOiI2UlNhSlZnVnZIIn0.ignored";
}
