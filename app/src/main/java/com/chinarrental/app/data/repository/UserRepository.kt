package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.UserDao
import com.chinarrental.app.data.model.User
import com.chinarrental.app.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    fun getUserById(id: Long): Flow<User?> = userDao.getUserById(id)

    fun getUserByUsername(username: String): Flow<User?> = userDao.getUserByUsername(username)

    fun getUsersByRole(role: UserRole): Flow<List<User>> = userDao.getUsersByRole(role)

    fun getActiveUsers(): Flow<List<User>> = userDao.getActiveUsers()

    suspend fun insertUser(user: User): Result<Long> {
        return try {
            val id = userDao.insertUser(user)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(user: User): Result<Unit> {
        return try {
            userDao.deleteUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun authenticate(username: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByUsername(username)
            // Note: In production, use proper password hashing (BCrypt, etc.)
            // This is simplified for demonstration
            user.collect { foundUser ->
                if (foundUser != null && foundUser.password == password && foundUser.isActive) {
                    return@collect Result.success(foundUser)
                }
            }
            Result.failure(Exception("Invalid credentials or inactive user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
