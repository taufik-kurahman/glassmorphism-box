package io.github.taufik_kurahman.glassmorphism_box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GlassmorphismBox(
    modifier: Modifier = Modifier,
    capturer: Capturer,
    contentAlignment: Alignment = Alignment.TopStart,
    blurRadius: Int = 25,
    blurPasses: Int = 5,
    onError: ((t: Throwable) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    require(blurRadius in 1..25) {
        "The radius should be between 1 and 25. $blurRadius provided."
    }
    require(blurPasses in 1..5) {
        "The passes should be between 1 and 5. $blurPasses provided."
    }
    var backgroundPositionInRoot by remember { mutableStateOf(IntOffset.Zero) }
    var positionInRoot by remember { mutableStateOf(IntOffset.Zero) }
    var clipSize by remember { mutableStateOf(IntSize.Zero) }
    var backgroundImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val updatedContent by rememberUpdatedState(content)
    LaunchedEffect(backgroundPositionInRoot, positionInRoot, clipSize) {
        try {
            val capturedOffset = capturer.captureOffsetAsync().await()
            val newBackgroundPositionInRoot = IntOffset(
                capturedOffset.x.toInt(),
                capturedOffset.y.toInt()
            )
            if (backgroundPositionInRoot != newBackgroundPositionInRoot) {
                backgroundPositionInRoot = newBackgroundPositionInRoot
                if (backgroundImageBitmap == null && clipSize != IntSize.Zero) {
                    withContext(Dispatchers.Default) {
                        val capturedImageBitmap = capturer.captureImageBitmapAsync().await()
                        backgroundImageBitmap = createBlurredBackground(
                            capturedImageBitmap,
                            backgroundPositionInRoot,
                            positionInRoot,
                            clipSize,
                            blurRadius,
                            blurPasses
                        )
                    }
                }
            }
        } catch (t: Throwable) {
            onError?.invoke(t)
        }
    }
    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                if (clipSize != layoutCoordinates.size) {
                    clipSize = layoutCoordinates.size
                }
                val newPosition = IntOffset(
                    layoutCoordinates.positionInRoot().x.toInt(),
                    layoutCoordinates.positionInRoot().y.toInt()
                )
                if (positionInRoot != newPosition) {
                    positionInRoot = newPosition
                }
            }
            .drawBehind {
                backgroundImageBitmap?.let {
                    drawImage(it)
                }
            },
        contentAlignment = contentAlignment
    ) {
        updatedContent()
    }
}