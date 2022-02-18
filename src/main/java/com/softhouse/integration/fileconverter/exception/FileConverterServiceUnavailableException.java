package com.softhouse.integration.fileconverter.exception;

import java.io.Serial;

/**
 * Created by Shahrooz on 02/17/2022.
 */
public class FileConverterServiceUnavailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1279758036120940397L;

    public FileConverterServiceUnavailableException(String message) {
        super(message);
    }

    public FileConverterServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
