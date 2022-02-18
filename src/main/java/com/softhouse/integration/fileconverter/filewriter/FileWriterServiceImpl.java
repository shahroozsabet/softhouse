package com.softhouse.integration.fileconverter.filewriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.softhouse.integration.fileconverter.exception.FileWriterServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@Component
@Slf4j
public class FileWriterServiceImpl implements FileWriterService {

    private final MessageSource messageSource;

    public FileWriterServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public byte[] toByteArray(String text) {
        if (!StringUtils.hasText(text)) return new byte[0];
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byteArrayOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("MSG_200317", e);
            throw new FileWriterServiceUnavailableException(messageSource.getMessage("MSG_200317", null, new Locale("en")), e);
        }
    }

}
