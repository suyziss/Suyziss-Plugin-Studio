package com.suyziss.pluginstudio.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginStudioApp() {
    var title by remember { mutableStateOf("Plugin Studio") }
    var extension by remember { mutableStateOf(".plugin") }
    var language by remember { mutableStateOf("Python") }
    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Рабочее пространство", style = MaterialTheme.typography.headlineMedium)
            Text("Android IDE для создания и редактирования плагинов. Автор: @suyziss")
            OutlinedTextField(title, { title = it }, label = { Text("Название проекта") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(extension, { extension = it }, label = { Text("Произвольное расширение") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(language, { language = it }, label = { Text("Язык программирования") }, modifier = Modifier.fillMaxWidth())
            FilledTonalButton(onClick = { title = title.ifBlank { "New Plugin" } }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Создать плагин")
            }
            Text("Имя файла: " + title + extension)
            Text("Профиль языка и расширение независимы друг от друга.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
