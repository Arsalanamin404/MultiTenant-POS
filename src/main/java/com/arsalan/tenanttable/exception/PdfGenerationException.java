package com.arsalan.tenanttable.exception;

public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String msg, Exception e) {
        super(msg, e);
    }
}
