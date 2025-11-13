package com.example.PatasyColas.repository;

import com.example.PatasyColas.model.VaccineRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineRecordRepository extends JpaRepository<VaccineRecord, Integer> {
}