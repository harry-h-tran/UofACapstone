package com.example.uofacapstone.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HitMissButtons(
    onHit: () -> Unit,
    onMiss: () -> Unit,
    modifier: Modifier = Modifier,
    hitEnabled: Boolean = true,
    missEnabled: Boolean = true,
    hitText: String = "HIT",
    missText: String = "MISS",
    showIcons: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TrackingButton(
            text = hitText,
            onClick = onHit,
            enabled = hitEnabled,
            containerColor = Color(0xFF4CAF50),
            icon = if (showIcons) Icons.Default.Check else null,
            iconTint = Color(0xFF2E7D32)
        )

        TrackingButton(
            text = missText,
            onClick = onMiss,
            enabled = missEnabled,
            containerColor = Color(0xFFF44336),
            icon = if (showIcons) Icons.Default.Close else null,
            iconTint = Color(0xFFC62828)
        )
    }
}

@Composable
fun TrackingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color,
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    height: androidx.compose.ui.unit.Dp = 140.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 80.sp
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(24.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = iconTint
                )
            }
        }
    }
}
