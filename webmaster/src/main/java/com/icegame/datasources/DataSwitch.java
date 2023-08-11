package com.icegame.datasources;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface DataSwitch {
	String dataSource() default "";
}