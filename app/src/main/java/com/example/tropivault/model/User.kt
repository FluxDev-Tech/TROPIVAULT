package com.example.tropivault.model

import com.example.tropivault.data.local.UserEntity

enum class UserRole {
    CLIENT,
    FARMER,
    RIDER,
    ADMIN;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.uppercase()) {
                "FARMER" -> FARMER
                "RIDER" -> RIDER
                "ADMIN" -> ADMIN
                else -> CLIENT
            }
        }
    }
}

enum class UserStatus {
    APPROVED,
    PENDING,
    REJECTED;

    companion object {
        fun fromString(status: String): UserStatus {
            return when (status.uppercase()) {
                "PENDING" -> PENDING
                "REJECTED" -> REJECTED
                else -> APPROVED
            }
        }
    }
}

/**
 * Domain User model supporting multi-role differentiation between
 * Client, Farmer, Rider, and Admin.
 */
data class User(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val phone: String = "",
    val address: String = "",
    val farmName: String? = null,
    val farmLocation: String? = null,
    val vehicleType: String? = null,
    val licenseNumber: String? = null,
    val status: UserStatus = UserStatus.APPROVED,
    val token: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Role Differentiation Accessors
    val isClient: Boolean get() = role == UserRole.CLIENT
    val isFarmer: Boolean get() = role == UserRole.FARMER
    val isRider: Boolean get() = role == UserRole.RIDER
    val isAdmin: Boolean get() = role == UserRole.ADMIN

    // Status Differentiation Accessors
    val isApproved: Boolean get() = status == UserStatus.APPROVED
    val isPending: Boolean get() = status == UserStatus.PENDING
    val isRejected: Boolean get() = status == UserStatus.REJECTED

    // Display title & descriptors
    val roleDisplayName: String
        get() = when (role) {
            UserRole.CLIENT -> "Client / Business Buyer"
            UserRole.FARMER -> "Verified Farmer Partner"
            UserRole.RIDER -> "Climate-Box Rider"
            UserRole.ADMIN -> "System Administrator"
        }

    fun toEntity(password: String = "password123"): UserEntity {
        return UserEntity(
            id = id,
            email = email,
            password = password,
            fullName = fullName,
            role = role.name,
            phone = phone,
            address = address,
            farmName = farmName ?: "",
            farmLocation = farmLocation ?: "",
            vehicleType = vehicleType ?: "",
            licenseNumber = licenseNumber ?: "",
            status = status.name,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromEntity(entity: UserEntity, token: String? = null): User {
            return User(
                id = entity.id,
                email = entity.email,
                fullName = entity.fullName,
                role = UserRole.fromString(entity.role),
                phone = entity.phone,
                address = entity.address,
                farmName = entity.farmName.ifBlank { null },
                farmLocation = entity.farmLocation.ifBlank { null },
                vehicleType = entity.vehicleType.ifBlank { null },
                licenseNumber = entity.licenseNumber.ifBlank { null },
                status = UserStatus.fromString(entity.status),
                token = token,
                createdAt = entity.createdAt
            )
        }
    }
}

/**
 * Session State hierarchy for reactive state management.
 */
sealed interface UserSessionState {
    data object Unauthenticated : UserSessionState
    data object Loading : UserSessionState
    data class Authenticated(val user: User) : UserSessionState
    data class PendingApproval(val user: User, val message: String) : UserSessionState
    data class Error(val message: String) : UserSessionState

    val currentUser: User?
        get() = when (this) {
            is Authenticated -> user
            is PendingApproval -> user
            else -> null
        }

    val isAuthenticated: Boolean
        get() = this is Authenticated

    val activeRole: UserRole?
        get() = currentUser?.role
}
