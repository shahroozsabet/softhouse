package com.softhouse.integration.util;

/**
 * Created by Shahrooz on 02/17/2022.
 */
public class CommonEnum {

    public enum RequestFileCSVError {
        FILE_NAME("File Name"), lINE_NO("Line Number"), FIELD_NAME("Field Name"), FIELD_VALUE("Field Value"), ERROR_DESCRIPTION("Error");

        private final String value;

        RequestFileCSVError(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CSVDelimiter {
        PIPE("|");

        private final String value;

        CSVDelimiter(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PhonePattern {
        MOBILE("\\d{4}-\\d{6}"), PHONE("\\d{2}-\\d{6}");

        private final String value;

        PhonePattern(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
