package com.softhouse.integration.fileconverter;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Shahrooz on 02/17/2022.
 */
public interface FileValidatorService {

    void validateFile(MultipartFile file);

}
