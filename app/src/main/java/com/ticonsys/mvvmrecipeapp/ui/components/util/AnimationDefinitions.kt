package com.ticonsys.mvvmrecipeapp.ui.components.util

import androidx.compose.animation.ColorPropKey
import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.Infinite
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * Author: https://github.com/Gurupreet/ComposeCookBook/blob/master/app/src/main/java/com/guru/composecookbook/ui/Animations/AnimationDefinitions.kt
 */
object AnimationDefinitions {
    // Each animation should be explained as a definition and using states.
    enum class AnimationState {
        START, MID, END
    }

    val shimmerColorPropKey = ColorPropKey(label = "shimmerColor")
    val shimmerColorAnimation = transitionDefinition<AnimationState> {
        state(AnimationState.START) {
            this[shimmerColorPropKey] = Color.LightGray.copy(alpha = 0.6f)
        }
        state(AnimationState.MID) {
            this[shimmerColorPropKey] = Color.LightGray.copy(alpha = 0.9f)
        }
        state(AnimationState.END) {
            this[shimmerColorPropKey] = Color.LightGray
        }
        transition(
            AnimationState.START to AnimationState.MID,
            AnimationState.MID to AnimationState.END,
            AnimationState.END to AnimationState.START
        ) {
            shimmerColorPropKey using tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing,
            )
        }
    }

    val shimmerDpPropKey = DpPropKey("shimmerdp")
    val shimmerTranslateAnimation = transitionDefinition<AnimationState> {
        state(AnimationState.START) { this[shimmerDpPropKey] = 100.dp }
        state(AnimationState.END) { this[shimmerDpPropKey] = 1200.dp }

        transition(AnimationState.START, AnimationState.END) {
            shimmerDpPropKey using repeatable(
                iterations = Infinite,
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                )
            )
        }
    }


}