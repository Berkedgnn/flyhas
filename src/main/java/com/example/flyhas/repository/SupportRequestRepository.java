package com.example.flyhas.repository;

import com.example.flyhas.model.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByCustomerEmail(String email);
}
