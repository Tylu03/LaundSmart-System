package com.c1se22.publiclaundsmartsystem.util;

import lombok.Getter;

import java.math.BigDecimal;

public class AppConstants {
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIR = "asc";
    public static final Integer TIME_TO_CANCEL_RESERVATION = 15;
    public static final Double TIME_TO_NOTIFY_USER = 0.7;
    public static final Integer MAX_MONTHLY_CANCEL_RESERVATION = 3;
    public static final Integer MAX_BAN_BEFORE_DELETE = 3;
    public static final Integer[] BAN_DURATION = {1, 7, 14};
    public static final BigDecimal MINIMUM_WITHDRAW_AMOUNT = BigDecimal.valueOf(1000000);
    public static final Double SHARING_REVENUE = 0.1;
    public static final Integer WITHDRAW_DURATION = 30;
}
