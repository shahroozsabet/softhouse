package com.softhouse.integration.fileconverter.exception;

import java.io.Serial;

import com.softhouse.integration.fileconverter.dto.FileErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileErrorException extends RuntimeException {

    @Serial
    static final long serialVersionUID = 1488617718443574515L;
    private final FileErrorDTO fileErrorDTO;

    public FileErrorException(FileErrorDTO fileErrorDTO) {
        super(fileErrorDTO.toString());
        this.fileErrorDTO = fileErrorDTO;
    }
}
