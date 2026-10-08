package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.local.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

class AuthRepository(private val userDao: UserDao) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun register(name: String, email: String, password: String, userType: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || !cleanEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))
        }
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(IllegalStateException("An account with this email already exists."))
        }

        val passwordHash = hashPassword(password)
        val newUser = User(
            name = name.trim().ifEmpty { "User" },
            email = cleanEmail,
            password = passwordHash,
            userType = userType
        )
        val id = userDao.insertUser(newUser)
        val savedUser = newUser.copy(id = id)
        _currentUser.value = savedUser
        return Result.success(savedUser)
    }

    suspend fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
            ?: return Result.failure(IllegalArgumentException("No account found with this email."))

        val passwordHash = hashPassword(password)
        if (user.passwordHash != passwordHash) {
            return Result.failure(IllegalArgumentException("Incorrect password."))
        }

        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun switchUserType(newType: String) {
        val current = _currentUser.value ?: return
        val updated = current.copy(userType = newType)
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    companion object {
        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
