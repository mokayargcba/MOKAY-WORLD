package com.example.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.game.model.Direction
import com.example.game.model.HouseSpec
import com.example.game.model.HouseStyle
import com.example.game.model.LivingFloraSpec
import com.example.game.model.FloraKind
import com.example.game.model.PlayerAnimState
import com.example.game.ui.drawBoxyCharacter
import com.example.game.ui.drawCupheadFlower
import com.example.game.ui.drawHouse2D
import com.example.game.ui.drawLivingFlora
import com.example.game.ui.drawSwayingGrassCluster
import com.example.game.ui.rememberMokaySpriteBank
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BoxyLavender
import com.example.ui.theme.CardSlate
import com.example.ui.theme.DeepTwilight
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.InkBlack
import com.example.ui.theme.RunicTeal
import com.example.ui.theme.SparkGold
import com.example.ui.theme.WarmParchment

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onAuthSuccess,
            onUnauthenticated = {},
            scope = scope
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "titleBounce")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val spriteBank = rememberMokaySpriteBank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF141126), DeepTwilight, Color(0xFF1B2B2B))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 840.dp)
                .heightIn(max = 420.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.96f)),
            border = BorderStroke(2.dp, BoxyLavender.copy(alpha = 0.48f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT PANE: Mini Illustrated Mokaya Scenic Stage (Boxy + Foskay + Townhouse + Trees + Flowers + Swaying Grass)
                Box(
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF28204A), Color(0xFF233840), Color(0xFF2A4A36))
                            )
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Horizon meadow & stone courtyard
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF335B40), Color(0xFF264532)),
                                startY = h * 0.56f,
                                endY = h
                            ),
                            topLeft = Offset(0f, h * 0.56f),
                            size = Size(w, h * 0.44f)
                        )
                        drawOval(
                            color = Color(0xFFD9C7A3).copy(alpha = 0.32f),
                            topLeft = Offset(w * 0.14f, h * 0.62f),
                            size = Size(w * 0.72f, h * 0.30f)
                        )

                        // Illustrated Mokaya houses behind Boxy
                        drawHouse2D(
                            house = HouseSpec(
                                id = "auth_h1",
                                name = "Taller",
                                x = w * 0.24f,
                                y = h * 0.54f,
                                width = 132f,
                                height = 98f,
                                roofColor = Color(0xFFD94E4E),
                                wallColor = Color(0xFFF4E7D1),
                                style = HouseStyle.TOWNHOUSE
                            ),
                            animPhase = animPhase
                        )
                        drawHouse2D(
                            house = HouseSpec(
                                id = "auth_h2",
                                name = "Café",
                                x = w * 0.77f,
                                y = h * 0.55f,
                                width = 128f,
                                height = 94f,
                                roofColor = Color(0xFF6C63FF),
                                wallColor = Color(0xFFEBDCCB),
                                style = HouseStyle.CAFE_AWNING
                            ),
                            animPhase = animPhase
                        )

                        // Swaying trees & animated Cuphead flowers
                        drawLivingFlora(
                            LivingFloraSpec("at1", w * 0.08f, h * 0.64f, FloraKind.TREE, 0.95f, 0.2f, Color(0xFF4CC37A)),
                            animPhase, 0f
                        )
                        drawLivingFlora(
                            LivingFloraSpec("at2", w * 0.92f, h * 0.66f, FloraKind.TREE, 0.92f, 1.4f, Color(0xFF2EC4B6)),
                            animPhase, 0f
                        )
                        drawSwayingGrassCluster(w * 0.18f, h * 0.86f, Color(0xFF4CC37A), animPhase, 1.1f)
                        drawSwayingGrassCluster(w * 0.83f, h * 0.86f, Color(0xFF4CC37A), animPhase + 1.2f, 1.1f)
                        drawCupheadFlower(w * 0.25f, h * 0.84f, 0.95f, Color(0xFFE97893), animPhase, 0f)
                        drawCupheadFlower(w * 0.75f, h * 0.84f, 0.90f, SparkGold, animPhase + 1.4f, 0f)

                        // Boxy + Foskay emerging inside
                        drawBoxyCharacter(
                            x = w * 0.50f,
                            y = h * 0.72f,
                            direction = Direction.FRONT,
                            animState = PlayerAnimState.IDLE,
                            phase = animPhase,
                            actionProgress = 0f,
                            scale = 1.35f,
                            spriteBank = spriteBank,
                            foskayLightActive = true,
                            foskayEmergence = 0.86f
                        )
                    }
                }

                // RIGHT PANE: Title, Features & Google Sign-In
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = FlameOrange.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = SparkGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.auth_tagline),
                                style = MaterialTheme.typography.labelSmall,
                                color = SparkGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "MOKAY WORLD",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                        color = WarmParchment,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stringResource(R.string.auth_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = BoxyLavender,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FeaturePill(Icons.Filled.Explore, "11 Regiones")
                        FeaturePill(Icons.Filled.AutoAwesome, "Puzzles & Craft")
                        FeaturePill(Icons.Filled.CloudDone, "Nube")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (errorMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = WarmParchment,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Button(
                        onClick = {
                            errorMessage = null
                            isLoading = true
                            onGoogleSignInClicked(
                                context = context,
                                credentialManager = credentialManager,
                                onAuthSuccess = {
                                    isLoading = false
                                    onAuthSuccess()
                                },
                                onAuthError = { msg ->
                                    isLoading = false
                                    errorMessage = msg
                                },
                                scope = scope,
                                onAuthCancelled = {
                                    isLoading = false
                                }
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_sign_in_button"),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlameOrange,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.sign_in_with_google),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.auth_cloud_note),
                        style = MaterialTheme.typography.labelSmall,
                        color = RunicTeal.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = InkBlack.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AntiqueGold,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = WarmParchment
            )
        }
    }
}
