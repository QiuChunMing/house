package com.example.house.service;

import com.example.house.dto.HouseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SimpleTest {
    @Test
    public void buildUrl() {
        UriComponents build = UriComponentsBuilder.fromHttpUrl("http://localhost:8080").queryParam("ak", "1")
                .queryParam("a", "b").build();
        System.out.println(build.toString());
    }

    @Test
    public void testDate() {
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        System.out.println(localDate.format(DateTimeFormatter.ISO_DATE));
        System.out.println(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime.getSecond());
        System.out.println(localDateTime.format(DateTimeFormatter.BASIC_ISO_DATE));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));

        System.out.println(new Date());
    }

    @Test
    public void testPassword() {
        System.out.println(new BCryptPasswordEncoder().encode("secret"));
    }


    @Test
    public void testDateFormat() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JsonTest jsonTest = new JsonTest();
        jsonTest.setLocalDateTime(LocalDateTime.now());
        String s = objectMapper.writeValueAsString(jsonTest);
        System.out.println(s);
    }

    @Data
    class JsonTest {
        @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
        private LocalDateTime localDateTime;
    }


    @Test
    public void testChar() {
        int a = 65;
        System.out.println('A'== 65);
    }
}
