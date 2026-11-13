# Plugin Studio

**Plugin Studio** is a mobile Android IDE for creating and editing plugins.  
Created by **[@suyziss](https://github.com/suyziss)**.

## ✨ Current Implementation

- Kotlin, Jetpack Compose and Material 3 with system Dynamic Colors.
- Android 8.0+ (API 26), compileSdk 36.
- Project creation with name, author, version, description, language and file extension.
- Custom file extensions such as `.plugin`, `.myscript` and `.py`.
- File extensions do not determine the programming language.
- Basic editor screen and start panel.
- Room, DataStore and Coroutines dependencies prepared.
- Unit tests for extension rules.
- GitHub Actions for tests, Android Lint and debug APK builds.

## 🛠 Build

Requirements: **JDK 17**, **Android SDK 36** and **Gradle 8.13**.

```bash
git clone https://github.com/suyziss/Suyziss-Plugin-Studio.git
cd Suyziss-Plugin-Studio

gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

After a successful build, the APK is located at:

`app/build/outputs/apk/debug/app-debug.apk`

## 🤖 GitHub Actions

The `.github/workflows/android-build.yml` workflow runs on:

- pushes to `main`;
- Pull Requests;
- manual GitHub Actions runs.

It performs:

1. Unit tests.
2. Android Lint.
3. Debug APK build.
4. Debug APK artifact upload.
5. Build report preservation.

The `.github/workflows/android-release.yml` workflow runs on `v*` tags. Until proper Gradle signing configuration is added, it does not publish an unsigned APK as a release.

## 🚀 Roadmap

- Full CodeMirror editor with locally bundled resources.
- Line numbers, search and replace, tabs and formatting.
- Complete project model and extension profiles in Room.
- SAF import/export and directory selection.
- Autosave.
- Version history and backups.
- Diff Viewer.
- Template library.
- Problems and Command Palette.
- README Generator.
- GitHub Device Flow.
- RU/EN localization.
- Theme settings.
- **Future Windows and Linux versions.**

## 📱 Status

**Plugin Studio is actively developed.**

The current version is an early implementation, not a finished IDE. Some planned features are still under development.

---

# Русская версия

**Plugin Studio** — мобильная Android IDE для создания и редактирования плагинов.  
Автор: **[@suyziss](https://github.com/suyziss)**.

## ✨ Текущая реализация

- Kotlin, Jetpack Compose и Material 3 с системными Dynamic Colors.
- Android 8.0+ (API 26), compileSdk 36.
- Создание проекта с названием, автором, версией, описанием, языком и расширением.
- Произвольные расширения файлов: например `.plugin`, `.myscript`, `.py`.
- Расширение файла не определяет язык программирования.
- Базовый экран редактора и стартовая панель.
- Подготовлены зависимости Room, DataStore и Coroutines.
- Unit-тесты правил расширений.
- GitHub Actions для тестов, Android Lint и сборки debug APK.

## 🛠 Сборка

Требуется **JDK 17**, **Android SDK 36** и **Gradle 8.13**.

```bash
git clone https://github.com/suyziss/Suyziss-Plugin-Studio.git
cd Suyziss-Plugin-Studio

gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

После успешной сборки APK находится здесь:

`app/build/outputs/apk/debug/app-debug.apk`

## 🤖 GitHub Actions

Workflow `.github/workflows/android-build.yml` запускается при:

- push в `main`;
- создании Pull Request;
- ручном запуске через GitHub Actions.

Workflow выполняет:

1. Unit-тесты.
2. Android Lint.
3. Сборку debug APK.
4. Загрузку APK как GitHub Actions Artifact.
5. Сохранение build reports.

Workflow `.github/workflows/android-release.yml` запускается по тегам `v*`. Пока в Gradle не добавлена полноценная signing-конфигурация, workflow не публикует неподписанный APK как релиз.

## 🚀 План развития

- Полноценный редактор CodeMirror с локально встроенными ресурсами.
- Нумерация строк, поиск и замена, вкладки и форматирование.
- Полная модель проектов и профили расширений в Room.
- SAF import/export и выбор каталога.
- Автосохранение.
- История версий и резервные копии.
- Diff Viewer.
- Библиотека шаблонов.
- Problems и Command Palette.
- README Generator.
- GitHub Device Flow.
- Локализация RU/EN.
- Настройки темы.
- **В будущем планируется версия для Windows и Linux.**

## 📱 Статус

**Проект находится в активной разработке.**

Текущая версия является начальной реализацией Plugin Studio, а не завершённой IDE. Часть запланированных функций ещё находится в разработке.

## 📄 Лицензия

Лицензия будет добавлена в отдельном обновлении репозитория.
