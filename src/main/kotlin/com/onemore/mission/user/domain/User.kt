package com.onemore.mission.user.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "employee_code", nullable = false, unique = true, length = 50)
    var employeeCode: String,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String,

    @Column(nullable = false, unique = true, length = 150)
    var email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", nullable = false, length = 50)
    var jobLevel: JobLevel,

    @Column(name = "function_name", length = 100)
    var functionName: String? = null,

    @Column(length = 100)
    var business: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<UserRole> = mutableSetOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)