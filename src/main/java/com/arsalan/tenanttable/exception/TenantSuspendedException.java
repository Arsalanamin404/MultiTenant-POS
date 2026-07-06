package com.arsalan.tenanttable.exception;

public class TenantSuspendedException extends RuntimeException {
    public TenantSuspendedException(String s) {
        super(s);
    }
}
