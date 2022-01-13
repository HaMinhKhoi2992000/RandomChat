/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author HP
 */
public class ValidateUtil {
    public static String isValidName(String name, boolean whitespace) {
        name = RemoveAccentUtil.removeAccent(name);
       
        name = name.trim();
        if (name.isEmpty()) return " không được để trống";
        
        String space = whitespace ? "\\s" : "";

        String regex = "[^A-Za-z" + space + "]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        boolean result = matcher.find();
        if(result) {
            String extraMessage = whitespace ? "" : ", khoảng trắng";
            return " không bao gồm số, ký tự đặc biệt" + extraMessage;
        }
        return "";
    }

}
