package com.softhouse.integration.fileconverter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@RestController
@RequiredArgsConstructor
public class FileConverterController {

    private final FileConverterService fileConverterService;

    @PostMapping(value = "/convertFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> convertFile(@RequestParam(name = "file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileConverterService.convertFile(file));
    }

}
