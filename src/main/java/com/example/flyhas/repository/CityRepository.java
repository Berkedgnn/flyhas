package com.example.flyhas.repository;

import com.example.flyhas.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
