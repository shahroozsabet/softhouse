package com.softhouse.integration.fileconverter.exception;

import java.io.Serial;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CSVException extends RuntimeException {

    @Serial
    static final long serialVersionUID = -3616754821125285157L;
    private final byte[] content;

    public CSVException(String message, byte[] content) {
        super(message);
        this.content = content;
    }
}
