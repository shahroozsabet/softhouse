package com.softhouse.integration.fileconverter.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ObjectToXmlTest {

    @Test
    void prepareData() {
        DataEntry dataEntry = getDataEntry();
        Person person = ObjectToXml.INSTANCE.prepareData(dataEntry);

        Assertions.assertEquals("Carl", person.getFirstname());
    }

    private DataEntry getDataEntry() {
        DataEntry dataEntry = new DataEntry();
        dataEntry.setFirstName("Carl");
        dataEntry.setLastName("Gustaf");
        dataEntry.setStreet("Wernskoldsgatan");
        dataEntry.setTown("Kalmar");
        dataEntry.setPostalCode("39249");
        dataEntry.setMobile("0768-101802");
        dataEntry.setLandPhone("08-101802");
        Family family = new Family();
        family.setName("Carl");
        family.setBorn("Vaxjo");
        Phone phone = new Phone();
        phone.setLandPhone("08-101803");
        phone.setMobile("0768-101801");
        family.setPhone(phone);
        Address address = new Address();
        address.setPostalCode("35257");
        address.setStreet("Serafimervagen");
        address.setPostalCode("35257");
        family.setAddress(address);
        Stack<Family> familyStack = new Stack<>();
        familyStack.push(family);
        dataEntry.setFamilies(familyStack);
        return dataEntry;
    }

    @Test
    void createPeopleByEntries() {
        DataEntry dataEntry = getDataEntry();
        List<DataEntry> dataEntries = new ArrayList<>();
        dataEntries.add(dataEntry);
        People peopleByEntries = ObjectToXml.INSTANCE.createPeopleByEntries(dataEntries);
        List<Person> persons = peopleByEntries.getPerson();

        Assertions.assertEquals(1, persons.size());
        Assertions.assertEquals("Carl", persons.get(0).getFirstname());
    }

    @Test
    void convertToXML() throws JAXBException {
        Person person = getPerson();
        List<Person> persons = new ArrayList<>();
        persons.add(person);
        People people = new People(persons);
        String xml = ObjectToXml.INSTANCE.convertToXML(people);

        Assertions.assertEquals("<people>", xml.substring(0, 8));
    }

    private Person getPerson() {
        Family family = new Family();
        family.setName("Carl");
        family.setBorn("Vaxjo");
        Phone phone = new Phone();
        phone.setLandPhone("08-101803");
        phone.setMobile("0768-101801");
        family.setPhone(phone);
        Address address = new Address();
        address.setPostalCode("35257");
        address.setStreet("Serafimervagen");
        address.setPostalCode("35257");
        family.setAddress(address);
        Stack<Family> familyStack = new Stack<>();
        familyStack.push(family);
        Person person = new Person();
        person.setFirstname("Barak");
        person.setLastname("Obama");
        person.setFamily(familyStack);
        person.setAddress(address);
        person.setPhone(phone);
        return person;
    }
}