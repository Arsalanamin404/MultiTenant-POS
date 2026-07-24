package com.arsalan.tenanttable.settings.dto;

import com.arsalan.tenanttable.settings.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class UpdateSettingsRequestDto {

    @Size(max = 150, message = "Business name must not exceed 150 characters")
    private String businessName;

    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Enter a valid phone number"
    )
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @Pattern(
            regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]{2,}(/\\S*)?$",
            message = "Invalid website URL"
    )
    private String website;

    @Size(max = 250, message = "Address must not exceed 250 characters")
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    private String postalCode;

    private Currency currency;

    private String timezone;
    
    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Tax rate must have at most 2 decimal places")
    private BigDecimal taxRate;

    // Indian GST format: 2 digit state code + 10 char PAN + 1 entity code + Z + 1 checksum
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST number format"
    )
    private String gstNumber;

    @Size(max = 10, message = "Invoice prefix must not exceed 10 characters")
    @Pattern(regexp = "^[A-Za-z0-9-]*$", message = "Invoice prefix can only contain letters, numbers, and hyphens")
    private String invoicePrefix;

    @Size(max = 500, message = "Receipt footer must not exceed 500 characters")
    private String receiptFooter;

    @Pattern(
            regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]{2,}(/\\S*)?$",
            message = "Invalid logo URL"
    )
    private String logoUrl;
}