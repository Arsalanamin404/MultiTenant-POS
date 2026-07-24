package com.arsalan.tenanttable.settings.entity;

import com.arsalan.tenanttable.settings.dto.UpdateSettingsRequestDto;
import com.arsalan.tenanttable.settings.enums.Currency;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "settings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "tenant_id")
        }
)
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String website;

    @Column(length = 250)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(length = 20)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Column(nullable = false, precision = 5, scale = 2)
    private java.math.BigDecimal taxRate;

    @Column(length = 30)
    private String gstNumber;

    @Column(nullable = false, length = 10)
    private String invoicePrefix;

    @Column(length = 500)
    private String receiptFooter;

    @Column(length = 220)
    private String logoUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;


    public void update(UpdateSettingsRequestDto dto) {

        if (dto.getBusinessName() != null)
            this.businessName = dto.getBusinessName();

        if (dto.getEmail() != null)
            this.email = dto.getEmail();

        if (dto.getPhoneNumber() != null)
            this.phoneNumber = dto.getPhoneNumber();

        if (dto.getWebsite() != null)
            this.website = dto.getWebsite();

        if (dto.getAddress() != null)
            this.address = dto.getAddress();

        if (dto.getCity() != null)
            this.city = dto.getCity();

        if (dto.getState() != null)
            this.state = dto.getState();

        if (dto.getCountry() != null)
            this.country = dto.getCountry();

        if (dto.getPostalCode() != null)
            this.postalCode = dto.getPostalCode();

        if (dto.getCurrency() != null)
            this.currency = dto.getCurrency();

        if (dto.getTimezone() != null)
            this.timezone = dto.getTimezone();

        if (dto.getTaxRate() != null)
            this.taxRate = dto.getTaxRate();

        if (dto.getGstNumber() != null)
            this.gstNumber = dto.getGstNumber();

        if (dto.getInvoicePrefix() != null)
            this.invoicePrefix = dto.getInvoicePrefix();

        if (dto.getReceiptFooter() != null)
            this.receiptFooter = dto.getReceiptFooter();

        if (dto.getLogoUrl() != null)
            this.logoUrl = dto.getLogoUrl();
    }
}
