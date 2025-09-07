package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
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
    onNavigateUpClick: (() -> Unit)? = null
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
        actions = {
            // TODO: ADD ACTION BUTTONS
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