package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import kotlinx.serialization.json.Json

val WarrantyNavType = object : NavType<Warranty?>(
    isNullableAllowed = true
) {

    override fun serializeAsValue(
        value: Warranty?
    ): String = Uri.encode(Json.encodeToString(value))

    override fun parseValue(
        value: String
    ): Warranty? = Json.decodeFromString(Uri.decode(value))

    //
    override fun put(
        bundle: SavedState,
        key: String,
        value: Warranty?
    ) = bundle.putString(
        /* key = */ key,
        /* value = */ Uri.encode(Json.encodeToString(value))
    )

    override fun get(
        bundle: SavedState,
        key: String
    ): Warranty? = bundle.getString(key)?.let { value ->
        Json.decodeFromString(Uri.decode(value))
    }
}