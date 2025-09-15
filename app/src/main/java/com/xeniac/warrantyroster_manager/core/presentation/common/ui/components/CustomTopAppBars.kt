package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCenterAlignedTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.dynamicBlack,
        navigationIconContentColor = MaterialTheme.colorScheme.dynamicBlack,
        actionIconContentColor = MaterialTheme.colorScheme.dynamicBlack
    ),
    onNavigateUpClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = colors,
        title = {
            Text(
                text = title,
                style = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )
        },
        navigationIcon = {
            onNavigateUpClick?.let {
                NavigateUpIcon(onClick = it)
            }
        },
        actions = actions,
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 4.dp,
                    color = Black,
                    alpha = 0.24f
                )
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCenterAlignedTopAppBarWithSearchBar(
    title: String,
    isSearchBarVisible: Boolean,
    searchQueryState: CustomTextFieldState,
    searchBarPlaceholder: String?,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.dynamicBlack,
        navigationIconContentColor = MaterialTheme.colorScheme.dynamicBlack,
        actionIconContentColor = MaterialTheme.colorScheme.dynamicBlack
    ),
    onNavigateUpClick: (() -> Unit)? = null,
    onSearchClick: () -> Unit,
    onCloseSearchClick: () -> Unit,
    onSearchValueChange: (newValue: TextFieldValue) -> Unit
) {
    val focusManager = LocalFocusManager.current

    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        colors = colors,
        title = {
            SearchBarSection(
                isSearchBarVisible = isSearchBarVisible,
                title = title,
                searchQueryState = searchQueryState,
                searchBarPlaceholder = searchBarPlaceholder,
                onSearchValueChange = onSearchValueChange,
                keyboardAction = { focusManager.clearFocus() }
            )
        },
        navigationIcon = {
            onNavigateUpClick?.let {
                NavigateUpIcon(onClick = it)
            }
        },
        actions = {
            SearchActionButtons(
                isSearchBarVisible = isSearchBarVisible,
                onSearchClick = onSearchClick,
                onCloseSearchClick = {
                    focusManager.clearFocus()
                    onCloseSearchClick()
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 4.dp,
                    color = Black,
                    alpha = 0.24f
                )
            )
    )
}

@Composable
private fun NavigateUpIcon(
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.dynamicBlack,
        disabledContentColor = MaterialTheme.colorScheme.dynamicBlack.copy(alpha = 0.38f)
    ),
    icon: Painter = painterResource(id = R.drawable.ic_core_navigate_up),
    contentDescription: String = stringResource(id = R.string.core_content_description_navigate_up),
    iconSize: Dp = 18.dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = modifier.size(iconSize)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarSection(
    isSearchBarVisible: Boolean,
    title: String,
    searchQueryState: CustomTextFieldState,
    searchBarPlaceholder: String?,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = 220,
            delayMillis = 90
        )
    ) + scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(durationMillis = 220, delayMillis = 90)
    ),
    exitTransition: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 90)),
    onSearchValueChange: (newValue: TextFieldValue) -> Unit,
    keyboardAction: () -> Unit
) {
    AnimatedContent(
        targetState = isSearchBarVisible,
        transitionSpec = { enterTransition.togetherWith(exit = exitTransition) },
        modifier = modifier
    ) { isSearchActive ->
        when {
            isSearchActive -> {
                SearchBar(
                    inputField = {
                        CustomSearchBarTextField(
                            value = searchQueryState.value,
                            placeholder = searchBarPlaceholder,
                            keyboardAction = keyboardAction,
                            onValueChange = onSearchValueChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    content = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                Text(
                    text = title,
                    style = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@Composable
private fun SearchActionButtons(
    isSearchBarVisible: Boolean,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn() + scaleIn(),
    exitTransition: ExitTransition = scaleOut() + fadeOut(),
    onSearchClick: () -> Unit,
    onCloseSearchClick: () -> Unit
) {
    AnimatedContent(
        targetState = isSearchBarVisible,
        transitionSpec = { enterTransition.togetherWith(exit = exitTransition) },
        modifier = modifier
    ) { isSearchActive ->
        when {
            isSearchActive -> CloseSearchActionButton(onClick = onCloseSearchClick)
            else -> SearchActionButton(onClick = onSearchClick)
        }
    }
}

@Composable
private fun SearchActionButton(
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.dynamicBlack,
        disabledContentColor = MaterialTheme.colorScheme.dynamicBlack.copy(alpha = 0.38f)
    ),
    icon: Painter = painterResource(id = R.drawable.ic_core_search),
    contentDescription: String = stringResource(id = R.string.core_search_content_description_show),
    iconSize: Dp = 18.dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = modifier.size(iconSize)
        )
    }
}

@Composable
private fun CloseSearchActionButton(
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.dynamicBlack,
        disabledContentColor = MaterialTheme.colorScheme.dynamicBlack.copy(alpha = 0.38f)
    ),
    icon: Painter = painterResource(id = R.drawable.ic_core_search_close),
    contentDescription: String = stringResource(id = R.string.core_search_content_description_hide),
    iconSize: Dp = 18.dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = colors,
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = modifier.size(iconSize)
        )
    }
}