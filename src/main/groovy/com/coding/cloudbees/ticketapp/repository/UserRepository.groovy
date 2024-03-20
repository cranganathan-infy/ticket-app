package com.coding.cloudbees.ticketapp.repository

import com.coding.cloudbees.ticketapp.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email)
}

