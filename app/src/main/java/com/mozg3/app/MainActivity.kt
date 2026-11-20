package com.mozg3.app

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MozgApp() }
    }
}

private enum class Screen { WELCOME, HOME, SETTINGS, PIN_GATE, TASKS, HELP }

@Composable
private fun MozgApp() {
    val context = LocalContext.current
    val prefs = remember(context) { AppPrefs(context) }
    var screen by remember { mutableStateOf(if (prefs.name.isBlank()) Screen.WELCOME else Screen.TASKS) }
    var name by remember { mutableStateOf(prefs.name) }
    var pin by remember { mutableStateOf("") }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var locked by remember { mutableStateOf(false) }
    var interval by remember { mutableStateOf(30f) }
    var required by remember { mutableStateOf(10f) }
    var age by remember { mutableStateOf(8f) }
    var backgroundIndex by remember { mutableStateOf(prefs.backgroundIndex) }
    var mathEnabled by remember { mutableStateOf(true) }
    var russianEnabled by remember { mutableStateOf(true) }
    var readingEnabled by remember { mutableStateOf(true) }
    var logicEnabled by remember { mutableStateOf(true) }
    val backgrounds = listOf(Color(0xFFF4FBFA), Color(0xFFFFF3E8), Color(0xFFEAF2FF), Color(0xFFF6EEFF), Color(0xFFEAF8EF))
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF3A3A3A),
            onPrimary = Color.White,
            secondary = Color(0xFF555555),
            onSecondary = Color.White,
            tertiary = Color(0xFF666666),
            onTertiary = Color.White
        )
    ) {
        Column(Modifier.fillMaxSize().background(backgrounds[backgroundIndex]).padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            when (screen) {
                Screen.WELCOME -> {
                    Text("Добро пожаловать", style = MaterialTheme.typography.headlineLarge)
                    Text("МОЗГ создан для детей и родителей. Он помогает тренировать математику и русский язык перед доступом к телефону.")
                    OutlinedTextField(name, { name = normalizeName(it) }, label = { Text("Имя ребёнка") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(pin, { pin = it.filter(Char::isDigit).take(12) }, label = { Text("PIN родителя, необязательно") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    RaisedButton("Сохранить и открыть настройки", enabled = name.trim().length >= 2) { prefs.name = name.trim(); screen = Screen.SETTINGS }
                }
                Screen.HOME -> TaskScreen(name = name, onSettings = { screen = if (pin.isBlank()) Screen.SETTINGS else Screen.PIN_GATE }, onHelp = { screen = Screen.HELP })
                Screen.SETTINGS -> {
                    Text("Настройки", style = MaterialTheme.typography.headlineLarge)
                    OutlinedTextField(name, { name = normalizeName(it); prefs.name = name }, label = { Text("Имя ребёнка") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Text("Возраст: ${age.toInt()} лет")
                    Slider(value = age, onValueChange = { age = it }, valueRange = 5f..16f, steps = 10)
                    Text("Цвет фона")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        backgrounds.forEachIndexed { index, color ->
                            Button(
                                onClick = { backgroundIndex = index; prefs.backgroundIndex = index },
                                modifier = Modifier.height(44.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = color,
                                    contentColor = Color.Black
                                )
                            ) { Text("●") }
                        }
                    }
                    Text("УПРАЖНЕНИЯ", style = MaterialTheme.typography.titleLarge)
                    ExerciseSwitch("Русский", russianEnabled) { russianEnabled = it }
                    ExerciseSwitch("Математика", mathEnabled) { mathEnabled = it }
                    ExerciseSwitch("Чтение", readingEnabled) { readingEnabled = it }
                    ExerciseSwitch("Логика", logicEnabled) { logicEnabled = it }
                    Text("БЛОКИРОВКА ЭКРАНА", style = MaterialTheme.typography.titleLarge)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Включить блокировку"); Switch(locked, { locked = it }) }
                    if (locked) { Text("Запускать каждые: ${interval.toInt()} мин"); Slider(value = interval, onValueChange = { interval = it }, valueRange = 5f..60f, steps = 10); Text("Выполнить ${required.toInt()} заданий для разблокировки"); Slider(value = required, onValueChange = { required = it }, valueRange = 1f..20f, steps = 18) }
                    Text("PIN РОДИТЕЛЯ", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(newPin, { newPin = it.filter(Char::isDigit).take(12) }, label = { Text("Новый PIN") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    RaisedButton("Изменить PIN", enabled = newPin.length >= 4) { pin = newPin; newPin = "" }
                    RaisedButton("Сохранить настройки") { prefs.name = name.trim(); screen = Screen.TASKS }
                }
                Screen.PIN_GATE -> {
                    Text("PIN родителя", style = MaterialTheme.typography.headlineLarge)
                    Text("Введите PIN, чтобы открыть настройки.")
                    OutlinedTextField(enteredPin, { enteredPin = it.filter(Char::isDigit).take(12) }, label = { Text("PIN") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    if (pinError.isNotBlank()) Text(pinError, color = Color.Red)
                    RaisedButton("Открыть настройки", enabled = enteredPin.length >= 4) { if (enteredPin == pin) { enteredPin = ""; pinError = ""; screen = Screen.SETTINGS } else pinError = "Неверный PIN" }
                }
                Screen.TASKS -> TaskScreen(name = name, onSettings = { screen = if (pin.isBlank()) Screen.SETTINGS else Screen.PIN_GATE }, onHelp = { screen = Screen.HELP })
                Screen.HELP -> { Text("Справка", style = MaterialTheme.typography.headlineLarge); Text("Версия 2.0"); Text("Дата сборки: 21.11.2026"); Text("В ручном режиме задания запускаются по желанию и не ограничиваются расписанием."); RaisedButton("Назад") { screen = Screen.TASKS } }
            }
        }
    }
}

@Composable
private fun ExerciseSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Switch(checked, onChange) }
}

@Composable
private fun TaskScreen(name: String, onSettings: () -> Unit, onHelp: () -> Unit) {
    var task by remember { mutableStateOf(TaskData.random()) }; var answer by remember { mutableStateOf("") }; var correct by remember { mutableStateOf(0) }; var wrong by remember { mutableStateOf(0) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { RaisedButton("⚙", modifier = Modifier.weight(1f), onClick = onSettings); RaisedButton("?", modifier = Modifier.weight(1f), onClick = onHelp) }
    Text("Задание для ${name.ifBlank { "ребёнка" }}", style = MaterialTheme.typography.titleMedium)
    Text(task.question, style = MaterialTheme.typography.displayMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    OutlinedTextField(answer, { answer = it }, label = { Text("Ответ") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    RaisedButton("Проверить") { if (answer.trim().equals(task.expected, ignoreCase = true)) correct++ else wrong++; answer = ""; task = TaskData.random() }
    Text("Верных: $correct   Ошибок: $wrong", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

private data class TaskData(val question: String, val expected: String) {
    companion object {
        fun random(): TaskData = when (Random.nextInt(3)) {
            0 -> { val a = Random.nextInt(1, 20); val b = Random.nextInt(1, 20); TaskData("$a + $b = ?", (a + b).toString()) }
            1 -> TaskData("Вставьте букву: м_ма", "а")
            else -> TaskData("Что лишнее: 2, 4, 7?", "7")
        }
    }
}

@Composable
private fun RaisedButton(text: String, enabled: Boolean = true, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp).shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) { Text(text) }
}

private class AppPrefs(context: Context) {
    private val store = context.getSharedPreferences("mozg3", Context.MODE_PRIVATE)
    var name: String
        get() = store.getString("name", "") ?: ""
        set(value) { store.edit().putString("name", value).apply() }
    var backgroundIndex: Int
        get() = store.getInt("background", 2).coerceIn(0, 4)
        set(value) { store.edit().putInt("background", value).apply() }
}

private fun normalizeName(value: String): String = value.take(24).replaceFirstChar { first ->
    if (first.isLowerCase()) first.titlecase() else first.toString()
}
