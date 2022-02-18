package com.softhouse.integration.fileconverter.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Shahrooz on 02/17/2022.
 */
@XmlRootElement
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class People {

    @XmlElement
    private List<Person> person = new ArrayList<>();

    public People(List<Person> person) {
        this.person = person;
    }

}
