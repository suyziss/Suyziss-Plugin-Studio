# Plugin Studio

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

## 📱 Статус

**Проект находится в активной разработке.**

Текущая версия является начальной реализацией Plugin Studio, а не завершённой IDE. Сборка на реальном устройстве и эмуляторе пока не подтверждена, поэтому часть запланированных функций ещё находится в разработке.

## 📄 Лицензия

Лицензия будет добавлена в отдельном обновлении репозитория.
