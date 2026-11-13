package com.suyziss.pluginstudio.ui

import android.content.Context
import android.graphics.Typeface
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

private data class PluginProject(
    val name: String,
    val code: String,
    val language: String,
    val extension: String,
    val updatedAt: Long
)

private enum class Screen { EDITOR, PROJECTS, SETTINGS }

private object ProjectStore {
    private const val PREFS = "plugin_studio_projects"
    private const val KEY = "items"

    fun load(context: Context): List<PluginProject> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                PluginProject(
                    name = item.optString("name"),
                    code = item.optString("code"),
                    language = item.optString("language", "Python"),
                    extension = item.optString("extension", ".plugin"),
                    updatedAt = item.optLong("updatedAt")
                )
            }.sortedByDescending { it.updatedAt }
        } catch (_: Exception) { emptyList() }
    }

    fun save(context: Context, project: PluginProject) {
        val current = load(context).filterNot { it.name.equals(project.name, ignoreCase = true) }
        val all = listOf(project) + current
        val array = JSONArray()
        all.forEach {
            array.put(JSONObject().apply {
                put("name", it.name)
                put("code", it.code)
                put("language", it.language)
                put("extension", it.extension)
                put("updatedAt", it.updatedAt)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginStudioApp() {
    val context = LocalContext.current
    var screen by remember { mutableStateOf(Screen.EDITOR) }
    var code by remember { mutableStateOf("") }
    var projectName by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("Python") }
    var extension by remember { mutableStateOf(".plugin") }
    var projects by remember { mutableStateOf(ProjectStore.load(context)) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

    fun saveProject(name: String) {
        val cleanName = name.trim()
        if (cleanName.isNotEmpty()) {
            ProjectStore.save(context, PluginProject(cleanName, code, language, extension, System.currentTimeMillis()))
            projects = ProjectStore.load(context)
            projectName = cleanName
            showSaveDialog = false
            screen = Screen.PROJECTS
            showSavedMessage = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(when (screen) {
                    Screen.EDITOR -> "Редактор"
                    Screen.PROJECTS -> "Проекты"
                    Screen.SETTINGS -> "Настройки"
                }) },
                navigationIcon = {
                    if (screen != Screen.EDITOR) {
                        IconButton(onClick = { screen = Screen.EDITOR }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { projects = ProjectStore.load(context); screen = Screen.PROJECTS }) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Проекты")
                    }
                    IconButton(onClick = { screen = Screen.SETTINGS }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        },
        bottomBar = {
            if (screen == Screen.EDITOR) {
                Surface(tonalElevation = 2.dp) {
                    Row(
                        Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { showSaveDialog = true }, enabled = code.isNotBlank()) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Завершить и сохранить")
                        }
                    }
                }
            }
        }
    ) { padding ->
        when (screen) {
            Screen.EDITOR -> {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = projectName.ifBlank { "Новый файл" } + extension,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.weight(1f))
                        Text(language, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider()
                    Box(Modifier.fillMaxSize().padding(12.dp)) {
                        if (code.isEmpty()) {
                            Text(
                                "Начните писать код…",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                        BasicTextField(
                            value = code,
                            onValueChange = { code = it },
                            modifier = Modifier.fillMaxSize(),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 21.sp
                            ),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
            Screen.PROJECTS -> {
                if (projects.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("Сохранённых проектов пока нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp)) {
                        items(projects, key = { it.name }) { project ->
                            ListItem(
                                headlineContent = { Text(project.name) },
                                supportingContent = { Text("${project.language} · ${project.extension}") },
                                trailingContent = { TextButton(onClick = {
                                    projectName = project.name
                                    code = project.code
                                    language = project.language
                                    extension = project.extension
                                    screen = Screen.EDITOR
                                }) { Text("Открыть") }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
            Screen.SETTINGS -> {
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Параметры новых файлов", style = MaterialTheme.typography.titleLarge)
                    var selectedLanguage by remember(language) { mutableStateOf(language) }
                    var selectedExtension by remember(extension) { mutableStateOf(extension) }
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = { selectedLanguage = it },
                        label = { Text("Язык программирования") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = selectedExtension,
                        onValueChange = { selectedExtension = it },
                        label = { Text("Расширение файла") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            language = selectedLanguage.ifBlank { "Text" }
                            val ext = selectedExtension.trim()
                            extension = when {
                                ext.isBlank() -> ".plugin"
                                ext.startsWith(".") -> ext
                                else -> ".$ext"
                            }
                            screen = Screen.EDITOR
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Сохранить настройки") }
                    Text(
                        "Настройки применяются к текущему редактированию и новым файлам. Содержимое проекта сохраняется отдельно.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Сохранить проект") },
            text = {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Имя проекта") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { saveProject(projectName) }, enabled = projectName.isNotBlank()) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Отмена") }
            }
        )
    }
    if (showSavedMessage) {
        LaunchedEffect(showSavedMessage) {
            kotlinx.coroutines.delay(1800)
            showSavedMessage = false
        }
        SnackbarHost(hostState = remember { SnackbarHostState() })
    }
}
