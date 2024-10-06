package org.tbank.fintech.exchange_rates_api.model;

public record Currency (
        String charCode,
        Double unitRate
) {
}
