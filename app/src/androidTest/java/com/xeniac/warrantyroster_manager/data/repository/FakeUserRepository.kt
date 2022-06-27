package com.xeniac.warrantyroster_manager.data.repository

import com.xeniac.warrantyroster_manager.data.TestUser
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository

class FakeUserRepository : UserRepository {

    private val users = mutableListOf<TestUser>()

    private var shouldReturnNetworkError = false

    fun addUser(email: String, password: String) {
        users.add(TestUser(email, password))
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun registerViaEmail(email: String, password: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            for (u in users) {
                if (u.email == email) {
                    throw Exception()
                }
            }
            users.add(TestUser(email, password))
        }
    }

    override suspend fun loginViaEmail(email: String, password: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            if (!users.contains(TestUser(email, password))) {
                throw Exception()
            }
        }
    }

    override suspend fun sendResetPasswordEmail(email: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            if (users[0].email != email) {
                throw Exception("email not found")
            }
        }
    }

    override fun getCurrentUser(): TestUser = users[0]

    override fun getCurrentUserEmail(): String = getCurrentUser().email

    override suspend fun sendVerificationEmail() {
        if (shouldReturnNetworkError) {
            throw Exception()
        }
    }

    override suspend fun reloadCurrentUser() {
        /* NO-OP */
    }

    override fun logoutUser() {
        /* NO-OP */
    }

    override suspend fun reAuthenticateUser(password: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            if (users[0].password != password) {
                throw Exception()
            }
        }
    }

    override suspend fun updateUserEmail(newEmail: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            users[0].email = newEmail
        }
    }

    override suspend fun updateUserPassword(newPassword: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            users[0].password = newPassword
        }
    }
}