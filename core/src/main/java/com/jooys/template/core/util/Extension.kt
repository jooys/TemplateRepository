package com.jooys.template.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.filter


fun Modifier.singleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
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

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("현재 화면은 Activity가 아닙니다.")
}

@Composable
fun LazyGridState.OnBottomReached(
    initialValue: Boolean = false,
    loadMore: () -> Unit,
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf initialValue

            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    // Convert the state into a cold flow and collect
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .filter { it }
            .collect {
                // if should load more, then invoke loadMore
                loadMore()
            }
    }
}

fun Modifier.roundedCornerShape(
    borderWidth: Dp,
    borderColor: Color,
    backgroundColor: Color,
    radius: Dp,
): Modifier {
    return this
        .border(
            width = borderWidth,
            color = borderColor,
            shape = RoundedCornerShape(radius)
        )
        .background(
            color = backgroundColor,
            shape = RoundedCornerShape(radius)
        )
        .clip(RoundedCornerShape(radius))
}
