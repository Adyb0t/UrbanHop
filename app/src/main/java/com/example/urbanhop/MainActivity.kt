package com.example.urbanhop

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.urbanhop.pages.Map
import com.example.urbanhop.ui.theme.UrbanHopTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint(
        "UnusedMaterial3ScaffoldPaddingParameter",
        "UnusedContentLambdaTargetStateParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanHopTheme {
                var selectedPage by rememberSaveable {
                    mutableStateOf(PageMarker.Home)
                }
                val contentPadding = WindowInsets.systemBars.asPaddingValues()
                val backdrop = rememberLayerBackdrop()
                val textFieldState = rememberTextFieldState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box {
                        Map(contentPadding, backdrop)
                    }
                    Box (
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        var expanded by rememberSaveable { mutableStateOf(false) }
                        SearchBar(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth(),
                            inputField = {
                                Box(Modifier.fillMaxSize())
                                SearchBarDefaults.InputField(
                                    query = textFieldState.text.toString(),
                                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                                    onSearch = {
                                        onSearch(textFieldState.text.toString())
                                        expanded = false
                                    },
                                    expanded = expanded,
                                    onExpandedChange = { expanded = it },
                                    placeholder = { Text("Search") }
                                )
                            },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        ){

                        }
                        NavBar(
                            Modifier.align(Alignment.BottomCenter),
                            selectedPage,
                            backdrop
                        ) { selectedPage = it }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun Preview() {
    UrbanHopTheme {

    }
}

fun onSearch(search: String){

}