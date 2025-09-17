package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireDecimalFormat
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray400
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.states.WarrantyExpiryStatus
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils.calculateWarrantyExpiryStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.text.DecimalFormat

@Composable
fun HeaderSection(
    category: WarrantyCategory,
    isLifetime: Boolean,
    startingDate: LocalDate,
    expiryDate: LocalDate?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 4.dp)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        CategoryIcon(category = category)

        DateInfoSection(
            isLifetime = isLifetime,
            startingDate = startingDate,
            expiryDate = expiryDate,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DateInfoSection(
    isLifetime: Boolean,
    startingDate: LocalDate,
    expiryDate: LocalDate?,
    modifier: Modifier = Modifier,
    decimalFormat: DecimalFormat = requireDecimalFormat()
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        modifier = modifier
    ) {
        ExpiryStatus(
            expiryStatus = calculateWarrantyExpiryStatus(
                isLifetime = isLifetime,
                expiryDate = expiryDate
            )
        )

        DateSection(
            title = stringResource(id = R.string.warranty_details_starting_date_title),
            value = with(startingDate) {
                stringResource(
                    id = R.string.warranty_details_starting_date_value,
                    decimalFormat.format(month.number),
                    decimalFormat.format(day),
                    year
                )
            }
        )

        DateSection(
            title = stringResource(id = R.string.warranty_details_expiry_date_title),
            value = when {
                isLifetime -> stringResource(id = R.string.warranty_details_expiry_date_lifetime)
                else -> with(expiryDate!!) {
                    stringResource(
                        id = R.string.warranty_details_expiry_date_value,
                        decimalFormat.format(month.number),
                        decimalFormat.format(day),
                        year
                    )
                }
            }
        )
    }
}

@Composable
private fun ExpiryStatus(
    expiryStatus: WarrantyExpiryStatus,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    background: Color = expiryStatus.color.copy(alpha = 0.20f),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp
    ),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        color = expiryStatus.color
    ),
    overflow: TextOverflow = TextOverflow.MiddleEllipsis,
    maxLines: Int = 1
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .padding(contentPadding)
    ) {
        Text(
            text = stringResource(id = expiryStatus.descriptionId).uppercase(),
            style = textStyle,
            maxLines = maxLines,
            overflow = overflow,
            softWrap = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DateSection(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: Painter = painterResource(id = R.drawable.ic_warranties_calendar),
    iconColor: Color = MaterialTheme.colorScheme.dynamicGray400,
    iconSize: Dp = 14.dp,
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.dynamicGray400
    ),
    titleMaxLines: Int = 1,
    valueStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Bold,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.End,
        color = MaterialTheme.colorScheme.dynamicBlack
    ),
    valueMaxLines: Int = 1,
    valueOverflow: TextOverflow = TextOverflow.MiddleEllipsis
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )

        Text(
            text = title,
            style = titleStyle,
            maxLines = titleMaxLines
        )

        Text(
            text = value,
            style = valueStyle,
            maxLines = valueMaxLines,
            overflow = valueOverflow,
            softWrap = false,
            modifier = modifier.weight(1f)
        )
    }
}