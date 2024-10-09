package io.github.taufik_kurahman.glassmorphism_box_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.taufik_kurahman.glassmorphism_box.GlassmorphismBox
import io.github.taufik_kurahman.glassmorphism_box.glassmorphismBackground
import io.github.taufik_kurahman.glassmorphism_box.rememberCapturer
import io.github.taufik_kurahman.glassmorphism_box_example.ui.theme.GlassmorphismboxTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlassmorphismboxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Example(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Example(modifier: Modifier) {
    val scrollState = rememberScrollState()
    val items = (1..10).map { it }
    val dividerBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0x00FFFFFF),
                Color(0xFFFFFFFF)
            )
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LargeCard(dividerBrush)
        items.forEach { item ->
            SmallCard(item, dividerBrush)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LargeCard(
    dividerBrush: Brush
) {
    val capturer = rememberCapturer()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painterResource(R.drawable.img_3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .glassmorphismBackground(capturer)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ut ipsum risus.",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GlassmorphismBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, dividerBrush, RoundedCornerShape(8.dp)),
                        capturer = capturer,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lorem",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    GlassmorphismBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, dividerBrush, RoundedCornerShape(8.dp)),
                        capturer = capturer,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ipsum",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SmallCard(
    item: Int,
    dividerBrush: Brush
) {
    val capturer = rememberCapturer()
    val bgResId = if (Random.nextInt() % 2 == 0) {
        R.drawable.img_1
    } else {
        R.drawable.img_2
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painterResource(bgResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphismBackground(capturer)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.6f)
                    .padding(16.dp)
            ) {
                Text(
                    "Item Title",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "Item Subtitle",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            GlassmorphismBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.4f)
                    .drawBehind {
                        drawLine(
                            brush = dividerBrush,
                            start = Offset.Zero,
                            end = Offset(x = this.size.width, y = 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                capturer = capturer,
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Item description",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Item location",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..Random.nextInt(1, 5)) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = .2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                item.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = .2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Water,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = .2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SignalCellularAlt,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}