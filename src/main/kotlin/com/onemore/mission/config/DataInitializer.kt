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
        if (userRepository.count() == 0L) {
            val admin = User(
                employeeCode = "ADMIN001",
                fullName = "System Admin",
                email = "admin@onemore.com",
                passwordHash = passwordEncoder.encode("admin123"),
                jobLevel = JobLevel.EXECUTIVE,
                functionName = "IT",
                business = "ONE MORE",
                roles = mutableSetOf(UserRole.ROLE_ADMIN, UserRole.ROLE_EXECUTIVE)
            )

            val employee = User(
                employeeCode = "EMP001",
                fullName = "John Doe",
                email = "employee@onemore.com",
                passwordHash = passwordEncoder.encode("emp123"),
                jobLevel = JobLevel.STAFF,
                functionName = "Sales",
                business = "ONE MORE",
                roles = mutableSetOf(UserRole.ROLE_STAFF)
            )

            userRepository.save(admin)
            userRepository.save(employee)

            println("===== Sample users created =====")
            println("Admin    → email: admin@onemore.com    | password: admin123")
            println("Employee → email: employee@onemore.com | password: emp123")
            println("================================")
        }
    }
}