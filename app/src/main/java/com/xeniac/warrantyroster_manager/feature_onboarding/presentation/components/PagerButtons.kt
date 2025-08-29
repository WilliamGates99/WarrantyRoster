package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun PagerButtons(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn() + scaleIn(),
    exitTransition: ExitTransition = scaleOut() + fadeOut(),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    ),
    onNavigateToAuthScreens: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = pagerState.currentPage == pagerState.pageCount - 1,
        transitionSpec = { (enterTransition).togetherWith(exit = exitTransition) },
        modifier = modifier.fillMaxWidth()
    ) { isLastPage ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            when {
                isLastPage -> StartButton(onClick = onNavigateToAuthScreens)
                else -> {
                    when (val currentPage = pagerState.currentPage) {
                        0 -> SkipButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page = pagerState.pageCount - 1)
                                }
                            }
                        )
                        else -> BackButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page = currentPage - 1)
                                }
                            }
                        )
                    }

                    NextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SkipButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors().copy(
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_skip),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors().copy(
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_back),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
private fun NextButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(
        containerColor = NavyBlue,
        disabledContainerColor = NavyBlue,
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 40.dp,
        vertical = 12.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_next),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
private fun StartButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(
        containerColor = NavyBlue,
        disabledContainerColor = NavyBlue,
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 40.dp,
        vertical = 12.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_start),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}