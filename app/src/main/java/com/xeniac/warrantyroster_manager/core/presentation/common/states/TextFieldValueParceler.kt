package com.xeniac.warrantyroster_manager.core.presentation.common.states

import android.os.Parcel
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.parcelize.Parceler

object TextFieldValueParceler : Parceler<TextFieldValue> {
    override fun TextFieldValue.write(
        parcel: Parcel,
        flags: Int
    ) {
        with(parcel) {
            writeString(text)
            writeInt(selection.start)
            writeInt(selection.end)

            composition?.let {
                writeByte(1) // Write composition nullability flag
                writeInt(it.start)
                writeInt(it.end)
            } ?: run {
                writeByte(0) // Write composition nullability flag
            }
        }
    }

    override fun create(
        parcel: Parcel
    ): TextFieldValue = with(parcel) {
        val text = readString().orEmpty()
        val selection = TextRange(
            start = readInt(),
            end = readInt()
        )

        val hasComposition = readByte().toInt() == 1
        val composition = if (hasComposition) {
            TextRange(
                start = readInt(),
                end = readInt()
            )
        } else null

        TextFieldValue(
            text = text,
            selection = selection,
            composition = composition
        )
    }
}