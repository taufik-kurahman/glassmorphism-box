package io.github.taufik_kurahman.glassmorphism_box

import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.android.renderscript.Toolkit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GlassmorphismBox(
    modifier: Modifier = Modifier,
    capturer: Capturer,
    contentAlignment: Alignment = Alignment.TopStart,
    blurRadius: Int = 25,
    onError: ((t: Throwable) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var backgroundPositionInRoot by remember { mutableStateOf(IntOffset.Zero) }
    var positionInRoot by remember { mutableStateOf(IntOffset.Zero) }
    var clipSize by remember { mutableStateOf(IntSize.Zero) }
    var backgroundImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val updatedContent by rememberUpdatedState(content)
    LaunchedEffect(backgroundPositionInRoot, positionInRoot, clipSize) {
        try {
            val capturedOffset = capturer.captureOffsetAsync().await()
            val newBgBoundsInRoot = IntOffset(
                capturedOffset.x.toInt(),
                capturedOffset.y.toInt()
            )
            if (backgroundPositionInRoot != newBgBoundsInRoot) {
                backgroundPositionInRoot = newBgBoundsInRoot
                if (backgroundImageBitmap == null && clipSize != IntSize.Zero) {
                    withContext(Dispatchers.Default) {
                        val capturedBitmap = capturer.captureBitmapAsync().await()
                        val configuredBitmap = capturedBitmap.asAndroidBitmap().copy(
                            Bitmap.Config.ARGB_8888,
                            false
                        )
                        val blurredImageBitmap =
                            Toolkit.blur(configuredBitmap, blurRadius).asImageBitmap()
                        val srcOffset = positionInRoot - backgroundPositionInRoot
                        backgroundImageBitmap = clipImageBitmap(
                            blurredImageBitmap,
                            srcOffset.x,
                            srcOffset.y,
                            clipSize.width,
                            clipSize.height
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