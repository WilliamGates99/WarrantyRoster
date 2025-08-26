package com.xeniac.warrantyroster_manager.core.domain.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object CryptoHelper {
    private const val KEY_ALIAS = "warranty_roster_key"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"

    private val cipher = Cipher.getInstance(TRANSFORMATION)
    private val keyStore = KeyStore.getInstance(/* type = */ "AndroidKeyStore").apply {
        load(/* param = */ null)
    }

    private fun getSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(
            /* alias = */ KEY_ALIAS,
            /* protParam = */ null
        ) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createSecretKey()
    }

    private fun createSecretKey(): SecretKey = KeyGenerator.getInstance(ALGORITHM).apply {
        init(
            KeyGenParameterSpec.Builder(
                /* keystoreAlias = */ KEY_ALIAS,
                /* purposes = */ KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(BLOCK_MODE)
                setEncryptionPaddings(ENCRYPTION_PADDING)
                setRandomizedEncryptionRequired(true)
                setUserAuthenticationRequired(false)
            }.build()
        )
    }.generateKey()

    fun encrypt(bytes: ByteArray): ByteArray {
        cipher.init(
            /* opmode = */ Cipher.ENCRYPT_MODE,
            /* key = */ getSecretKey()
        )

        val iv = cipher.iv
        val encrypted = cipher.doFinal(/* input = */ bytes)

        return iv + encrypted
    }

    fun decrypt(bytes: ByteArray): ByteArray {
        val iv = bytes.copyOfRange(fromIndex = 0, toIndex = cipher.blockSize)
        val data = bytes.copyOfRange(fromIndex = cipher.blockSize, toIndex = bytes.size)

        cipher.init(
            /* opmode = */ Cipher.DECRYPT_MODE,
            /* key = */ getSecretKey(),
            /* params = */ IvParameterSpec(iv)
        )

        return cipher.doFinal(data)
    }
}