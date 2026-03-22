package com.itticket.repository;

import com.itticket.entity.Sla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlaRepository extends JpaRepository<Sla, Long> {
    List<Sla> findByIsActiveTrue();
    Optional<Sla> findByName(String name);
}
