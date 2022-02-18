package com.softhouse.integration.fileconverter.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@Data
public class FileErrorDTO {
    private Set<String> generalErrors = new HashSet<>(0);
}
