package io.github.taufik_kurahman.glassmorphism_box

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class Capturer(internal val graphicsLayer: GraphicsLayer) {
    private val _bitmapCaptureRequests =
        Channel<CompletableDeferred<ImageBitmap>>(capacity = Channel.UNLIMITED)
    private val _offsetCaptureRequests =
        Channel<CompletableDeferred<Offset>>(capacity = Channel.UNLIMITED)

    internal val bitmapCaptureRequests = _bitmapCaptureRequests.consumeAsFlow()
    internal val offsetCaptureRequests = _offsetCaptureRequests.consumeAsFlow()

    var capturedOffset: Offset = Offset.Zero
        private set

    fun captureBitmapAsync(): Deferred<ImageBitmap> {
        return CompletableDeferred<ImageBitmap>().also { deferred ->
            _bitmapCaptureRequests.trySend(deferred)
        }
    }

    fun captureOffsetAsync(): Deferred<Offset> {
        return CompletableDeferred<Offset>().also { deferred ->
            _offsetCaptureRequests.trySend(deferred)
        }
    }

    fun setOffset(value: Offset) {
        capturedOffset = value
    }
}

@Composable
fun rememberCapturer(): Capturer {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) { Capturer(graphicsLayer) }
}

@ExperimentalComposeUiApi
fun Modifier.glassmorphismBackground(capturer: Capturer): Modifier {
    return this then GlassmorphismBackgroundModifierNodeElement(capturer)
        .onGloballyPositioned {
            capturer.setOffset(it.positionInRoot())
        }
}

private data class GlassmorphismBackgroundModifierNodeElement(
    private val capturer: Capturer
) : ModifierNodeElement<GlassmorphismBackgroundModifierNode>() {
    override fun create(): GlassmorphismBackgroundModifierNode {
        return GlassmorphismBackgroundModifierNode(capturer)
    }

    override fun update(node: GlassmorphismBackgroundModifierNode) {
        node.updateCapturer(capturer)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "glassmorphismBackground"
        properties["capturer"] = capturer
    }
}

private class GlassmorphismBackgroundModifierNode(
    capturer: Capturer
) : Modifier.Node(), DrawModifierNode {
    private val currentCapturer = MutableStateFlow(capturer)

    private val currentGraphicsLayer
        get() = currentCapturer.value.graphicsLayer

    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            observeOffsetCaptureRequests()
        }
        coroutineScope.launch {
            observeBitmapCaptureRequests()
        }
    }

    fun updateCapturer(newCapturer: Capturer) {
        currentCapturer.value = newCapturer
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeBitmapCaptureRequests() {
        currentCapturer
            .flatMapLatest { it.bitmapCaptureRequests }
            .collect { request ->
                try {
                    val imageBitmap = currentGraphicsLayer.toImageBitmap()
                    request.complete(imageBitmap)
                } catch (error: Throwable) {
                    request.completeExceptionally(error)
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeOffsetCaptureRequests() {
        currentCapturer
            .flatMapLatest { it.offsetCaptureRequests }
            .collect { request ->
                try {
                    val capturedOffset = currentCapturer.value.capturedOffset
                    request.complete(capturedOffset)
                } catch (error: Throwable) {
                    request.completeExceptionally(error)
                }
            }
    }

    override fun ContentDrawScope.draw() {
        currentGraphicsLayer.record {
            this@draw.drawContent()
        }
        drawLayer(currentGraphicsLayer)
    }
}

internal fun clipImageBitmap(
    image: ImageBitmap,
    offsetX: Int,
    offsetY: Int,
    width: Int,
    height: Int
): ImageBitmap {
    val bitmap = image.asAndroidBitmap()

    val actualWidth = if (offsetX + width > bitmap.width) bitmap.width - offsetX else width
    val actualHeight = if (offsetY + height > bitmap.height) bitmap.height - offsetY else height

    val safeOffsetX = if (offsetX < 0) 0 else offsetX
    val safeOffsetY = if (offsetY < 0) 0 else offsetY

    val clippedBitmap = Bitmap.createBitmap(
        bitmap,
        safeOffsetX,
        safeOffsetY,
        actualWidth,
        actualHeight
    )

    return clippedBitmap.asImageBitmap()
}