package com.bernacki.testtask.marketing_api.repository;

import com.bernacki.testtask.marketing_api.entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface TownRepository extends JpaRepository<Town, BigInteger> {
    Optional<Town> findByTownName(String townName);
}
