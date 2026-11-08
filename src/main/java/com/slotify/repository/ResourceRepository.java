package com.slotify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slotify.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByStatus(String status);
}