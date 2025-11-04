package com.example.finai.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    onItemSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFFFFC800),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(70.dp)
        ) {
            val items = listOf(
                BottomNavItem("home", R.drawable.ic_home),
                BottomNavItem("trending", R.drawable.ic_trending),
                BottomNavItem("DUMMY", 0), // Espaço para o FAB
                BottomNavItem("table", R.drawable.ic_table),
                BottomNavItem("profile", R.drawable.ic_profile)
            )

            items.forEach { item ->
                if (item.route == "DUMMY") {
                    // Item invisível para dar espaço ao FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { },
                        enabled = false
                    )
                } else {
                    val isSelected = selectedItem == item.route

                    // Animação de escala quando selecionado
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = tween(300),
                        label = "scale"
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onItemSelected(item.route) },
                        alwaysShowLabel = false,
                        icon = {
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
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            unselectedIconColor = Color.Black.copy(alpha = 0.6f),
                            indicatorColor = Color.Transparent
                        )
                    )
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