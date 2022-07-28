package com.kazuki.fakecameraclient.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE
import android.util.Log
import android.util.Range
import androidx.activity.compose.BackHandler
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazuki.fakecameraclient.FakeCameraAppState
import com.kazuki.fakecameraclient.data.AlertType
import com.kazuki.fakecameraclient.ui.components.DefaultAlertDialog
import com.kazuki.fakecameraclient.util.YuvToRgbConverter
import com.kazuki.fakecameraclient.util.executor
import com.kazuki.fakecameraclient.util.getCameraProvider
import com.kazuki.fakecameraclient.util.parse

@ExperimentalAnimationApi
@Composable
fun FakeCamera(
    appState: FakeCameraAppState,
    modifier: Modifier = Modifier,
    viewModel: FakeCameraViewModel,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA,
) {
    BackHandler(enabled = true) {
        viewModel.onBackPress()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = modifier) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = Color.parse("#4CAF50"),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Connected to [name]")
                    }
                }
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                var previewUseCase by remember {
                    mutableStateOf<UseCase>(
                        Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build()
                    )
                }
                val imageAnalysisBuider = ImageAnalysis.Builder()
//            .setTargetResolution(Size(640, 480))
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                val extender = Camera2Interop.Extender(imageAnalysisBuider)
                extender.setCaptureRequestOption(CONTROL_AE_TARGET_FPS_RANGE, Range<Int>(12, 12))
                val imageAnalyzer =
                    imageAnalysisBuider.build().also { analysisUseCase: ImageAnalysis ->
                        analysisUseCase.setAnalyzer(context.executor, ImageAnalyzer(context) {
                            viewModel.sendBitmap(it)
                        })
                    }
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onUseCase = {
                        previewUseCase = it
                    }
                )
                LaunchedEffect(previewUseCase) {
                    val cameraProvider = context.getCameraProvider()
                    try {
                        // Must unbind the use-cases before rebinding them.
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, previewUseCase, imageAnalyzer
                        )
                    } catch (ex: Exception) {
                        Log.e("CameraCapture", "Failed to bind camera use cases", ex)
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(128.dp)
                .padding(32.dp),
            shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
            icon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) },
            text = { Text("Disconnect") },
            onClick = { viewModel.onBackPress() })
    }
    DefaultAlertDialog(viewModel.alert)
}

private class ImageAnalyzer(
    ctx: Context,
    private val onReport: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (!::bitmapBuffer.isInitialized) {
            // The image rotation and RGB image buffer are initialized only once
            // the analyzer has started running
            imageRotationDegrees = imageProxy.imageInfo.rotationDegrees
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
            )
        }

        imageProxy.use { yuvToRgbConverter.yuvToRgb(imageProxy.image!!, bitmapBuffer) }
        synchronized(true) {
            onReport(bitmapBuffer)
        }
    }

    private val yuvToRgbConverter = YuvToRgbConverter(ctx)
    private var imageRotationDegrees: Int = 0
    private lateinit var bitmapBuffer: Bitmap
}