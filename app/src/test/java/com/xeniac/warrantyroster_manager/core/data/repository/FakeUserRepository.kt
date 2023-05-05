package com.xeniac.warrantyroster_manager.core.data.repository

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.xeniac.warrantyroster_manager.core.data.dto.TestUserDto
import com.xeniac.warrantyroster_manager.core.data.mapper.toTestUser
import com.xeniac.warrantyroster_manager.core.domain.model.TestUser
import com.xeniac.warrantyroster_manager.core.domain.model.UserInfo
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER

class FakeUserRepository : UserRepository {

    private val users = mutableListOf<TestUser>()

    private var shouldReturnNetworkError = false

    fun addUser(
        email: String,
        password: String? = null,
        providerIds: MutableList<String> = mutableListOf(),
        isEmailVerified: Boolean = false
    ) {
        users.add(
            TestUserDto(
                email = email,
                password = password,
                providerIds = providerIds,
                isEmailVerified = isEmailVerified
            ).toTestUser()
        )
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun registerWithEmail(email: String, password: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val user = users.find { it.email == email }
            val userWithSameEmailExist = user != null

            if (userWithSameEmailExist) {
                throw Exception()
            } else {
                addUser(email, password)
            }
        }
    }

    override suspend fun loginWithEmail(email: String, password: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            users.find { it.email == email && it.password == password } ?: throw Exception()
        }
    }

    override suspend fun loginWithGoogleAccount(account: GoogleSignInAccount) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val user = users.find { it.email == account.email }

            val userDoesNotExist = user == null
            if (userDoesNotExist) {
                addUser(
                    email = account.email.toString(),
                    providerIds = mutableListOf(FIREBASE_AUTH_PROVIDER_ID_GOOGLE),
                    isEmailVerified = true
                )
            } else {
                val isUserNotLinkedToGoogle = !user!!.providerIds.contains(
                    FIREBASE_AUTH_PROVIDER_ID_GOOGLE
                )

                if (isUserNotLinkedToGoogle) {
                    users.find {
                        it.email == account.email
                    }!!.providerIds.add(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)
                }
            }
        }
    }

    override suspend fun loginWithTwitterAccount(credential: AuthCredential) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserNotLinkedToTwitter = !users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_TWITTER
            )

            if (isUserNotLinkedToTwitter) {
                throw Exception()
            }
        }
    }

    override suspend fun loginWithFacebookAccount(accessToken: AccessToken) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserNotLinkedToFacebook = !users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
            )

            if (isUserNotLinkedToFacebook) {
                throw Exception()
            }
        }
    }

    override suspend fun sendResetPasswordEmail(email: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            users.find { it.email == email } ?: throw Exception("email not found")
        }
    }

    override suspend fun reloadCurrentUser() {
        /* NO-OP */
    }

    override fun getCachedUserInfo(): UserInfo {
        val currentUser = users[0]
        return UserInfo(
            uid = currentUser.uid,
            email = currentUser.email,
            isEmailVerified = currentUser.isEmailVerified
        )
    }

    override suspend fun getReloadedUserInfo(): UserInfo {
        val currentUser = users[0]
        return UserInfo(
            uid = currentUser.uid,
            email = currentUser.email,
            isEmailVerified = currentUser.isEmailVerified
        )
    }

    override fun getCurrentUserUid(): String = users[0].uid

    override fun getCurrentUserEmail(): String = users[0].email

    override suspend fun sendVerificationEmail() {
        if (shouldReturnNetworkError) {
            throw Exception()
        }
    }

    override fun logoutUser() {
        /* NO-OP */
    }

    override suspend fun reAuthenticateUser(password: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isPasswordNotCorrect = users[0].password != password
            if (isPasswordNotCorrect) {
                throw Exception()
            }
        }
    }

    override suspend fun getCurrentUserProviderIds(): List<String> = users[0].providerIds

    override suspend fun linkGoogleAccount(account: GoogleSignInAccount) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserNotLinkedToGoogle = !users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_GOOGLE
            )

            if (isUserNotLinkedToGoogle) {
                users[0].apply {
                    providerIds.add(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)
                    isEmailVerified = true
                }
            }
        }
    }

    override suspend fun unlinkGoogleAccount() {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserLinkedToGoogle = users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_GOOGLE
            )

            if (isUserLinkedToGoogle) {
                users[0].providerIds.remove(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)
            }
        }
    }

    override suspend fun linkTwitterAccount(credential: AuthCredential) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserNotLinkedToTwitter = !users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_TWITTER
            )

            if (isUserNotLinkedToTwitter) {
                users[0].apply {
                    providerIds.add(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
                    isEmailVerified = true
                }
            }
        }
    }

    override suspend fun unlinkTwitterAccount() {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserLinkedToTwitter = users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_TWITTER
            )

            if (isUserLinkedToTwitter) {
                users[0].providerIds.remove(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
            }
        }
    }

    override suspend fun linkFacebookAccount(accessToken: AccessToken) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserNotLinkedToFacebook = !users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
            )

            if (isUserNotLinkedToFacebook) {
                users[0].apply {
                    providerIds.add(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)
                    isEmailVerified = true
                }
            }
        }
    }

    override suspend fun unlinkFacebookAccount() {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val isUserLinkedToFacebook = users[0].providerIds.contains(
                FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
            )

            if (isUserLinkedToFacebook) {
                users[0].providerIds.remove(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)
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