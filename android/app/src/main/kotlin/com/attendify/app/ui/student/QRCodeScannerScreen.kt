package com.attendify.app.ui.student

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.attendify.app.utils.Resource
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

/**
 * QR Code Scanner Screen for Student Attendance Check-in, with improved stability and functionality.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGetImage::class)
@Composable
fun QRCodeScannerScreen(
    viewModel: StudentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val qrActionStatus by viewModel.qrActionStatus.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Show message and navigate back on success or error
    LaunchedEffect(qrActionStatus) {
        qrActionStatus?.let {
            if (it is Resource.Success || it is Resource.Error) {
                delay(2500) // Show message briefly
                viewModel.clearQrActionStatus()
                if (it is Resource.Success) onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (hasCameraPermission) {
                CameraPreview(modifier = Modifier.fillMaxSize()) { qrCode ->
                    viewModel.handleScannedQRCode(qrCode)
                }

                // Overlay UI
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Point camera at a QR code for enrollment or check-in.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    qrActionStatus?.let { status ->
                        val (message, color) = when (status) {
                            is Resource.Success -> status.data to MaterialTheme.colorScheme.primaryContainer
                            is Resource.Error -> status.message to MaterialTheme.colorScheme.errorContainer
                            is Resource.Loading -> "Processing..." to MaterialTheme.colorScheme.surface
                        }

                        Card(colors = CardDefaults.cardColors(containerColor = color ?: MaterialTheme.colorScheme.surface)) {
                            Text(
                                text = message ?: "",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Permission denied UI
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Camera permission is required to scan QR codes.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

@ExperimentalGetImage
@Composable
private fun CameraPreview(modifier: Modifier = Modifier, onQRCodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    var isProcessing by remember { mutableStateOf(false) }

    AndroidView(factory = {
        val previewView = PreviewView(it)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(it)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (!isProcessing) {
                            processImageProxy(imageProxy, barcodeScanner) { qrCode ->
                                if (qrCode.isNotEmpty()) {
                                    isProcessing = true
                                    onQRCodeDetected(qrCode)
                                }
                            }
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } catch (e: Exception) {
                // Log or handle camera binding error
            }
        }, ContextCompat.getMainExecutor(context))

        previewView
    }, modifier = modifier)

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }
}

@ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onQRCodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }?.rawValue?.let {
                    onQRCodeDetected(it)
                }
            }
            .addOnCompleteListener { 
                imageProxy.close() // Ensure imageProxy is closed
            }
    } else {
        imageProxy.close() // Close if mediaImage is null
    }
}
