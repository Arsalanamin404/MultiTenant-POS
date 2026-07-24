package com.arsalan.tenanttable.settings.dto;

import com.arsalan.tenanttable.settings.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class SettingsResponseDto {
    private UUID id;
    private String businessName;
    private String phoneNumber;
    private String email;
    private String website;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Currency currency;
    private String timezone;
    private BigDecimal taxRate;
    private String gstNumber;
    private String invoicePrefix;
    private String receiptFooter;
    private String logoUrl;
}
