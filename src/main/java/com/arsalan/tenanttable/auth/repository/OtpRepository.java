package com.arsalan.tenanttable.auth.repository;

import com.arsalan.tenanttable.auth.enitity.Otp;
import com.arsalan.tenanttable.auth.enums.OtpPurpose;
import com.arsalan.tenanttable.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    //TODO: Fetch the most recent unused OTP for the given user and purpose
    Optional<Otp> findTopByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            User user,
            OtpPurpose purpose
    );

    @Query("""
            SELECT o
            FROM Otp o
            WHERE o.user = :user
              AND o.purpose = :purpose
              AND o.used = false
            """)
    List<Otp> findAllByUserAndPurposeAndUsedFalse(
            @Param("user") User user,
            @Param("purpose") OtpPurpose purpose
    );

    void deleteAllByExpiresAtBefore(Instant instant);
}
