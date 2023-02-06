package com.tmobile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Calculator implements TelephoneBillCalculator {

    private static final BigDecimal DAY_MINUTE_RATE = new BigDecimal("1.0");
    private static final BigDecimal REDUCED_MINUTE_RATE = new BigDecimal("0.5");
    private static final BigDecimal PROMOTION_MINUTE_RATE = new BigDecimal("0.2");

    private static final int PROMOTION_DURATION = 5;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final String DAY_PERIOD_START = "08:00:00";
    private static final String DAY_PERIOD_END = "16:00:00";

    @Override
    public BigDecimal calculate(String phoneLog) {
        List<Call> calls = parsePhoneLog(phoneLog);
        Map<String, Integer> phoneNumberFrequencies = getPhoneNumberFrequencies(calls);
        String mostFrequentPhoneNumber = getMostFrequentPhoneNumber(phoneNumberFrequencies);
        BigDecimal bill = new BigDecimal(0);
        for (Call call : calls) {
            if (!call.getPhoneNumber().equals(mostFrequentPhoneNumber)) {
                bill = bill.add(getCallCharge(call));
            }
        }
        return bill;
    }
    private List<Call> parsePhoneLog(String phoneLog) {
        List<Call> calls = new ArrayList<>();
        String[] callStrings = phoneLog.split("\n");
        for (String callString : callStrings) {
            String[] callDetails = callString.split(",");
            String phoneNumber = callDetails[0];
            try {
                Date start = DATE_FORMAT.parse(callDetails[1]);
                Date end = DATE_FORMAT.parse(callDetails[2]);
                calls.add(new Call(phoneNumber, start, end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return calls;
    }
    private Map<String, Integer> getPhoneNumberFrequencies(List<Call> calls) {
        Map<String, Integer> phoneNumberFrequencies = new HashMap<>();
        for (Call call : calls) {
            String phoneNumber = call.getPhoneNumber();
            if (phoneNumberFrequencies.containsKey(phoneNumber)) {
                phoneNumberFrequencies.put(phoneNumber, phoneNumberFrequencies.get(phoneNumber) + 1);
            } else {
                phoneNumberFrequencies.put(phoneNumber, 1);
            }
        }
        return phoneNumberFrequencies;
    }
    private String getMostFrequentPhoneNumber(Map<String, Integer> phoneNumberFrequencies) {
        List<Map.Entry<String, Integer>> phoneNumbers = new ArrayList<>(phoneNumberFrequencies.entrySet());
        phoneNumbers.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        if (phoneNumbers.get(0).getValue() > 1) {
            return phoneNumbers.get(0).getKey();
        } else {
            long highestNumber = Long.MIN_VALUE;
            String highestPhoneNumber = "";
            for (Map.Entry<String, Integer> entry : phoneNumbers) {
                long phoneNumber = Long.parseLong(entry.getKey());
                if (phoneNumber > highestNumber) {
                    highestNumber = phoneNumber;
                    highestPhoneNumber = entry.getKey();
                }
            }
            return highestPhoneNumber;
        }
    }
    private BigDecimal getCallCharge(Call call) {
        BigDecimal callCharge = new BigDecimal(0);
        long duration = (call.getEnd().getTime() - call.getStart().getTime()) / 1000 / 60;
        if (duration <= PROMOTION_DURATION) {
            callCharge = callCharge.add(DAY_MINUTE_RATE.multiply(new BigDecimal(duration)));
        } else {
            callCharge = callCharge.add(DAY_MINUTE_RATE.multiply(new BigDecimal(PROMOTION_DURATION)));
            callCharge = callCharge.add(PROMOTION_MINUTE_RATE.multiply(new BigDecimal(duration - PROMOTION_DURATION)));
        }
        try {
            Date start = DATE_FORMAT.parse(DATE_FORMAT.format(call.getStart()).substring(0, 11) + DAY_PERIOD_START);
            Date end = DATE_FORMAT.parse(DATE_FORMAT.format(call.getStart()).substring(0, 11) + DAY_PERIOD_END);
            if (call.getStart().before(start) || call.getStart().after(end)) {
                callCharge = callCharge.multiply(REDUCED_MINUTE_RATE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return callCharge;
    }
}