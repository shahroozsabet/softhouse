package com.softhouse.integration.fileconverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.softhouse.integration.fileconverter.dto.CSVFieldA;
import com.softhouse.integration.fileconverter.dto.CSVFieldF;
import com.softhouse.integration.fileconverter.dto.CSVFieldP;
import com.softhouse.integration.fileconverter.dto.CSVFieldT;
import com.softhouse.integration.fileconverter.dto.CSVFollowerF;
import com.softhouse.integration.fileconverter.dto.CSVFollowerP;
import com.softhouse.integration.fileconverter.dto.CSVType;
import com.softhouse.integration.fileconverter.dto.FileErrorDTO;
import com.softhouse.integration.fileconverter.exception.CSVException;
import com.softhouse.integration.fileconverter.exception.CSVLineErrorDTO;
import com.softhouse.integration.fileconverter.exception.FileConverterServiceUnavailableException;
import com.softhouse.integration.fileconverter.exception.FileErrorException;
import com.softhouse.integration.fileconverter.filewriter.FileWriterService;
import com.softhouse.integration.util.CommonEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.Tika;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@Slf4j
@Component
public class FileValidatorServiceImpl implements FileValidatorService {

    private final MessageSource messageSource;
    private final Tika tika;
    private final FileWriterService fileWriterService;

    public FileValidatorServiceImpl(MessageSource messageSource, Tika tika, FileWriterService fileWriterService) {
        this.messageSource = messageSource;
        this.tika = tika;
        this.fileWriterService = fileWriterService;
    }

    /**
     * Several check will be run in turn:
     * emptyCheck, mimeTypeCheck, checkFirstCharacter, checkFirstCharacters, fieldsOrderCheck, fieldsCheck
     * IF an Error is found the makeErrorCSVText will be run, and thrown.
     * <p>
     * Running time measured to see which method is bottleneck.
     *
     * @param file The file to be validated
     */
    @Override
    public void validateFile(MultipartFile file) {
        StopWatch stopWatch = new StopWatch("validateFile");
        stopWatch.start("emptyCheck");
        emptyCheck(file);
        stopWatch.stop();
        stopWatch.start("mimeTypeCheck");
        mimeTypeCheck(file);
        stopWatch.stop();
        stopWatch.start("checkFirstCharacter");
        checkFirstCharacter(file);
        stopWatch.stop();
        stopWatch.start("checkFirstCharacters");
        checkFirstCharacters(file);
        stopWatch.stop();
        stopWatch.start("fieldsOrderCheck");
        fieldsOrderCheck(file);
        stopWatch.stop();
        stopWatch.start("fieldsCheck");
        List<CSVLineErrorDTO> errors = fieldsCheck(file);
        stopWatch.stop();
        stopWatch.start("makeErrorCSVText");
        String errorFile = makeErrorCSVText(errors);
        stopWatch.stop();
        log.info("file with size={} validated, running time (s) = {}", file.getSize(), stopWatch.getTotalTimeSeconds());
        log.info("{}", stopWatch.prettyPrint());
        if (!errors.isEmpty())
            throw new CSVException(messageSource.getMessage("MSG_200319", null, new Locale("en")), fileWriterService.toByteArray(errorFile));
    }

