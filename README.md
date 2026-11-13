# Plugin Studio

**Plugin Studio** is an Android-first workspace for creating and packaging plugins. Created by [@suyziss](https://github.com/suyziss).

## Current implementation

- Kotlin, Jetpack Compose, Material 3 and Android Dynamic Colors.
- Android 8.0+ (API 26), compileSdk 36.
- Focused code editor with monospace text, configurable font size and line-number gutter.
- Automatic local draft recovery.
- Undo and redo history for the current editing session.
- Search and replace all.
- Basic static checks for bracket balance, trailing spaces, TODO markers and common Python indentation mixing.
- Starter templates for a plugin, Python, JavaScript and JSON manifest.
- Project save, reopen, search and delete.
- ZIP export containing the source file and a small README.
- Text-file import through Android's document picker.
- Settings for language, extension, font size and line numbers.
- GitHub Actions for unit tests, Android Lint and debug APK builds.

## Build

Requirements: JDK 17, Android SDK 36 and Gradle 8.13.

```bash
git clone https://github.com/suyziss/Suyziss-Plugin-Studio.git
cd Suyziss-Plugin-Studio
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Current limitations

This is an early-stage editor, not yet a full IDE:
- The current editor is based on Compose BasicTextField; it does not yet provide full syntax highlighting, language-server completion, formatting engines or code execution.
- Diagnostics are lightweight text checks, not compiler-grade validation.
- Projects currently use local JSON-backed SharedPreferences storage. Room migration and a multi-file project tree are planned.
- ZIP export currently packages the active source file and README; it is not yet a full multi-directory project archive.
- Git integration, diff/history UI, advanced plugin manifest validation and language-specific tooling are not implemented yet.
- Project data is stored locally on the device; there is no cloud sync.

## Roadmap

1. Integrate a production-grade editor component with syntax highlighting, completion hooks, code folding and reliable line-number scrolling.
2. Move project persistence to Room and introduce multi-file project trees, tabs and safe rename/delete operations.
3. Add project metadata, manifest schemas, language-specific validators and configurable templates.
4. Add version snapshots, diff viewer, backup/restore and document-tree access.
5. Add Git support and optional local toolchains where Android permits.
6. Add full RU/EN localization and accessibility polish.

## GitHub Actions

The Android build workflow runs on pushes to `main`, pull requests and manual dispatch. It runs unit tests, Android Lint, builds a debug APK and uploads the APK artifact.

---

# Русская версия

**Plugin Studio** — Android-среда для создания, редактирования и упаковки плагинов. Автор: [@suyziss](https://github.com/suyziss).

## Что уже реализовано

- Kotlin, Jetpack Compose, Material 3 и системные Dynamic Colors.
- Android 8.0+ (API 26), compileSdk 36.
- Редактор с моноширинным текстом, настраиваемым размером шрифта и колонкой номеров строк.
- Автоматическое сохранение и восстановление локального черновика.
- Отмена и повтор действий в рамках текущей сессии.
- Поиск и замена всех совпадений.
- Базовая статическая проверка баланса скобок, пробелов в конце строк, TODO и некоторых проблем с отступами Python.
- Шаблоны плагина, Python, JavaScript и JSON-манифеста.
- Сохранение, повторное открытие, поиск и удаление проектов.
- Экспорт ZIP с исходным файлом и небольшим README.
- Импорт текстовых файлов через системный выбор документов Android.
- Настройки языка, расширения, размера шрифта и нумерации строк.
- GitHub Actions для unit-тестов, Android Lint и сборки debug APK.

## Сборка

Требуются JDK 17, Android SDK 36 и Gradle 8.13.

```bash
git clone https://github.com/suyziss/Suyziss-Plugin-Studio.git
cd Suyziss-Plugin-Studio
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

Debug APK создаётся в `app/build/outputs/apk/debug/app-debug.apk`.

## Текущие ограничения

Это ранняя версия редактора, а не полноценная IDE:
- Редактор пока основан на Compose BasicTextField. Полноценной подсветки синтаксиса, автодополнения через language server, форматтеров и запуска кода пока нет.
- Диагностика выполняет простые текстовые проверки, а не полноценную компиляцию.
- Проекты пока хранятся в локальном JSON внутри SharedPreferences. Переход на Room и дерево многофайловых проектов запланированы.
- ZIP содержит активный исходный файл и README, но пока не архивирует полноценную структуру каталогов.
- Git, просмотр diff и истории версий, расширенная проверка манифестов и языковые инструменты пока не реализованы.
- Данные хранятся локально; облачной синхронизации нет.

## План развития

1. Подключить полноценный редактор с подсветкой синтаксиса, автодополнением, сворачиванием блоков и синхронной прокруткой номеров строк.
2. Перевести хранение проектов на Room, добавить дерево файлов и вкладки.
3. Добавить метаданные проектов, схемы манифестов, языковые валидаторы и настраиваемые шаблоны.
4. Реализовать снимки версий, diff, резервное копирование и восстановление.
5. Добавить Git и поддерживаемые локальные инструменты разработки.
6. Завершить локализацию RU/EN и улучшить доступность интерфейса.

## GitHub Actions

Workflow сборки Android запускается при push в `main`, создании Pull Request и ручном запуске. Он выполняет unit-тесты, Android Lint, сборку debug APK и загрузку APK как artifact.
