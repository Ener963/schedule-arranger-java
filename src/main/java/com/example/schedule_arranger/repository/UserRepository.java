package com.example.schedule_arranger.repository;

import com.example.schedule_arranger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}