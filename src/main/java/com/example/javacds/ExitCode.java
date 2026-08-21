package com.example.javacds;

public enum ExitCode {

    SUCCESS(0),
    ERROR(1),
    INVALID_ARGUMENTS(2),
    CONFIGURATION_ERROR(3);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
