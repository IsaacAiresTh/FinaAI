package com.example.finai.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Uma forma customizada para a Bottom App Bar com um recorte circular no centro.
 * @param cutoutRadius O raio do recorte
 * @param topCornerRadius O raio dos cantos superiores da barra
 */
class BottomBarCutoutShape(
    private val cutoutRadius: Dp,
    private val topCornerRadius: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val radiusPx = with(density) { cutoutRadius.toPx() }
            val cornerPx = with(density) { topCornerRadius.toPx() }
            val cutoutCenter = size.width / 2

            // Adiciona uma pequena margem ao redor do recorte para suavizar
            val cutoutMargin = 2.dp.value * density.density
            val actualRadius = radiusPx + cutoutMargin

            // Começa do canto inferior esquerdo
            moveTo(0f, size.height)

            // Sobe até o início do canto superior esquerdo
            lineTo(0f, cornerPx)

            // Arco do canto superior esquerdo
            arcTo(
                rect = Rect(
                    left = 0f,
                    top = 0f,
                    right = 2 * cornerPx,
                    bottom = 2 * cornerPx
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Linha superior até o início do recorte
            lineTo(cutoutCenter - actualRadius, 0f)

            // Arco do recorte (semicírculo invertido)
            arcTo(
                rect = Rect(
                    left = cutoutCenter - actualRadius,
                    top = -actualRadius,
                    right = cutoutCenter + actualRadius,
                    bottom = actualRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )

            // Linha do final do recorte até o canto superior direito
            lineTo(size.width - cornerPx, 0f)

            // Arco do canto superior direito
            arcTo(
                rect = Rect(
                    left = size.width - (2 * cornerPx),
                    top = 0f,
                    right = size.width,
                    bottom = 2 * cornerPx
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Linha da direita até o canto inferior direito
            lineTo(size.width, size.height)

            // Linha inferior e fecha o caminho
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}