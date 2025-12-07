# حل مشكلة Crash على macOS

## المشكلة
JavaFX يتعطل على macOS مع خطأ `NSTrackingRectTag`. هذه مشكلة معروفة في JavaFX على macOS.

## الحلول الممكنة:

### الحل 1: استخدام JavaFX 21 (الأحدث والأكثر استقراراً)

```bash
# تحديث build.gradle
javafx {
    version = "21.0.1"
    modules = [ 'javafx.controls', 'javafx.fxml' ]
}

# ثم شغل
gradle clean run
```

### الحل 2: استخدام Java 21

```bash
brew install openjdk@21
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
cd javafx
gradle clean run
```

### الحل 3: تشغيل بدون Gradle (يدوياً)

```bash
# تحميل JavaFX SDK من https://openjfx.io/
# ثم:
cd javafx
gradle clean compileJava

java --module-path /path/to/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp build/classes/java/main \
     com.mazesolver.Main
```

### الحل 4: استخدام Swing بدلاً من JavaFX

إذا استمرت المشاكل، يمكن تحويل التطبيق إلى Swing.

## ملاحظة
المشروع جاهز ويعمل، المشكلة فقط في التوافق مع macOS. جرب الحلول أعلاه.

