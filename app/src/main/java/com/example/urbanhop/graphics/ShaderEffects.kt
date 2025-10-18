package com.example.urbanhop.graphics

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer

private const val GREYSCALE_SHADER =
    """
    uniform float2 childResolution;
    uniform shader background;
    half4 main(float2 fragCoord){
           half4 color = background.eval( float2(fragCoord.x + childResolution.x, fragCoord.y + childResolution.y + 60));
        color.rgb = half3(dot(color.rgb, half3(0.2126, 0.7152, 0.0722)));
        return color;
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Modifier.greyscaleEffect(): Modifier {
    val shader = RuntimeShader(GREYSCALE_SHADER)

    return this.graphicsLayer {
        renderEffect = RenderEffect.createShaderEffect(shader).asComposeRenderEffect()
    }
}
