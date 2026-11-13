package com.suyziss.pluginstudio.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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
                PluginProject(item.optString("name"), item.optString("code"),
                    item.optString("language", "Python"), item.optString("extension", ".plugin"),
                    item.optLong("updatedAt"))
            }.sortedByDescending { it.updatedAt }
        } catch (_: Exception) { emptyList() }
    }

    fun save(context: Context, project: PluginProject) {
        val all = listOf(project) + load(context).filterNot { it.name.equals(project.name, true) }
        val array = JSONArray()
        all.forEach { p -> array.put(JSONObject().apply {
            put("name", p.name); put("code", p.code); put("language", p.language)
            put("extension", p.extension); put("updatedAt", p.updatedAt)
        }) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }

    fun delete(context: Context, name: String) {
        val array = JSONArray()
        load(context).filterNot { it.name == name }.forEach { p -> array.put(JSONObject().apply {
            put("name", p.name); put("code", p.code); put("language", p.language)
            put("extension", p.extension); put("updatedAt", p.updatedAt)
        }) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }
}

private fun inspectCode(source: String, language: String): List<String> {
    val problems = mutableListOf<String>()
    val pairs = mapOf('(' to ')', '[' to ']', '{' to '}')
    val stack = ArrayDeque<Char>()
    source.forEachIndexed { index, char ->
        if (char in pairs.keys) stack.addLast(char)
        if (char in pairs.values) {
            val expected = pairs.entries.firstOrNull { it.value == char }?.key
            if (stack.lastOrNull() == expected) stack.removeLast()
            else problems.add("Лишняя или несогласованная закрывающая скобка (позиция ${index + 1})")
        }
    }
    if (stack.isNotEmpty()) problems.add("Не закрыты скобки: ${stack.joinToString("")}")
    source.lines().forEachIndexed { index, line ->
        if (line.trimEnd() != line) problems.add("Строка ${index + 1}: пробелы в конце")
        if (line.contains("TODO")) problems.add("Строка ${index + 1}: осталось TODO")
    }
    if (language.equals("Python", true) && source.contains("\t") && source.contains("    "))
        problems.add("Возможное смешение табуляции и пробелов")
    if (source.isBlank()) problems.add("Проект пуст")
    return problems.distinct()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginStudioApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("plugin_studio_editor", Context.MODE_PRIVATE) }
    var screen by remember { mutableStateOf(Screen.EDITOR) }
    var code by remember { mutableStateOf(prefs.getString("draft_code", "") ?: "") }
    var projectName by remember { mutableStateOf(prefs.getString("draft_name", "") ?: "") }
    var language by remember { mutableStateOf(prefs.getString("language", "Python") ?: "Python") }
    var extension by remember { mutableStateOf(prefs.getString("extension", ".plugin") ?: ".plugin") }
    var fontSize by remember { mutableFloatStateOf(prefs.getFloat("font_size", 14f)) }
    var showLineNumbers by remember { mutableStateOf(prefs.getBoolean("line_numbers", true)) }
    var projects by remember { mutableStateOf(ProjectStore.load(context)) }
    var projectFilter by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showProblems by remember { mutableStateOf(false) }
    var showDeleteName by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf(projectName) }
    var status by remember { mutableStateOf("Готово") }
    val undoStack = remember { mutableStateListOf<String>() }
    val redoStack = remember { mutableStateListOf<String>() }
    val problems = remember(code, language) { inspectCode(code, language) }

    fun updateCode(next: String) {
        if (next != code) {
            undoStack.add(code)
            if (undoStack.size > 80) undoStack.removeAt(0)
            redoStack.clear()
            code = next
        }
    }
    fun saveProject(name: String) {
        val clean = name.trim()
        if (clean.isBlank()) return
        ProjectStore.save(context, PluginProject(clean, code, language, extension, System.currentTimeMillis()))
        projectName = clean
        projects = ProjectStore.load(context)
        prefs.edit().remove("draft_code").remove("draft_name").apply()
        showSaveDialog = false
        screen = Screen.PROJECTS
        status = "Проект сохранён"
    }

    LaunchedEffect(code, projectName, language, extension) {
        delay(650)
        prefs.edit().putString("draft_code", code).putString("draft_name", projectName)
            .putString("language", language).putString("extension", extension).apply()
        status = "Черновик сохранён"
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching { context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } }
                .onSuccess { imported -> if (imported != null) { updateCode(imported); projectName = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: "Импорт"; status = "Файл импортирован" } }
                .onFailure { status = "Не удалось открыть файл" }
        }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    ZipOutputStream(stream).use { zip ->
                        val safe = (projectName.ifBlank { "PluginProject" }).replace(Regex("[^A-Za-z0-9._-]"), "_")
                        zip.putNextEntry(ZipEntry("$safe$extension"))
                        zip.write(code.toByteArray(Charsets.UTF_8)); zip.closeEntry()
                        zip.putNextEntry(ZipEntry("README.txt"))
                        zip.write("Project: $projectName\nLanguage: $language\nExtension: $extension\n".toByteArray())
                        zip.closeEntry()
                    }
                } ?: error("No output stream")
            }.onSuccess { status = "ZIP экспортирован" }.onFailure { status = "Ошибка экспорта: ${it.message}" }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text(when (screen) { Screen.EDITOR -> "Plugin Studio"; Screen.PROJECTS -> "Проекты"; Screen.SETTINGS -> "Настройки" })
                    if (screen == Screen.EDITOR) Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { if (screen != Screen.EDITOR) IconButton(onClick = { screen = Screen.EDITOR }) { Icon(Icons.Default.ArrowBack, "Назад") } },
                actions = {
                    if (screen == Screen.EDITOR) {
                        IconButton(onClick = { showSearchDialog = true }) { Icon(Icons.Default.Search, "Поиск и замена") }
                        IconButton(onClick = { showProblems = true }) { Icon(Icons.Default.BugReport, "Проверка кода") }
                        IconButton(onClick = { showTemplateDialog = true }) { Icon(Icons.Default.Code, "Шаблоны") }
                        IconButton(onClick = { importLauncher.launch(arrayOf("text/*", "application/json", "application/octet-stream")) }) { Icon(Icons.Default.FileOpen, "Импорт") }
                        IconButton(onClick = { exportLauncher.launch("${projectName.ifBlank { "PluginProject" }}.zip") }) { Icon(Icons.Default.FileDownload, "Экспорт ZIP") }
                    }
                    IconButton(onClick = { projects = ProjectStore.load(context); screen = Screen.PROJECTS }) { Icon(Icons.Default.FolderOpen, "Проекты") }
                    IconButton(onClick = { screen = Screen.SETTINGS }) { Icon(Icons.Default.Settings, "Настройки") }
                }
            )
        },
        bottomBar = {
            if (screen == Screen.EDITOR) Surface(tonalElevation = 3.dp) {
                Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(enabled = undoStack.isNotEmpty(), onClick = {
                        if (undoStack.isNotEmpty()) { redoStack.add(code); code = undoStack.removeAt(undoStack.lastIndex) }
                    }) { Icon(Icons.Default.Undo, "Отменить") }
                    IconButton(enabled = redoStack.isNotEmpty(), onClick = {
                        if (redoStack.isNotEmpty()) { undoStack.add(code); code = redoStack.removeAt(redoStack.lastIndex) }
                    }) { Icon(Icons.Default.Redo, "Повторить") }
                    Text("${code.lines().size} строк", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                    Button(onClick = { nameInput = projectName; showSaveDialog = true }, enabled = code.isNotBlank()) {
                        Icon(Icons.Default.Save, null); Spacer(Modifier.width(6.dp)); Text("Завершить")
                    }
                }
            }
        }
    ) { padding ->
        when (screen) {
            Screen.EDITOR -> Column(Modifier.fillMaxSize().padding(padding)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(projectName.ifBlank { "Новый файл" } + extension, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.weight(1f))
                    Text(language, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                }
                HorizontalDivider()
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    if (showLineNumbers) {
                        val count = code.lines().size.coerceAtLeast(1)
                        Column(Modifier.width(42.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .35f)).padding(top = 10.dp),
                            horizontalAlignment = Alignment.End) {
                            (1..count).forEach { Text(it.toString(), modifier = Modifier.padding(end = 8.dp), fontSize = fontSize.sp,
                                lineHeight = (fontSize + 7).sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)) }
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxHeight().padding(12.dp)) {
                        if (code.isEmpty()) Text("Начните писать код…", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        BasicTextField(value = code, onValueChange = ::updateCode, modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = fontSize.sp,
                                fontFamily = FontFamily.Monospace, lineHeight = (fontSize + 7).sp),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary))
                    }
                }
                if (problems.isNotEmpty()) Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp)); Text("${problems.size} замечаний · нажмите для просмотра", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.weight(1f)); TextButton(onClick = { showProblems = true }) { Text("Проверить") }
                }
            }
            Screen.PROJECTS -> Column(Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(projectFilter, { projectFilter = it }, Modifier.fillMaxWidth().padding(12.dp),
                    label = { Text("Поиск проектов") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
                val filtered = projects.filter { it.name.contains(projectFilter, true) || it.language.contains(projectFilter, true) }
                if (filtered.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Проекты не найдены") }
                else LazyColumn {
                    items(filtered, key = { it.name }) { project ->
                        ListItem(headlineContent = { Text(project.name) },
                            supportingContent = { Text("${project.language} · ${project.extension}") },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(onClick = {
                                        projectName = project.name; code = project.code; language = project.language
                                        extension = project.extension; undoStack.clear(); redoStack.clear(); screen = Screen.EDITOR
                                    }) { Text("Открыть") }
                                    IconButton(onClick = { showDeleteName = project.name }) { Icon(Icons.Default.Delete, "Удалить проект") }
                                }
                            })
                        HorizontalDivider()
                    }
                }
                Button(onClick = { projectName = ""; code = ""; undoStack.clear(); redoStack.clear(); screen = Screen.EDITOR },
                    modifier = Modifier.fillMaxWidth().padding(12.dp)) { Icon(Icons.Default.Add, null); Text("Новый проект") }
            }
            Screen.SETTINGS -> Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Параметры редактора", style = MaterialTheme.typography.titleLarge)
                var selectedLanguage by remember(language) { mutableStateOf(language) }
                var selectedExtension by remember(extension) { mutableStateOf(extension) }
                OutlinedTextField(selectedLanguage, { selectedLanguage = it }, label = { Text("Язык программирования") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(selectedExtension, { selectedExtension = it }, label = { Text("Расширение файла") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Text("Размер текста: ${fontSize.toInt()} sp")
                Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 11f..24f)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Нумерация строк", modifier = Modifier.weight(1f))
                    Switch(showLineNumbers, { showLineNumbers = it })
                }
                Button(onClick = {
                    language = selectedLanguage.ifBlank { "Text" }
                    val ext = selectedExtension.trim()
                    extension = when { ext.isBlank() -> ".plugin"; ext.startsWith(".") -> ext; else -> ".$ext" }
                    prefs.edit().putString("language", language).putString("extension", extension)
                        .putFloat("font_size", fontSize).putBoolean("line_numbers", showLineNumbers).apply()
                    screen = Screen.EDITOR
                }, modifier = Modifier.fillMaxWidth()) { Text("Сохранить настройки") }
                Text("Черновик сохраняется автоматически. Настройки редактора хранятся на устройстве.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (showSaveDialog) AlertDialog(onDismissRequest = { showSaveDialog = false }, title = { Text("Сохранить проект") },
        text = { OutlinedTextField(nameInput, { nameInput = it }, label = { Text("Имя проекта") }, singleLine = true) },
        confirmButton = { TextButton(onClick = { saveProject(nameInput) }, enabled = nameInput.isNotBlank()) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("Отмена") } })

    if (showSearchDialog) AlertDialog(onDismissRequest = { showSearchDialog = false }, title = { Text("Поиск и замена") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(searchText, { searchText = it }, label = { Text("Найти") }, singleLine = true)
            OutlinedTextField(replaceText, { replaceText = it }, label = { Text("Заменить на") }, singleLine = true)
            Text("Совпадений: ${if (searchText.isBlank()) 0 else Regex(Regex.escape(searchText)).findAll(code).count()}")
        } },
        confirmButton = { TextButton(onClick = { if (searchText.isNotEmpty()) updateCode(code.replace(searchText, replaceText)); showSearchDialog = false }) { Text("Заменить всё") } },
        dismissButton = { TextButton(onClick = { showSearchDialog = false }) { Text("Закрыть") } })

    if (showTemplateDialog) AlertDialog(onDismissRequest = { showTemplateDialog = false }, title = { Text("Шаблоны кода") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Пустой плагин" to "# Plugin name\n# Author: @username\n\ndef on_load():\n    print(\"Plugin loaded\")\n",
                "Python" to "def main():\n    print(\"Hello, Plugin Studio!\")\n\nif __name__ == \"__main__\":\n    main()\n",
                "JavaScript" to "function main() {\n  console.log('Hello, Plugin Studio!');\n}\n\nmain();\n",
                "JSON manifest" to "{\n  \"name\": \"My Plugin\",\n  \"version\": \"1.0.0\",\n  \"author\": \"@username\",\n  \"description\": \"\"\n}\n"
            ).forEach { (label, template) -> TextButton(onClick = { updateCode(template); showTemplateDialog = false }) { Text(label) } }
        } },
        confirmButton = { TextButton(onClick = { showTemplateDialog = false }) { Text("Закрыть") } })

    if (showProblems) AlertDialog(onDismissRequest = { showProblems = false }, title = { Text("Проверка кода") },
        text = { if (problems.isEmpty()) Text("Простые проверки не нашли замечаний.") else Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
            problems.forEach { Text("• $it", modifier = Modifier.padding(vertical = 4.dp)) }
            Text("Это базовая статическая проверка, а не полноценный запуск или анализатор языка.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } },
        confirmButton = { TextButton(onClick = { showProblems = false }) { Text("Готово") } })

    showDeleteName?.let { target -> AlertDialog(onDismissRequest = { showDeleteName = null },
        title = { Text("Удалить проект?") }, text = { Text("Проект «$target» будет удалён с устройства.") },
        confirmButton = { TextButton(onClick = { ProjectStore.delete(context, target); projects = ProjectStore.load(context); showDeleteName = null }) { Text("Удалить") } },
        dismissButton = { TextButton(onClick = { showDeleteName = null }) { Text("Отмена") } }) }
}
