package com.xeniac.warrantyroster_manager.core.domain.models

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.xeniac.warrantyroster_manager.core.domain.utils.CryptoHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

typealias AppUpdateDialogShowCount = Int
typealias AppUpdateDialogShowDateTime = String
typealias PreviousRateAppRequestDateTime = String

@Serializable
data class MiscellaneousPreferences(
    val appUpdateDialogShowCount: AppUpdateDialogShowCount = 0,
    val appUpdateDialogShowDateTime: AppUpdateDialogShowDateTime? = null,
    val selectedRateAppOption: RateAppOption = RateAppOption.NOT_SHOWN_YET,
    val previousRateAppRequestDateTime: PreviousRateAppRequestDateTime? = null
)

object MiscellaneousPreferencesSerializer : Serializer<MiscellaneousPreferences> {

    override val defaultValue: MiscellaneousPreferences = MiscellaneousPreferences()

    override suspend fun readFrom(
        input: InputStream
    ): MiscellaneousPreferences = try {
        withContext(Dispatchers.IO) {
            val encryptedBytes = input.use { it.readBytes() }
            val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
            val decryptedBytes = CryptoHelper.decrypt(bytes = encryptedBytesDecoded)
            val decodedJsonString = decryptedBytes.decodeToString()

            Json.decodeFromString(decodedJsonString)
        }
    } catch (e: SerializationException) {
        throw CorruptionException(
            message = "Unable to read MiscellaneousPreferences",
            cause = e
        )
    }

    override suspend fun writeTo(
        t: MiscellaneousPreferences,
        output: OutputStream
    ) {
        withContext(Dispatchers.IO) {
            val encodedJsonString = Json.encodeToString(value = t)
            val bytes = encodedJsonString.toByteArray()
            val encryptedBytes = CryptoHelper.encrypt(bytes = bytes)
            val encryptedBytesBase64 = Base64.getEncoder().encode(encryptedBytes)

            output.use { it.write(encryptedBytesBase64) }
        }
    }
}