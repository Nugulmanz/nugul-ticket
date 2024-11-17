package io.nugulticket.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;
}