package com.application.project.classroom.module;

import java.util.Random;

public class Const {
    public final static String ACCOUNT = "account";
    public final static String AVATAR = "avatar";
    public final static String COURSE = "course";
    public final static String TEACHER = "teacher";
    public final static String STUDENT = "student";

    public final static String[] chars = {"1","2","3","4","5","6","7","8","9","0","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};


    public static String getCourseID(){
        String result = "";
        int length = 7 + new Random().nextInt(5);
        int count = 0;
        while (count<=length){
            int random = new Random().nextInt(chars.length);
            result += chars[random];
            count++;
        }
        return result;
    }
}
