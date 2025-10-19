package com.example.finai.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finai.R

/*
TODO: Colocar icone ao centro da bottom bar para upload de arquivos, seguindo o que esta no figma
 */

@Composable
fun CBottomNavBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp),
        color = Color(0xFFFFC800),
        tonalElevation = 4.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            val items = listOf(
                BottomNavItem("home", R.drawable.ic_home),
                BottomNavItem("trending", R.drawable.ic_trending),
                BottomNavItem("table", R.drawable.ic_table),
                BottomNavItem("profile", R.drawable.ic_profile)
            )
            items.forEach { item ->
                val isSelected = selectedItem == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(item.route) },
                    alwaysShowLabel = false,
                    icon = {
                        // background animado quando o icone e selecionado
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else Color.Transparent,
                            animationSpec = tween(300)
                        )
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        indicatorColor = Color.Transparent
                    )
                )
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
