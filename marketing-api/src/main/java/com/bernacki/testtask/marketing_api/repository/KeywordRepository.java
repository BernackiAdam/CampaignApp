package com.bernacki.testtask.marketing_api.repository;

import com.bernacki.testtask.marketing_api.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, BigInteger> {
}
