package com.xeniac.warrantyroster_manager.repositories

import com.xeniac.warrantyroster_manager.data.TestUser

class FakeUserRepository : UserRepository {

    private val users = mutableListOf<TestUser>()

    private var shouldReturnNetworkError = false

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

    override suspend fun sendVerificationEmail() {
        /* NO-OP */
    }

    override suspend fun reloadCurrentUser() {

    }

    override fun logoutUser() {

    }

    override suspend fun reAuthenticateUser(password: String) {

    }

    override suspend fun updateUserEmail(newEmail: String) {

    }

    override suspend fun updateUserPassword(newPassword: String) {

    }
}