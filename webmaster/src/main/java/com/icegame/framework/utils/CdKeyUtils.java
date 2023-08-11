package com.icegame.framework.utils;

import java.util.Random;

public class CdKeyUtils {

    private static Random strGen = new Random();;
    private static Random numGen = new Random();;
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();;
    private static char[] numbers = ("0123456789").toCharArray();;
    /** * 产生随机字符串 * */
    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(61)];
        }
        return new String(randBuffer);
    }

    /** * 产生随机数值字符串 * */
    public static final String randomNumStr(int length) {
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbers[numGen.nextInt(9)];
        }
        return new String(randBuffer);
    }

    public static String randomKey(int min, int max) {

        /*String[] baseString={"0","1","2","3",
                "4","5","6","7","8","9",
                "a","b","c","d","e",
                "f","g","h","i","j",
                "k","l","m","n","o",
                "p","q","r","s","t",
                "u","v","w","x","y",
                "z","A","B","C","D",
                "E","F","G","H","I",
                "J","K","L","M","N",
                "O","P","Q","R","S",
                "T","U","V","W","X","Y","Z"};*/

        String[] baseString={"0","1","2","3",
                "4","5","6","7","8","9",
                "a","b","c","d","e",
                "f","g","h","i","j",
                "k","m","n",
                "p","q","r","s","t",
                "u","v","w","x","y",
                "z","A","B","C","D",
                "E","F","G","H",
                "J","K","L","M","N",
                "P","Q","R","S",
                "T","U","V","W","X","Y","Z"};


        int len = new Random().nextInt(max) % (max - min + 1) + min;

        Random random = new Random();
        int length=baseString.length;
        String randomString="";
        for(int i=0;i<length;i++){
            randomString += baseString[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr="";
        for (int i = 0; i < len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length()-1));
        }

        return resultStr.toUpperCase();
    }

    public static String randomPwd(int min, int max) {

        String[] baseString={"0","1","2","3",
                "4","5","6","7","8","9",
                "a","b","c","d","e",
                "f","g","h","i","j",
                "k","m","n",
                "p","q","r","s","t",
                "u","v","w","x","y",
                "z","A","B","C","D",
                "E","F","G","H",
                "J","K","L","M","N",
                "P","Q","R","S",
                "T","U","V","W","X","Y","Z"};


        int len = new Random().nextInt(max) % (max - min + 1) + min;

        Random random = new Random();
        int length=baseString.length;
        String randomString="";
        for(int i=0;i<length;i++){
            randomString += baseString[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr="";
        for (int i = 0; i < len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length()-1));
        }

        return resultStr;
    }

    public static String validateCdKey(String cdKey){
        char[] chs = cdKey.toCharArray();
        int BigCount = 0;
        int SmallCount = 0;
        int NumberCount = 0;

        for (int x = 0; x < chs.length; x++) {
            // 判断该字符是
            if (Character.isUpperCase(chs[x])) {
                BigCount++;
            } else if (Character.isDigit(chs[x])) {
                NumberCount++;
            }
        }

        if(BigCount > 7 || BigCount < 3 || NumberCount > 7 || NumberCount < 3){
            cdKey = validateCdKey(randomKey(8,10));
        }
        return cdKey;
    }

    public static String randomKeyList(int number, int diffType){
        /*JSONArray array = new JSONArray();

        Object size = 0;
        switch (diffType){
            case 0 : size = "无限使用";break;
            case 1 : size = 1 ;break;
            case 2 : size = 1 ;break;
        }*/
        StringBuffer keyList = new StringBuffer();
        for(int i = 0 ; i < number ; i++){
            String randamKey = validateCdKey(randomKey(8,10));
            keyList.append(randamKey).append(",");
        }
        return keyList.toString();
    }

    public static void main(String[] args){
        System.out.println(randomKeyList(8,0));
    }
}
