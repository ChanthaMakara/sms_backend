package com.onemore.mission.user.repository

import com.onemore.mission.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): Optional<User>

    fun findByEmployeeCode(employeeCode: String): Optional<User>

    fun existsByEmail(email: String): Boolean

    fun existsByEmployeeCode(employeeCode: String): Boolean
}