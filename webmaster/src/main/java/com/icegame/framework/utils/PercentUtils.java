package com.icegame.framework.utils;

import java.text.DecimalFormat;

public class PercentUtils {
	public static String turnPercent(float count,float total) {
		DecimalFormat df = new DecimalFormat("##.00%");
		return df.format(count/total);
	}
}
