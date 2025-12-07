# حل المشاكل - Troubleshooting

## المشكلة: ClassNotFoundException على macOS

إذا واجهت خطأ `ClassNotFoundException: com.sun.glass.ui.mac.macPlatformFactory`، فهذا يعني أن JavaFX لا يعمل بشكل صحيح مع Java 11 على macOS.

## الحلول:

### الحل 1: استخدام Java 17+ (موصى به)

```bash
# تثبيت Java 17
brew install openjdk@17

# تعيين Java 17 كافتراضي
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# ثم شغل التطبيق
cd javafx
gradle clean run
```

### الحل 2: استخدام JavaFX SDK يدوياً

1. حمّل JavaFX SDK من: https://openjfx.io/
2. استخرج الملف
3. شغّل:

```bash
cd javafx
gradle clean compileJava

java --module-path /path/to/javafx-sdk-17.0.2/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp build/classes/java/main \
     com.mazesolver.Main
```

### الحل 3: استخدام Java 21 (الأحدث)

```bash
brew install openjdk@21
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
cd javafx
gradle clean run
```

## ملاحظات:

- JavaFX 17+ يتطلب Java 11+ لكن يعمل بشكل أفضل مع Java 17+
- على macOS مع Apple Silicon، استخدم Java 17+ للحصول على أفضل أداء
- إذا استمرت المشاكل، جرب الحل 2 (JavaFX SDK يدوياً)

