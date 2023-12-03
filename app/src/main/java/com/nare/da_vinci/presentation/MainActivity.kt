/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.nare.da_vinci.presentation

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.nare.da_vinci.R
import com.nare.da_vinci.presentation.theme.Da_VinciTheme

class MainActivity : ComponentActivity(), bodySensing.Lit {
    val length = 20
    val Accx = mutableStateOf(0f)
    val Accy = mutableStateOf(0f)
    val Accz = mutableStateOf(0f)
    val Gyrox = mutableStateOf(0f)
    val Gyroy = mutableStateOf(0f)
    val Gyroz = mutableStateOf(0f)
    val Ax = mutableStateOf(arrayListOf<Float>())
    val Ay = mutableStateOf(arrayListOf<Float>())
    val Az = mutableStateOf(arrayListOf<Float>())
    val count = mutableStateOf(0f)
    val modes = mutableStateOf("Nomal")
    private lateinit var mbodySensing: bodySensing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mbodySensing = bodySensing(this)
        setContent {
            WearApp()
        }
    }

    @Composable
    fun WearApp() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier
                    .fillMaxSize(), onDraw = {
                val w = size.width
                val h = size.height
                var array = Ax.value.nomali()
                val len = w/array.size
                val bias = h*0.25f
                if(array.size >= 2) {
                    array.forEachIndexed { index, fl ->
                        Log.e("a", index.toString())
                        if(index != array.size-1) drawLine(Color.Red, Offset(len*index, (h*0.5f)*fl+bias), Offset(len*(index+1), (h*0.5f)*array[index+1]+bias), 5f)
                    }
                }
                array = Ay.value.nomali()
                if(array.size >= 2) {
                    array.forEachIndexed { index, fl ->
                        Log.e("a", index.toString())
                        if(index != array.size-1) drawLine(Color.Green, Offset(len*index, (h*0.5f)*fl+bias), Offset(len*(index+1), (h*0.5f)*array[index+1]+bias), 5f)
                    }
                }

                array = Az.value.nomali()
                if(array.size >= 2) {
                    array.forEachIndexed { index, fl ->
                        Log.e("a", index.toString())
                        if(index != array.size-1) drawLine(Color.Blue, Offset(len*index, (h*0.5f)*fl+bias), Offset(len*(index+1), (h*0.5f)*array[index+1]+bias), 5f)
                    }
                }
            })

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Accx.value = 0f
                        Accy.value = 0f
                        Accz.value = 0f

                        Gyrox.value = 0f
                        Gyroy.value = 0f
                        Gyroz.value = 0f
                    },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = "Acc x:${Accx.value}\n" +
                        "y:${Accy.value}\n" +
                        "z:${Accz.value}\n"
//                        "${modes.value}\n" +
//                        "${count.value}\n"
            )
        }
    }


    private fun ArrayList<Float>.nomali(): FloatArray {
        val array = this.toFloatArray()
        val max = array.max()
        val min = array.min()
        this.forEachIndexed { index, fl ->
            val it = fl - min
            array[index] = it / (max-min)
        }
        return array
    }


    override fun onStart() {
        super.onStart()
        mbodySensing.start(this)
    }

    override fun onStop() {
        super.onStop()
        mbodySensing.stop()
    }

    override fun onJescher(Motion: String) {
        this.modes.value = Motion
    }
    override fun onAccChager(Xs: Float, Ys: Float, Zs: Float) {
//        this.Accx.value = if(Accx.value > Xs) Xs else Accx.value
//        this.Accy.value = if(Accy.value > Ys) Ys else Accy.value
//        this.Accz.value = if(Accz.value > Zs) Zs else Accz.value
        this.Accx.value = Xs
        this.Accy.value = Ys
        this.Accz.value = Zs

        this.Ax.value.add(Accx.value)
        this.Ay.value.add(Accy.value)
        this.Az.value.add(Accz.value)

        Log.e("Nomalx", Ax.value.nomali().toList().toString())
//        Log.e("Nomaly", Ay.nomali().toList().toString())
//        Log.e("Nomalz", Az.nomali().toList().toString())

        if(this.Ax.value.size > length) this.Ax.value.removeAt(0)
        if(this.Ay.value.size > length) this.Ay.value.removeAt(0)
        if(this.Az.value.size > length) this.Az.value.removeAt(0)

    }

    override fun onGyroChager(Xs: Float, Ys: Float, Zs: Float) {
//        this.Gyrox.value = if(Gyrox.value < Xs) Xs else Gyrox.value
//        this.Gyroy.value = if(Gyroy.value < Ys) Ys else Gyroy.value
//        this.Gyroz.value = if(Gyroz.value < Zs) Zs else Gyroz.value
//
//        this.Gyrox.value = kotlin.math.round(Xs*(180f/kotlin.math.PI).toFloat()*10)/10f
//        this.Gyroy.value = Ys
//        this.Gyroz.value = Zs
    }
}

//@Composable
//fun WearApp(greetingName: String) {
//    Da_VinciTheme {
//        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
//         * version of LazyColumn for wear devices with some added features. For more information,
//         * see d.android.com/wear/compose.
//         */
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Greeting(greetingName = greetingName)
//        }
//    }
//}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    Da_VinciTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Greeting(greetingName = "")
        }
    }
}