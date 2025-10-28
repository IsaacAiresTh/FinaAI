package com.example.finai.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
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
 * @param cutoutRadius O raio do recorte (metade do tamanho do FAB + margem)
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
            // Converter Dp para Px
            val radiusPx = with(density) { cutoutRadius.toPx() }
            val cornerPx = with(density) { topCornerRadius.toPx() }

            // O FAB é centrado, então o recorte é centrado
            val cutoutCenter = size.width / 2

            // Começa do canto inferior esquerdo e sobe para o canto superior esquerdo
            moveTo(0f, size.height)
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
            lineTo(cutoutCenter - radiusPx, 0f)

            // Arco do recorte (semicírculo)
            arcTo(
                rect = Rect(
                    center = Offset(cutoutCenter, 0f),
                    radius = radiusPx
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

            // Linha inferior
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}