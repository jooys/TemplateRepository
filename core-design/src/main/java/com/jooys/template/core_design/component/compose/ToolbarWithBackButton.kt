package com.jooys.template.core_design.component.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jooys.template.core_design.R

@Composable
fun ToolbarWithBackButton(
    title: String,
    clickBackButton: () -> Unit,
) {
    ToolbarWithButton(
        title = title,
        iconResId = R.drawable.ic_2line_chevron_left,
        withLine = true,
        onClickIconButton = clickBackButton
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewToolbarWithBackButton() {
    ToolbarWithBackButton(
        title = "화면 타이틀",
        clickBackButton = {}
    )
}
