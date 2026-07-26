package com.arsalan.tenanttable.receipt.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BusinessInfoDto {
    private String businessName;

    private String address;

    private String phone;

    private String email;

    private String gstNumber;
}
