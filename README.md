# Plugin Studio

**Plugin Studio** — мобильная Android IDE для создания и редактирования плагинов.  
Автор: **[@suyziss](https://github.com/suyziss)**.

## Текущая реализация

- Kotlin, Jetpack Compose и Material 3 с системными Dynamic Colors.
- Android 8.0+ (API 26), compileSdk 36.
- Создание проекта с названием, автором, версией, описанием, языком и расширением.
- Произвольные расширения файлов: например `.plugin`, `.myscript`, `.py`.
- Расширение файла не определяет язык программирования.
- Базовый экран редактора и стартовая панель.
- Подготовлены зависимости Room, DataStore и Coroutines.
- Unit-тесты правил расширений.
- GitHub Actions workflow для тестов, lint и debug APK artifact.

## Сборка

Требуется JDK 17, Android SDK 36 и Gradle 8.13.

```bash
git clone https://github.com/suyziss/Suyziss-Plugin-Studio.git
cd Suyziss-Plugin-Studio
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

APK после сборки: `app/build/outputs/apk/debug/app-debug.apk`.

## GitHub Actions

Workflow `.github/workflows/android-build.yml` запускается при push в `main`, Pull Request и вручную. Он выполняет unit-тесты, Android Lint, собирает debug APK и загружает APK как Actions Artifact.

Workflow `.github/workflows/android-release.yml` запускается по тегам `v*`. Пока в Gradle не добавлена реальная signing-конфигурация, workflow не публикует неподписанный APK как релиз.

## План следующих этапов

- Полноценный редактор CodeMirror с локально встроенными ресурсами.
- Нумерация строк, поиск/замена, вкладки и форматирование.
- Полная модель проектов и профили расширений в Room.
- SAF import/export и выбор каталога.
- Автосохранение, история версий, резервные копии и Diff Viewer.
- Библиотека шаблонов, Problems и Command Palette.
- README generator и GitHub Device Flow.
- Локализация RU/EN и настройки темы.

## Статус

Это начальная версия репозитория, а не завершённая IDE. Сборка на реальном устройстве и эмуляторе пока не подтверждена. Не все перечисленные в первоначальном ТЗ функции реализованы.
