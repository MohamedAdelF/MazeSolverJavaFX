# تشغيل سريع - Quick Start

## الطريقة 1: باستخدام Gradle (الأسهل)

```bash
cd javafx

# إذا لم يكن Gradle مثبت:
brew install gradle

# ثم شغل:
gradle run
```

## الطريقة 2: باستخدام Maven

```bash
cd javafx

# إذا لم يكن Maven مثبت:
brew install maven

# ثم شغل:
mvn clean javafx:run
```

## الطريقة 3: يدوياً (إذا كان JavaFX مثبت)

```bash
cd javafx

# تجميع:
javac --module-path /path/to/javafx/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d target/classes \
      src/main/java/com/mazesolver/**/*.java

# تشغيل:
java --module-path /path/to/javafx/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp target/classes \
     com.mazesolver.Main
```

## ملاحظة

إذا واجهت مشاكل، تأكد من:
1. Java 11+ مثبت
2. JavaFX SDK موجود (يتم تثبيته تلقائياً مع Maven/Gradle)

