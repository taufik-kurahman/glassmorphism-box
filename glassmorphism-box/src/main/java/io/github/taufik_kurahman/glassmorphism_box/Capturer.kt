package io.github.taufik_kurahman.glassmorphism_box

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
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
    private val _captureRequests = Channel<CaptureRequest>(capacity = Channel.UNLIMITED)
    internal val captureRequests = _captureRequests.consumeAsFlow()

    fun captureAsync(): Deferred<ImageBitmap> {
        val deferredImageBitmap = CompletableDeferred<ImageBitmap>()
        return deferredImageBitmap.also {
            _captureRequests.trySend(CaptureRequest(imageBitmapDeferred = it))
        }
    }

    internal class CaptureRequest(val imageBitmapDeferred: CompletableDeferred<ImageBitmap>)
}

@Composable
fun rememberCapturer(): Capturer {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) { Capturer(graphicsLayer) }
}

@ExperimentalComposeUiApi
fun Modifier.capturable(controller: Capturer): Modifier {
    return this then CapturableModifierNodeElement(controller)
}

private data class CapturableModifierNodeElement(
    private val controller: Capturer
) : ModifierNodeElement<CapturableModifierNode>() {
    override fun create(): CapturableModifierNode {
        return CapturableModifierNode(controller)
    }

    override fun update(node: CapturableModifierNode) {
        node.updateController(controller)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "capturable"
        properties["controller"] = controller
    }
}

@Suppress("unused")
private class CapturableModifierNode(
    controller: Capturer
) : Modifier.Node(), DrawModifierNode {
    private val currentController = MutableStateFlow(controller)

    private val currentGraphicsLayer
        get() = currentController.value.graphicsLayer

    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            observeCaptureRequestsAndServe()
        }
    }

    fun updateController(newController: Capturer) {
        currentController.value = newController
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeCaptureRequestsAndServe() {
        currentController
            .flatMapLatest { it.captureRequests }
            .collect { request ->
                val completable = request.imageBitmapDeferred
                try {
                    completable.complete(currentGraphicsLayer.toImageBitmap())
                } catch (error: Throwable) {
                    completable.completeExceptionally(error)
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