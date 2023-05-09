package com.example.myrefreshcomponent

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { (this@toDp / density).dp }
}

@Composable
fun Dp.toPx(density: Float): Float {
    return this.value * density
}

@Composable
fun CustomSwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val translateY = remember { mutableStateOf(0f) }
    val density = LocalDensity.current.density
    val maxTranslatePx = 50.dp.toPx(density)
    val shouldAnimateBack = remember { mutableStateOf(false) }
    val shouldDisplayProgress = remember { mutableStateOf(false) }

    val scrollableState = rememberScrollableState { delta ->
        val oldTranslateY = translateY.value
        val newTranslateY = (oldTranslateY + delta).coerceIn(0f, maxTranslatePx)
        translateY.value = newTranslateY
        shouldAnimateBack.value = false
        delta - (newTranslateY - oldTranslateY)
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            shouldAnimateBack.value = true
            shouldDisplayProgress.value = true
        } else {
            shouldDisplayProgress.value = false
            translateY.value = 0f
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .scrollable(
                state = scrollableState,
                orientation = Orientation.Vertical
            )
    ) {
        if (translateY.value > 0f) {
            Box(modifier = Modifier.height((translateY.value / density).dp)) {
                if (shouldDisplayProgress.value) {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        content()

        if (translateY.value >= maxTranslatePx && !isRefreshing) {
            onRefresh()
        }
    }
}


