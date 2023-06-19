package com.devsecops;

public class Course {
    String courseName;
    Course (){
        Course c = new Course();
        c.courseName="Oracle";
    }

    public static void main(String[] args) {
        Course c = new Course();
        c.courseName="Java";
        System.out.println(c.courseName);
    }

}

