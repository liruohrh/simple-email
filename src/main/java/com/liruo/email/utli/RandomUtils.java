package com.liruo.email.utli;

/**
 * @Author:liruo
 * @Date:2023-02-15-21:18:34
 * @Desc
 */
public class RandomUtils {

    /**
     * such as verification code
     * @param len
     * @return
     */
    public static String letterAndNumber(int len){
        StringBuilder stringBuilder = new StringBuilder();
        int random;
        for (int i = 0; i < len; i++) {
            random = random(0, 35);
            if(random > 9){
                stringBuilder.append((char) ('a' + (random - 10)));
            }else{
                stringBuilder.append((char) ('0' + random));
            }
        }
        return stringBuilder.toString();
    }
    public static int random(int min, int max){
        return min + (int)(Math.random() * (max + 1 - min));
    }
}
