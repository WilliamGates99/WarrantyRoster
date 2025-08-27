package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ArrayRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
sealed class UiText : Parcelable {
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @param:StringRes val resId: Int,
        vararg val args: @RawValue Any
    ) : UiText()

    class PluralStringResource(
        @param:PluralsRes val resId: Int,
        val quantity: Int,
        vararg val args: @RawValue Any
    ) : UiText()

    class StringArrayResource(
        @param:ArrayRes val resId: Int,
        val index: Int
    ) : UiText()

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is StringResource -> stringResource(id = resId, *args)
        is PluralStringResource -> pluralStringResource(id = resId, count = quantity, *args)
        is StringArrayResource -> stringArrayResource(id = resId)[index]
    }

    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(resId, *args)
        is PluralStringResource -> context.resources.getQuantityString(resId, quantity, *args)
        is StringArrayResource -> context.resources.getStringArray(resId)[index]
    }

    fun getValueOrResId(): String = when (this) {
        is DynamicString -> value
        is StringResource -> resId.toString()
        is PluralStringResource -> resId.toString()
        is StringArrayResource -> resId.toString()
    }
}