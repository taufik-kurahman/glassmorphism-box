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
    var backgroundBoundsInRoot by remember { mutableStateOf(IntOffset.Zero) }
    var boundsInRoot by remember { mutableStateOf(IntOffset.Zero) }
    var backgroundImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val updatedContent by rememberUpdatedState(content)
    LaunchedEffect(backgroundBoundsInRoot, boundsInRoot) {
        try {
            val capturedOffset = capturer.captureOffsetAsync().await()
            val newBgBoundsInRoot = IntOffset(
                capturedOffset.x.toInt(),
                capturedOffset.y.toInt()
            )
            if (backgroundBoundsInRoot != newBgBoundsInRoot) {
                backgroundBoundsInRoot = newBgBoundsInRoot
                if (backgroundImageBitmap == null) {
                    withContext(Dispatchers.Default) {
                        val capturedBitmap = capturer.captureBitmapAsync().await()
                        val configuredBitmap = capturedBitmap.asAndroidBitmap().copy(
                            Bitmap.Config.ARGB_8888,
                            false
                        )
                        val blurredImageBitmap =
                            Toolkit.blur(configuredBitmap, blurRadius).asImageBitmap()
                        val srcOffset = boundsInRoot - backgroundBoundsInRoot
                        backgroundImageBitmap = clipImageBitmap(
                            blurredImageBitmap,
                            srcOffset.x,
                            srcOffset.y,
                            capturedBitmap.width,
                            capturedBitmap.height
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
                val newPosition = IntOffset(
                    layoutCoordinates.positionInRoot().x.toInt(),
                    layoutCoordinates.positionInRoot().y.toInt()
                )
                if (boundsInRoot != newPosition) {
                    boundsInRoot = newPosition
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