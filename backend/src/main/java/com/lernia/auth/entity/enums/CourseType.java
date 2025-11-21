package com.lernia.auth.entity.enums;

public enum CourseType {

    BACHELOR, MASTER, DOCTORATE;

    public static boolean contains(String value) {
        try {
            CourseType.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
