package com.rebalance.dto.common;

public class ValidationConst {
    public final static String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"; // RFC 5322
    public final static String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–_[{}]:;',?/*~$^+=<>]).{8,255}$";
    public final static String currencyRegex = "^[A-Z]{3}$"; // ISO 4217
}
