package com.onemore.mission.user.repository

import com.onemore.mission.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): Optional<User>

    fun findByEmployeeCode(employeeCode: String): Optional<User>

    fun existsByEmail(email: String): Boolean

    fun existsByEmployeeCode(employeeCode: String): Boolean

    @Query(
        """
        SELECT u FROM User u
        WHERE (:search IS NULL OR :search = ''
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.functionName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.business) LIKE LOWER(CONCAT('%', :search, '%')))
        """
    )
    fun search(@Param("search") search: String?, pageable: Pageable): Page<User>
}
