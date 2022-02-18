package com.softhouse.integration.fileconverter.dto;

import java.util.Stack;

import lombok.Data;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@Data
public class DataEntry {
    private String firstName;
    private String lastName;
    private String street;
    private String town;
    private String postalCode;
    private String mobile;
    private String landPhone;
    private Stack<Family> families = new Stack<>();
}