    private void emptyCheck(MultipartFile file) {
        FileErrorDTO fileErrorDTO = new FileErrorDTO();
        if (file == null || file.isEmpty()) {
            fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200304", new String[]{""}, new Locale("en")));
            throw new FileErrorException(fileErrorDTO);
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
            fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200304", new String[]{fileName}, new Locale("en")));
            throw new FileErrorException(fileErrorDTO);
        }
        try (InputStream inputStream = file.getInputStream()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    if (bufferedReader.lines().allMatch(line -> line.trim().isEmpty())) {
                        fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200304", new String[]{fileName}, new Locale("en")));
                        throw new FileErrorException(fileErrorDTO);
                    }
                }
            }
        } catch (IOException e) {
            log.error("MSG_200313", e);
            throw new FileConverterServiceUnavailableException(messageSource.getMessage("MSG_200313", new String[]{fileName}, new Locale("en")), e);
        }
    }

    private void mimeTypeCheck(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            String detectedMimeType = tika.detect(file.getBytes());
            if (!MimeTypeUtils.TEXT_PLAIN_VALUE.equals(detectedMimeType)) {
                FileErrorDTO fileErrorDTO = new FileErrorDTO();
                fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200303", new String[]{fileName, MimeTypeUtils.TEXT_PLAIN_VALUE, detectedMimeType}, new Locale("en")));
                throw new FileErrorException(fileErrorDTO);
            }
        } catch (IOException e) {
            log.error("MSG_200321", e);
            throw new FileConverterServiceUnavailableException(messageSource.getMessage("MSG_200321", new String[]{fileName}, new Locale("en")), e);
        }
    }

    private void checkFirstCharacters(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    List<String> csvTypes = Arrays.stream(CSVType.values()).map(CSVType::name).toList();
                    if (bufferedReader.lines().filter(line -> !line.trim().isEmpty()).map(line -> line.trim().split(Pattern.quote(CommonEnum.CSVDelimiter.PIPE.getValue()))).map(strings -> strings[0]).anyMatch(string -> !csvTypes.contains(string))) {
                        FileErrorDTO fileErrorDTO = new FileErrorDTO();
                        fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200101", new String[]{Arrays.toString(CSVType.values())}, new Locale("en")));
                        throw new FileErrorException(fileErrorDTO);
                    }
                }
            }
        } catch (IOException e) {
            log.error("MSG_200313", e);
            throw new FileConverterServiceUnavailableException(messageSource.getMessage("MSG_200313", new String[]{fileName}, new Locale("en")), e);
        }
    }

    private void checkFirstCharacter(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    if (!CSVType.P.name().equals(bufferedReader.lines().filter(line -> !line.trim().isEmpty()).map(line -> line.trim().split(Pattern.quote(CommonEnum.CSVDelimiter.PIPE.getValue()))).map(strings -> strings[0]).findFirst().get())) {
                        FileErrorDTO fileErrorDTO = new FileErrorDTO();
                        fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200100", new String[]{Arrays.toString(CSVType.values())}, new Locale("en")));
                        throw new FileErrorException(fileErrorDTO);
                    }
                }
            }
        } catch (IOException e) {
            log.error("MSG_200313", e);
            throw new FileConverterServiceUnavailableException(messageSource.getMessage("MSG_200313", new String[]{fileName}, new Locale("en")), e);
        }
    }

    private void fieldsOrderCheck(MultipartFile file) {
        FileErrorDTO fileErrorDTO = new FileErrorDTO();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    List<String> firstChars = bufferedReader.lines().filter(line -> !line.trim().isEmpty()).map(line -> line.trim().split(Pattern.quote(CommonEnum.CSVDelimiter.PIPE.getValue()))).map(strings -> strings[0]).toList();
                    List<String> followerP = Arrays.stream(CSVFollowerP.values()).map(CSVFollowerP::name).toList();
                    List<String> followerF = Arrays.stream(CSVFollowerF.values()).map(CSVFollowerF::name).toList();
                    for (int i = 0; i < firstChars.size() - 1; i++) {
                        String firstChar = firstChars.get(i);
                        if (firstChar.equals(CSVType.P.name()) && !followerP.contains(firstChars.get(i + 1))) {
                            fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200102", new String[]{CSVType.P.name(), Arrays.toString(CSVFollowerP.values())}, new Locale("en")));
                            throw new FileErrorException(fileErrorDTO);
                        }
                        if (firstChar.equals(CSVType.F.name()) && !followerF.contains(firstChars.get(i + 1))) {
                            fileErrorDTO.getGeneralErrors().add(messageSource.getMessage("MSG_200102", new String[]{CSVType.F.name(), Arrays.toString(CSVFollowerF.values())}, new Locale("en")));
                            throw new FileErrorException(fileErrorDTO);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("MSG_200313", e);
            throw new FileConverterServiceUnavailableException(messageSource.getMessage("MSG_200313", new String[]{fileName}, new Locale("en")), e);
        }
    }

    private List<CSVLineErrorDTO> fieldsCheck(MultipartFile file) {
        List<CSVLineErrorDTO> errors = new ArrayList<>();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    AtomicInteger lineNo = new AtomicInteger(0);
                    bufferedReader.lines().filter(line -> !line.trim().isEmpty()).map(line -> line.trim().split(Pattern.quote(CommonEnum.CSVDelimiter.PIPE.getValue()))).forEach(columns -> {
                        lineNo.incrementAndGet();
                        if (CSVType.P.name().equals(columns[0])) {
                            if (CSVFieldP.values().length != columns.length)
                                errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(Arrays.toString(columns)).fieldName(CSVType.P.name()).errorDescription(messageSource.getMessage("MSG_200103", new Integer[]{CSVFieldP.values().length}, new Locale("en"))).build());
                            else {
                                if (!org.apache.commons.lang3.StringUtils.isAlphaSpace(columns[CSVFieldP.FIRST_NAME.ordinal()]) || columns[CSVFieldP.FIRST_NAME.ordinal()].length() < 2 || columns[CSVFieldP.FIRST_NAME.ordinal()].length() > 255) {
                                    errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(columns[CSVFieldP.FIRST_NAME.ordinal()]).fieldName(messageSource.getMessage("Field.firstName", null, new Locale("en"))).errorDescription(messageSource.getMessage("MSG_200308", null, new Locale("en"))).build());
                                }
                                if (!org.apache.commons.lang3.StringUtils.isAlphaSpace(columns[CSVFieldP.LAST_NAME.ordinal()]) || columns[CSVFieldP.LAST_NAME.ordinal()].length() < 2 || columns[CSVFieldP.LAST_NAME.ordinal()].length() > 255) {
                                    errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(columns[CSVFieldP.LAST_NAME.ordinal()]).fieldName(messageSource.getMessage("Field.lastName", null, new Locale("en"))).errorDescription(messageSource.getMessage("MSG_200308", null, new Locale("en"))).build());
                                }
                            }
                        } else if (CSVType.T.name().equals(columns[0])) {
                            if (CSVFieldT.values().length != columns.length)
                                errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(Arrays.toString(columns)).fieldName(CSVType.T.name()).errorDescription(messageSource.getMessage("MSG_200103", new Integer[]{CSVFieldP.values().length}, new Locale("en"))).build());
                            else {
                                if (!(Pattern.compile(CommonEnum.PhonePattern.MOBILE.getValue()).matcher(columns[CSVFieldT.MOBILE.ordinal()].trim()).matches() || Pattern.compile(CommonEnum.PhonePattern.PHONE.getValue()).matcher(columns[CSVFieldT.MOBILE.ordinal()].trim()).matches())) {
                                    errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(columns[CSVFieldT.MOBILE.ordinal()]).fieldName(messageSource.getMessage("Field.mobile", null, new Locale("en"))).errorDescription(messageSource.getMessage("MSG_200104", null, new Locale("en"))).build());
                                }
                            }
                        } else if (CSVType.A.name().equals(columns[0])) {
                            if (CSVFieldA.values().length != columns.length)
                                errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(Arrays.toString(columns)).fieldName(CSVType.P.name()).errorDescription(messageSource.getMessage("MSG_200103", new Integer[]{CSVFieldP.values().length}, new Locale("en"))).build());
                        } else {
                            if (CSVFieldF.values().length != columns.length)
                                errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(Arrays.toString(columns)).fieldName(CSVType.P.name()).errorDescription(messageSource.getMessage("MSG_200103", new Integer[]{CSVFieldP.values().length}, new Locale("en"))).build());
                            else {
                                if (!org.apache.commons.lang3.StringUtils.isAlphaSpace(columns[CSVFieldF.NAME.ordinal()]) || columns[CSVFieldF.NAME.ordinal()].length() < 2 || columns[CSVFieldF.NAME.ordinal()].length() > 255) {
                                    errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(columns[CSVFieldF.NAME.ordinal()]).fieldName(messageSource.getMessage("Field.name", null, new Locale("en"))).errorDescription(messageSource.getMessage("MSG_200308", null, new Locale("en"))).build());
                                }
                                if (!NumberUtils.isDigits(columns[CSVFieldF.YEAR.ordinal()]) || columns[CSVFieldF.YEAR.ordinal()].length() != 4) {
                                    errors.add(CSVLineErrorDTO.builder().lineNo(lineNo.get()).fileName(fileName).fieldValue(columns[CSVFieldF.YEAR.ordinal()]).fieldName(messageSource.getMessage("Field.year", null, new Locale("en"))).errorDescription(messageSource.getMessage("MSG_200105", new Integer[]{4}, new Locale("en"))).build());
                                }
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            log.error("MSG_200313", e);
            throw new FileConverterServiceUnavailableException(messageSource.getMessage("MSG_200313", new String[]{fileName}, new Locale("en")), e);
        }
        return errors;
    }

    private String makeErrorCSVText(List<CSVLineErrorDTO> errors) {
        return errors.isEmpty() ? null : Arrays.stream(CommonEnum.RequestFileCSVError.values()).map(CommonEnum.RequestFileCSVError::getValue).collect(Collectors.joining(CommonEnum.CSVDelimiter.PIPE.getValue())) + System.lineSeparator() + errors.stream().sorted(Comparator.comparing(CSVLineErrorDTO::getFileName).thenComparingInt(CSVLineErrorDTO::getLineNo)).map(CSVLineErrorDTO::toString).collect(Collectors.joining());
    }

}
