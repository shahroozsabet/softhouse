package com.softhouse.integration.fileconverter.dto;

import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Created by Shahrooz on 02/17/2022.
 */
public enum ObjectToXml {

    INSTANCE;

    public Person prepareData(DataEntry dataEntry) {
        Person person = new Person();
        person.setFirstname(dataEntry.getFirstName());
        person.setLastname(dataEntry.getLastName());
        if (dataEntry.getPostalCode() != null) {
            Address address = new Address();
            address.setPostalCode(dataEntry.getPostalCode());
            address.setStreet(dataEntry.getStreet());
            address.setTown(dataEntry.getTown());
            person.setAddress(address);
        }
        if (dataEntry.getMobile() != null) {
            Phone phone = new Phone();
            phone.setLandPhone(dataEntry.getLandPhone());
            phone.setMobile(dataEntry.getMobile());
            person.setPhone(phone);
        }
        if (!dataEntry.getFamilies().isEmpty()) {
            person.setFamily(dataEntry.getFamilies());
        }
        return person;
    }

    public People createPeopleByEntries(List<DataEntry> dataEntries) {
        return new People(dataEntries.stream().map(ObjectToXml.INSTANCE::prepareData).toList());
    }

    public String convertToXML(People people) throws JAXBException {
        Marshaller marshallerObj = JAXBContext.newInstance(People.class).createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshallerObj.marshal(people, sw);
        return sw.toString();
    }

}
