package com.jztchl.splitwiseclonejava.repos;

import com.jztchl.splitwiseclonejava.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<Users,Long> {
    Optional<Users> findByEmail(String email);
}
