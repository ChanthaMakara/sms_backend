package com.onemore.mission.config

import com.onemore.mission.user.domain.JobLevel
import com.onemore.mission.user.domain.User
import com.onemore.mission.user.domain.UserRole
import com.onemore.mission.user.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (!userRepository.existsByEmail("admin@onemore.com")) {
            userRepository.save(
                User(
                    employeeCode = "ADMIN001",
                    fullName = "System Admin",
                    email = "admin@onemore.com",
                    passwordHash = passwordEncoder.encode("admin123"),
                    jobLevel = JobLevel.EXECUTIVE,
                    functionName = "IT",
                    business = "ONE MORE",
                    roles = mutableSetOf(UserRole.ROLE_ADMIN, UserRole.ROLE_EXECUTIVE)
                )
            )
            println("Admin created → admin@onemore.com | admin123")
        }

        if (!userRepository.existsByEmail("employee@onemore.com")) {
            userRepository.save(
                User(
                    employeeCode = "EMP001",
                    fullName = "John Doe",
                    email = "employee@onemore.com",
                    passwordHash = passwordEncoder.encode("emp123"),
                    jobLevel = JobLevel.STAFF,
                    functionName = "Sales",
                    business = "ONE MORE",
                    roles = mutableSetOf(UserRole.ROLE_STAFF)
                )
            )
            println("Employee created → employee@onemore.com | emp123")
        }
    }
}