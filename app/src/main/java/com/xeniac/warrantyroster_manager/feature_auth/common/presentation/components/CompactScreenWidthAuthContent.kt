package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupAuthNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ForgotPasswordGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LoginScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.RegisterScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthAction

@Composable
fun CompactScreenWidthAuthContent(
    currentAppLocale: AppLocale?,
    rootNavController: NavHostController,
    authNavController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(topStart = 64.dp),
    background: Color = White,
    onAction: (action: AuthAction) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection)
            )
    ) {
        Header(
            currentAppLocale = currentAppLocale,
            authNavController = authNavController,
            onAction = onAction
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(shape)
                .background(background)
        ) {
            SetupAuthNavGraph(
                rootNavController = rootNavController,
                authNavController = authNavController
            )
        }
    }
}

@Composable
private fun Header(
    currentAppLocale: AppLocale?,
    authNavController: NavHostController,
    modifier: Modifier = Modifier,
    onAction: (action: AuthAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        HeaderRow(authNavController = authNavController)

        LocaleButton(
            currentAppLocale = currentAppLocale,
            onAction = onAction,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun HeaderRow(
    authNavController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        end = 24.dp,
        top = 8.dp
    )
) {
    val backStackEntry by authNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    var headerTitleId by remember { mutableStateOf<Int?>(value = null) }
    var headerImageId by remember { mutableStateOf<Int?>(value = null) }

    LaunchedEffect(key1 = currentDestination) {
        when {
            currentDestination == null -> {
                headerTitleId = null
                headerImageId = null
            }
            currentDestination.hasRoute(LoginScreen::class) -> {
                headerTitleId = R.string.auth_title_login
                headerImageId = R.drawable.ic_auth_login
            }
            currentDestination.hasRoute(RegisterScreen::class) -> {
                headerTitleId = R.string.auth_title_register
                headerImageId = R.drawable.ic_auth_register
            }
            currentDestination.hasRoute(ForgotPasswordGraph.ForgotPwScreen::class) -> {
                headerTitleId = R.string.auth_title_forgot_pw
                headerImageId = R.drawable.ic_auth_forgot_pw
            }
            currentDestination.hasRoute(ForgotPasswordGraph.ResetPwInstructionScreen::class) -> {
                headerTitleId = R.string.auth_title_forgot_pw_instruction
                headerImageId = R.drawable.ic_auth_forgot_pw_instruction
            }
            else -> {
                headerTitleId = null
                headerImageId = null
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        HeaderTitle(
            headerTitleId = headerTitleId,
            modifier = Modifier.weight(1f)
        )

        HeaderImage(headerImageId = headerImageId)
    }
}

@Composable
private fun HeaderTitle(
    @StringRes headerTitleId: Int?,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = 400)
    ) + scaleIn(
        animationSpec = tween(durationMillis = 400)
    ),
    exiTransition: ExitTransition = scaleOut(
        animationSpec = tween(durationMillis = 400)
    ) + fadeOut(
        animationSpec = tween(durationMillis = 400)
    ),
    contentPadding: PaddingValues = PaddingValues(bottom = 8.dp),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 34.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Start,
        color = White
    )
) {
    AnimatedContent(
        targetState = headerTitleId,
        transitionSpec = { enterTransition.togetherWith(exit = exiTransition) },
        modifier = modifier
    ) { titleId ->
        Text(
            text = titleId?.let { stringResource(id = titleId) }.orEmpty(),
            style = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
    }
}

@Composable
private fun HeaderImage(
    @DrawableRes headerImageId: Int?,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 400)),
    exiTransition: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 400))
) {
    AnimatedContent(
        targetState = headerImageId,
        transitionSpec = { enterTransition.togetherWith(exit = exiTransition) },
        modifier = modifier
    ) { imageId ->
        imageId?.let {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                alpha = 0.90f,
                colorFilter = ColorFilter.tint(
                    color = Blue,
                    blendMode = BlendMode.Multiply
                ),
                modifier = modifier.height(120.dp)
            )
        }
    }
}