package com.jooys.template.core_design.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo


fun Modifier.singleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClick"] = onClick
    }
) {
    val singleClickListener = remember { ComposeSingleClickListener() }
    this.clickable(
        enabled = enabled,
        onClick = { singleClickListener.onSingleClick(onClick) },
    )
}
