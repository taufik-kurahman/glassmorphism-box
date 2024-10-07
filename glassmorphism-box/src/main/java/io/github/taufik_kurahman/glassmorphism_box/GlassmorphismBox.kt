package io.github.taufik_kurahman.glassmorphism_box

import androidx.compose.runtime.Composable
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.android.renderscript.Toolkit

@Composable
fun GlassmorphismBox(
    modifier: Modifier = Modifier,
    capturer: Capturer,
    contentAlignment: Alignment = Alignment.TopStart,
    blurRadius: Int = 25,
    onError: ((t: Throwable) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var positionInParent by remember {
        mutableStateOf(IntOffset.Zero)
    }
    var blurryBackground by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    LaunchedEffect(Unit) {
        try {
            val captureImageBitmap = capturer.captureAsync()
            val imageBitmap = captureImageBitmap.await()
            val configuredBitmap = imageBitmap.asAndroidBitmap().copy(
                Bitmap.Config.ARGB_8888,
                false
            )
            val blurryBitmap = Toolkit.blur(configuredBitmap, blurRadius)
            val blurryImageBitmap = blurryBitmap.asImageBitmap()
            blurryBackground = blurryImageBitmap
        } catch (t: Throwable) {
            onError?.invoke(t)
        }
    }
    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                val newPosition = IntOffset(
                    x = layoutCoordinates.positionInParent().x.toInt(),
                    y = layoutCoordinates.positionInParent().y.toInt()
                )
                if (positionInParent != newPosition) {
                    positionInParent = newPosition
                }
            }
            .drawWithCache {
                onDrawBehind {
                    blurryBackground?.let { imageBitmap ->
                        val srcOffset = IntOffset(
                            positionInParent.x,
                            positionInParent.y
                        )
                        val srcSize = IntSize(
                            imageBitmap.width,
                            imageBitmap.height - positionInParent.y
                        )
                        drawImage(
                            image = imageBitmap,
                            srcOffset = srcOffset,
                            srcSize = srcSize,
                            dstOffset = IntOffset.Zero
                        )
                    }
                }
            },
        contentAlignment = contentAlignment
    ) {
        content()
    }
}