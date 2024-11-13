package io.nugulticket.otp.repository;

import io.nugulticket.otp.entity.OtpKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpKeyRepository extends JpaRepository<OtpKey, Long> {

}