package com.jooys.template.core.component.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jooys.template.core.theme.CustomTheme
import com.jooys.template.core.theme.NEUTRAL_GRAY_50

@Composable
internal fun ToolbarWithButton(
    @DrawableRes iconResId: Int,
    title: String,
    withLine: Boolean = false,
    onClickIconButton: () -> Unit
) {
    Column(
        modifier = Modifier.background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onClickIconButton() },
                modifier = Modifier
                    .padding(4.dp)
                    .size(48.dp)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .padding(end = 24.dp),
                text = title,
                style = CustomTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if(withLine) {
            HorizontalDivider(
                thickness = 1.dp,
                color = NEUTRAL_GRAY_50
            )
        }
    }
}
