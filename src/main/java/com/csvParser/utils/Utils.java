package com.csvParser.utils;

import org.apache.http.HttpStatus;

public class Utils {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static boolean isRedirected(int statusCode) {
        return statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
            statusCode == HttpStatus.SC_MOVED_TEMPORARILY ||
            statusCode == HttpStatus.SC_MULTIPLE_CHOICES ||
            statusCode == HttpStatus.SC_SEE_OTHER ||
            statusCode == HttpStatus.SC_TEMPORARY_REDIRECT ||
            statusCode == 308
        ;
    }

    public static boolean isValidContentType(String contentType){
        return contentType.contains("html") || contentType.toLowerCase().equals("text/plain") || contentType.toLowerCase().equals("application/json") ;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();

    }
}