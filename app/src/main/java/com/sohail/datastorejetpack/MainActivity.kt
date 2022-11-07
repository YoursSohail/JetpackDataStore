package com.sohail.datastorejetpack

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sohail.datastorejetpack.data.SettingsDataManager
import com.sohail.datastorejetpack.data.SquareSettings
import com.sohail.datastorejetpack.ui.theme.DataStoreJetpackTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var settingsDataManager: SettingsDataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsDataManager = SettingsDataManager(this)

        val settingsSaved = MutableLiveData<SquareSettings>()

        GlobalScope.launch {
            settingsDataManager.getSettings().catch { e ->
                e.printStackTrace()
            }.collect {
                settingsSaved.postValue(
                    it
                )
            }
        }

        setContent {
            DataStoreJetpackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SettingsInput(this, settingsDataManager, settingsSaved)
                }
            }
        }
    }
}

@Composable
fun SettingsInput(
    context: MainActivity,
    settingsDataManager: SettingsDataManager,
    settingsSaved: MutableLiveData<SquareSettings>
) {
    var colorState by remember { mutableStateOf(TextFieldValue()) }
    var sizeState by remember { mutableStateOf(TextFieldValue()) }
    var thicknessState by remember { mutableStateOf(TextFieldValue()) }

    var colorSavedState by remember {
        mutableStateOf("FF0000")
    }

    var sizeSavedState by remember {
        mutableStateOf(0)
    }

    var thicknessSavedState by remember {
        mutableStateOf(0)
    }

    settingsSaved.observe(context, Observer {
        Log.d("SETTINGS_TAG", it.toString())
        colorSavedState = it.color
        sizeSavedState = it.size
        thicknessSavedState = it.thickness
    })


    Column(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = colorState,
            onValueChange = { colorState = it },
            label = { Text(text = "Square color") },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )
        TextField(
            value = sizeState,
            onValueChange = { sizeState = it },
            label = { Text(text = "Square size") },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = thicknessState,
            onValueChange = { thicknessState = it },
            label = { Text(text = "Square thickness") },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            GlobalScope.launch(Dispatchers.IO) {
                settingsDataManager.saveSettings(
                    SquareSettings(
                        color = colorState.text,
                        size = sizeState.text.toInt(),
                        thickness = thicknessState.text.toInt()
                    )
                )
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Save")
        }

        Card(
            modifier = Modifier
                .width(sizeSavedState.dp)
                .height(sizeSavedState.dp),
            elevation = 10.dp,
            border = BorderStroke(
                width = thicknessSavedState.dp,
                color = Color.Black
            ),
            backgroundColor = Color(color = android.graphics.Color.parseColor("#${colorSavedState}"))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "#${colorSavedState}")
                Text(text = sizeSavedState.toString())
                Text(text = thicknessSavedState.toString())
            }
        }

    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DataStoreJetpackTheme {
        Greeting("Android")
    }
}
