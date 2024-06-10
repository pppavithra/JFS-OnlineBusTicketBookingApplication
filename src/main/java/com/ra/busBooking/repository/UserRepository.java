package com.ra.busBooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ra.busBooking.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	//In user email id can findbyEmail
	User findByEmail(String emailId);
}
