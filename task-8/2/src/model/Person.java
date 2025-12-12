package model;

import java.io.Serializable;

public class Person implements Serializable {
    protected String fullName;
    protected int age;

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }
}
