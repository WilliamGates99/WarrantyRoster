package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CradleCutoutShape(
    private val cutoutRadiusPx: Float? = null,
    private val cradleSpacePx: Float = 0f,
    private val topStartCornerRadiusPx: Float = 0f,
    private val topEndCornerRadiusPx: Float = 0f,
    private val bottomStartCornerRadiusPx: Float = 0f,
    private val bottomEndCornerRadiusPx: Float = 0f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val cradleRadius = cutoutRadiusPx?.let { radiusPx ->
                (radiusPx + cradleSpacePx) / 2
            } ?: ((size.height + cradleSpacePx) / 2)

            // Add a rounded rectangle with custom corner radii
            addRoundRect(
                roundRect = RoundRect(
                    rect = Rect(
                        offset = Offset.Zero,
                        size = Size(
                            width = size.width,
                            height = size.height
                        )
                    ),
                    topLeft = CornerRadius(topStartCornerRadiusPx),
                    topRight = CornerRadius(topEndCornerRadiusPx),
                    bottomRight = CornerRadius(bottomEndCornerRadiusPx),
                    bottomLeft = CornerRadius(bottomStartCornerRadiusPx)
                )
            )

            // Add the oval for the cradle cutout
            addOval(
                oval = Rect(
                    center = Offset(
                        x = size.width / 2,
                        y = 0f
                    ),
                    radius = cradleRadius
                )
            )

            // Perform the difference operation to cut out the oval
            op(
                path1 = this,
                path2 = Path().apply {
                    addOval(
                        oval = Rect(
                            center = Offset(
                                x = size.width / 2,
                                y = 0f
                            ),
                            radius = cradleRadius
                        )
                    )
                },
                operation = PathOperation.Difference
            )
        }

        return Outline.Generic(path = path)
    }
}