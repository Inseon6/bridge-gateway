package com.hansolinticube.gateway.utility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonLoader {
    private static final Gson gson = new Gson();

    public static JsonObject getJsonObject(String jsonRelPath) throws FileNotFoundException {
        String iniAbsPath = new File(jsonRelPath).getAbsolutePath();
        return gson.fromJson(new JsonReader(new FileReader(iniAbsPath)), JsonObject.class);
    }
}
