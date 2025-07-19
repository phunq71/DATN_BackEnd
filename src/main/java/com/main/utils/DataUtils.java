package com.main.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.ColorOption;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataUtils {
    public static List<ColorOption> getListColors() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = DataUtils.class.getResourceAsStream("/static/data/Color.json")) {
            return Arrays.asList(mapper.readValue(inputStream, ColorOption[].class));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



}
