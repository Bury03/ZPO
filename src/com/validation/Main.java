package com.validation;

import com.validation.exception.ValidationException;
import com.validation.validator.Validator;

public class Main {

    public static void main(String[] args) {
        test(new Student());
        test(new Student("Al", "K", "123", "zly_mail"));
        test(new Student("Jan", "Kowalski", "12345678", "jan.kowalski@pbs.edu.pl"));
    }

    private static void test(Student student) {
        try {
            Validator.validate(student);
            System.out.println("Walidacja poprawna");
        } catch (ValidationException e) {
            System.out.println("Błędy walidacji:");
            System.out.println(e.getMessage());
        }

        System.out.println("--------------------");
    }
}
