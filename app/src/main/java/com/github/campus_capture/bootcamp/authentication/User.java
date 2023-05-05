package com.github.campus_capture.bootcamp.authentication;

public class User {

    private static String name;
    private static String uid = null;
    private static Section section;

    public static String getName(){
        return name;
    }

    public static void setName(String name){
        User.name = name;
    }

    public static String getUid(){
        return uid;
    }

    public static void setUid(String uid){
        User.uid = uid;
    }

    public static Section getSection() {
        return section;
    }

    public static void setSection(Section section) {
        User.section = section;
    }

}
