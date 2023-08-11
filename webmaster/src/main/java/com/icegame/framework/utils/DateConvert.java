package com.icegame.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.expression.ParseException;

public class DateConvert implements Converter<String, Date> {

    public Date convert(String stringDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            try {
				return simpleDateFormat.parse(stringDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
