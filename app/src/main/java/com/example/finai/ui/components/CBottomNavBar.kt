package com.example.finai.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finai.R

@Composable
fun CBottomNavBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Container que define a barra de navegação
    Surface(
        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFFFFC800),
        shadowElevation = 8.dp
    ) {
        // Column para organizar a Row de ícones e o padding do sistema
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val items = listOf(
                    BottomNavItem("home", R.drawable.ic_home),
                    BottomNavItem("trending", R.drawable.ic_trending),
                    BottomNavItem("DUMMY", 0),
                    BottomNavItem("table", R.drawable.ic_table),
                    BottomNavItem("profile", R.drawable.ic_profile)
                )

                items.forEach { item ->
                    if (item.route == "DUMMY") {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val isSelected = selectedItem == item.route
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onItemSelected(item.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.1f else 1f,
                                animationSpec = tween(300),
                                label = "scale"
                            )

                            val backgroundColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else Color.Transparent,
                                animationSpec = tween(300),
                                label = "background"
                            )

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .background(backgroundColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    val label: String = route
)

@Preview
@Composable
private fun BottomNavPreview() {
    CBottomNavBar(selectedItem = "home", onItemSelected = {})
}
