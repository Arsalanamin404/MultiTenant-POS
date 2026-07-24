package com.arsalan.tenanttable.settings.mapper;

import com.arsalan.tenanttable.settings.dto.SettingsResponseDto;
import com.arsalan.tenanttable.settings.entity.Settings;

public final class SettingsMapper {
    private SettingsMapper() {
    }

    public static SettingsResponseDto toDto(Settings settings) {
        return SettingsResponseDto.builder()
                .id(settings.getId())
                .businessName(settings.getBusinessName())
                .phoneNumber(settings.getPhoneNumber())
                .email(settings.getEmail())
                .website(settings.getWebsite())
                .address(settings.getAddress())
                .city(settings.getCity())
                .state(settings.getState())
                .country(settings.getCountry())
                .postalCode(settings.getPostalCode())
                .currency(settings.getCurrency())
                .timezone(settings.getTimezone())
                .taxRate(settings.getTaxRate())
                .gstNumber(settings.getGstNumber())
                .invoicePrefix(settings.getInvoicePrefix())
                .receiptFooter(settings.getReceiptFooter())
                .logoUrl(settings.getLogoUrl())
                .build();
    }
}
