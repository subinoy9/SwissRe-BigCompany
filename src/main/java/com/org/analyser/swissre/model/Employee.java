package com.org.analyser.swissre.model;

import java.math.BigDecimal;

public class Employee {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final BigDecimal salary;
    private final String managerId;

    public Employee(String id, String firstName, String lastName, BigDecimal salary, String managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId == null || managerId.isBlank() ? null : managerId;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public BigDecimal getSalary() { return salary; }
    public String getManagerId() { return managerId; }

    @Override
    public String toString() {
        return id + " " + firstName + " " + lastName;
    }
}
