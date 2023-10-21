package com.Templatization.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Templatization.dashboard.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
