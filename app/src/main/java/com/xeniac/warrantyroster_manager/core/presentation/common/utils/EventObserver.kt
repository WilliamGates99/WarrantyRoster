package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun <T> ObserverAsEvent(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    key3: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(
        lifecycleOwner.lifecycle,
        key1,
        key2,
        key3
    ) {
        lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            withContext(context = Dispatchers.Main.immediate) {
                flow.collect(collector = onEvent)
            }
        }
    }
}