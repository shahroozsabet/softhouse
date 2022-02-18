package com.softhouse.integration.fileconverter.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@XmlRootElement
@Data
@XmlType
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {
    @XmlElement
    private String street;
    @XmlElement
    private String town;
    @XmlElement
    private String postalCode;
}
