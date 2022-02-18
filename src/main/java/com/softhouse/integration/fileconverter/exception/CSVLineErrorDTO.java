package com.softhouse.integration.fileconverter.exception;

import com.softhouse.integration.util.CommonEnum;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@Data
@Builder
public class CSVLineErrorDTO {
    private String fileName;
    private Integer lineNo;
    private String fieldName;
    private String fieldValue;
    private String errorDescription;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(fileName).append(CommonEnum.CSVDelimiter.PIPE.getValue());
        sb.append(lineNo).append(CommonEnum.CSVDelimiter.PIPE.getValue());
        sb.append(fieldName).append(CommonEnum.CSVDelimiter.PIPE.getValue());
        sb.append(fieldValue).append(CommonEnum.CSVDelimiter.PIPE.getValue());
        sb.append(errorDescription).append(System.getProperty("line.separator"));
        return sb.toString();
    }
}
