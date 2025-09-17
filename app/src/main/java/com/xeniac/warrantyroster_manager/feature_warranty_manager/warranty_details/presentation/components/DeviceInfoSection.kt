package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray400
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue

@Composable
fun DeviceInfoSection(
    brand: String,
    model: String,
    serialNumber: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 4.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Title()

        DeviceInfo(
            brand = brand,
            model = model,
            serialNumber = serialNumber
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.warranty_details_device_info_title).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    ),
    maxLines: Int = 1
) {
    Text(
        text = title,
        style = textStyle,
        maxLines = maxLines,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
    )
}

@Composable
private fun DeviceInfo(
    brand: String,
    model: String,
    serialNumber: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        DeviceInfoItem(
            title = stringResource(id = R.string.warranty_details_device_info_brand_title),
            value = brand
        )

        DeviceInfoItem(
            title = stringResource(id = R.string.warranty_details_device_info_model_title),
            value = model
        )

        DeviceInfoItem(
            title = stringResource(id = R.string.warranty_details_device_info_serial_number_title),
            value = serialNumber
        )
    }
}

@Composable
private fun DeviceInfoItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(id = R.string.warranty_details_device_info_placeholder),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.dynamicGray400
    ),
    titleMaxLines: Int = 1,
    titleOverflow: TextOverflow = TextOverflow.StartEllipsis,
    valueStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        textDirection = TextDirection.Content,
        textAlign = TextAlign.End,
        color = when {
            value.isBlank() -> MaterialTheme.colorScheme.dynamicGray400
            else -> MaterialTheme.colorScheme.dynamicBlack
        }
    ),
    valueMaxLines: Int = 1
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = titleStyle,
            maxLines = titleMaxLines,
            overflow = titleOverflow,
            softWrap = false,
            modifier = modifier.weight(1f)
        )

        Text(
            text = when {
                value.isBlank() -> placeholder
                else -> value
            },
            style = valueStyle,
            maxLines = valueMaxLines,
        )
    }
}