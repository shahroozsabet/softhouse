package com.softhouse.integration.fileconverter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class FileConverterControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void convertFileException() throws Exception {
        String text = "some text";
        MockMultipartFile file = new MockMultipartFile("file", "filename.csv", MediaType.TEXT_PLAIN_VALUE, text.getBytes());
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/convertFile").file(file)).andExpect(status().is4xxClientError());
    }

    @Test
    void convertFileException2() throws Exception {
        String text = """
                P|Carl Gustaf             |Bernadotte
                T|0768-101801             |08101801
                A|Drottningholms slott    |Stockholm      |10001
                F|Victoria                |1977
                A|Haga Slott              |Stockholm      |10002
                F|Carl Philip             |1979
                T|0768-101802             |08-101802
                P|Barack                  |Obama
                A|1600 Pennsylvania Avenue|Washington, D.C""";
        MockMultipartFile file = new MockMultipartFile("file", "filename.csv", MediaType.TEXT_PLAIN_VALUE, text.getBytes());
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/convertFile").file(file)).andExpect(status().is4xxClientError());
    }

    @Test
    void convertFile() throws Exception {
        String text = """
                P|Carl Gustaf             |Bernadotte
                T|0768-101801             |08101801
                A|Drottningholms slott    |Stockholm      |10001
                F|Victoria                |1977
                A|Haga Slott              |Stockholm      |10002
                F|Carl Philip             |1979
                T|0768-101802             |08-101802
                P|Barack                  |Obama
                A|1600 Pennsylvania Avenue|Washington, D.C|10003""";
        MockMultipartFile file = new MockMultipartFile("file", "filename.csv", MediaType.TEXT_PLAIN_VALUE, text.getBytes());
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/convertFile").file(file)).andExpect(status().isCreated());
    }
}