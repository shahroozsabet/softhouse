package com.softhouse.integration.fileconverter.exception;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.softhouse.integration.fileconverter.dto.FileErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@ControllerAdvice
@Slf4j
public class FileConverterExceptionAdvice {

    @ExceptionHandler(FileErrorException.class)
    ResponseEntity<FileErrorDTO> fileExceptionHandler(FileErrorException ex) {
        return new ResponseEntity<>(ex.getFileErrorDTO(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileConverterServiceUnavailableException.class)
    void fileConverterServiceUnavailableExceptionHandler(FileConverterServiceUnavailableException ex, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage());
    }

    @ExceptionHandler(FileWriterServiceUnavailableException.class)
    void fileWriterExceptionHandler(FileWriterServiceUnavailableException ex, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage());
    }

    @ExceptionHandler(CSVException.class)
    ResponseEntity<byte[]> csvFileExceptionHandler(CSVException ex) {
        log.info("{}", ex.getContent().length < 10000 ? new String(ex.getContent()) : "cvs content length=" + ex.getContent().length);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body(ex.getContent());
    }

}
