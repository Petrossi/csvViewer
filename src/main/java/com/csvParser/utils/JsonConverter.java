package com.csvParser.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Useful class to convert to and from Json In this example we use Google gson
 */
public class JsonConverter {

    private static final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .serializeSpecialFloatingPointValues().create()
    ;

    /**
     * This method deserializes the specified Json into an object of the
     * specified class.
     *
     */
    public static <T> T convertFromJson(String toConvert, Class<T> clazz) {

        return gson.fromJson(toConvert, clazz);
    }

    /**
     * This method deserializes the specified Json into an object of the
     * specified class.
     *
     */
    public static <T> T convertFromJson(String toConvert, Type typeOfT) {

        return gson.fromJson(toConvert, typeOfT);
    }

    /**
     * This method serializes the specified object into its equivalent Json
     * representation.
     */
    public static String convertToJson(Object toConvert) {

        return gson.toJson(toConvert);
    }
    /**
     * This method serializes the specified object into its equivalent Json
     * representation.
     */
    public static JSONObject convertToJsonObject(Object toConvert) {
        JSONObject r = new JSONObject();

        for (Field field : toConvert.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(toConvert);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            r.put(name, value);
        }

        return r;
    }
}