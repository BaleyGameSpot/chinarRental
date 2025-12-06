package com.chinarrental.app.ui.screens

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chinarrental.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WarningScreen() {
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(4000) // Show warning for 4 seconds
        (context as? Activity)?.finishAndRemoveTask()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8B0000), // Dark red
                        Color(0xFFDC143C), // Crimson
                        Color(0xFFFF6347)  // Tomato
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Warning Icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFFDC143C)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Warning Title
                Text(
                    text = "⚠️ WARNING ⚠️",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B0000),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Warning Message
                Text(
                    text = "Payment Not Completed",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Project Terminated",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Auto-close message
                Text(
                    text = "App will close automatically...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
