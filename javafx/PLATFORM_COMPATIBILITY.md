# توافق المنصات - Platform Compatibility

## ✅ Windows (ممتاز - Excellent)

**الحالة**: يعمل بشكل ممتاز بدون أي مشاكل

**الميزات**:
- ✅ Hardware acceleration يعمل بشكل ممتاز
- ✅ جميع الميزات متاحة
- ✅ أداء عالي
- ✅ لا توجد مشاكل معروفة

**التشغيل**:
```cmd
cd javafx
gradle clean run
```

## ✅ Linux (ممتاز - Excellent)

**الحالة**: يعمل بشكل ممتاز

**الميزات**:
- ✅ OpenGL acceleration متاح
- ✅ جميع الميزات متاحة
- ✅ أداء جيد

**التشغيل**:
```bash
cd javafx
gradle clean run
```

## ⚠️ macOS (يحتاج Java 21+)

**الحالة**: يعمل لكن قد يحتاج Java 21+ لتجنب مشاكل NSTrackingRectTag

**الميزات**:
- ✅ جميع الميزات متاحة
- ⚠️ قد يحتاج Java 21+ لتجنب crashes
- ✅ Software rendering يعمل بشكل جيد

**التشغيل**:
```bash
# مع Java 17 (قد يعمل)
cd javafx
gradle clean run

# مع Java 21 (موصى به)
export JAVA_HOME=/path/to/java21
cd javafx
gradle clean run
```

## الإعدادات التلقائية

التطبيق يكتشف المنصة تلقائياً ويطبق الإعدادات المناسبة:

- **Windows**: Hardware acceleration (D3D)
- **Linux**: OpenGL acceleration (ES2)
- **macOS**: Software rendering (لتجنب المشاكل)

## ملاحظات

- الكود متوافق مع جميع المنصات
- الإعدادات تطبق تلقائياً حسب المنصة
- لا حاجة لإعدادات يدوية على Windows/Linux

