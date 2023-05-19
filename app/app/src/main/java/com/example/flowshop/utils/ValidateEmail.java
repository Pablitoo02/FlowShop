package com.example.flowshop.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidateEmail {

    public static boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
