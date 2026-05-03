package com.java.springmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.springmvc.domain.CartDetail;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

}
