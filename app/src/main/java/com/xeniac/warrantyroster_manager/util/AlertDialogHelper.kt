package com.xeniac.warrantyroster_manager.util

import android.content.Context
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AlertDialogHelper {

    fun showOneBtnAlertDialog(
        context: Context,
        @StringRes message: Int,
        @StringRes positiveBtn: Int,
        dismissAction: (() -> Unit)? = null
    ) = MaterialAlertDialogBuilder(context).apply {
        setMessage(context.getString(message))
        setPositiveButton(context.getString(positiveBtn)) { _, _ -> }
        setOnDismissListener { dismissAction?.let { it() } }
        setCancelable(false)
        show()
    }

    fun showTwoBtnAlertDialog(
        context: Context,
        @StringRes title: Int,
        message: String,
        @StringRes positiveBtn: Int,
        @StringRes negativeBtn: Int,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null
    ) = MaterialAlertDialogBuilder(context).apply {
        setTitle(context.getString(title))
        setMessage(message)
        setPositiveButton(context.getString(positiveBtn)) { _, _ -> positiveAction?.let { it() } }
        setNegativeButton(context.getString(negativeBtn)) { _, _ -> negativeAction?.let { it() } }
        setCancelable(false)
        show()
    }

    fun showThreeBtnAlertDialog(
        context: Context,
        title: String,
        @StringRes message: Int,
        @StringRes positiveBtn: Int,
        @StringRes negativeBtn: Int,
        @StringRes neutralBtn: Int,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
        neutralAction: (() -> Unit)? = null
    ) = MaterialAlertDialogBuilder(context).apply {
        setTitle(title)
        setMessage(context.getString(message))
        setPositiveButton(context.getString(positiveBtn)) { _, _ -> positiveAction?.let { it() } }
        setNegativeButton(context.getString(negativeBtn)) { _, _ -> negativeAction?.let { it() } }
        setNeutralButton(context.getString(neutralBtn)) { _, _ -> neutralAction?.let { it() } }
        setCancelable(false)
        show()
    }

    fun showSingleChoiceItemsDialog(
        context: Context,
        @StringRes title: Int,
        items: Array<String>,
        checkedItemIndex: Int,
        singleChoiceAction: (index: Int) -> Unit
    ) = MaterialAlertDialogBuilder(context).apply {
        setTitle(context.getString(title))
        setSingleChoiceItems(items, checkedItemIndex) { dialogInterface, index ->
            singleChoiceAction(index)
            dialogInterface.dismiss()
        }
        show()
    }
}