# Maze Solver Robot - JavaFX

تطبيق JavaFX لتصور خوارزميات حل المتاهات باستخدام بنيات بيانات مختلفة (Stack, Queue, Linked List) وخوارزمية AI (A*).

## المتطلبات

- Java 11+ (يفضل Java 17)
- Gradle (أو استخدم Gradle Wrapper المرفق)
- JavaFX SDK (يتم تثبيته تلقائياً عبر Gradle)

## كيفية التشغيل

### على Windows (يعمل بشكل ممتاز! ✅):

```cmd
cd javafx
gradle clean run
```

أو:
```cmd
gradlew.bat clean run
```

### على Linux/Mac:

```bash
cd javafx
gradle clean run
```

### ملاحظة مهمة:
- ✅ **Windows**: يعمل بشكل ممتاز بدون مشاكل
- ⚠️ **macOS**: قد تحتاج Java 21+ (راجع MACOS_FIX.md)
- ✅ **Linux**: يعمل بشكل ممتاز

## الميزات

- ✅ تصور خوارزميات حل المتاهات (DFS, BFS, Linked List, A* AI)
- ✅ واجهة مستخدم تفاعلية مع JavaFX
- ✅ لوحة تحكم كاملة (تشغيل، إيقاف، خطوة للأمام)
- ✅ إحصائيات مباشرة
- ✅ تصور البنيات البيانات (Stack, Queue, Linked List)
- ✅ متاهات جاهزة للاختيار
- ✅ تحرير المتاهة (إضافة/إزالة جدران)
- ✅ تعيين نقطة البداية والنهاية

## الاستخدام

- **النقر على خلية**: تبديل بين جدار/فارغة
- **Shift + النقر**: تعيين نقطة البداية
- **Alt + النقر**: تعيين نقطة النهاية
- **أزرار التحكم**: تشغيل، إيقاف، خطوة للأمام، إعادة تعيين

## البنية

```
javafx/
├── src/main/java/com/mazesolver/
│   ├── Main.java                    # نقطة الدخول
│   ├── MazeSolverApp.java          # التطبيق الرئيسي
│   ├── model/                       # نماذج البيانات
│   ├── solver/                     # منطق حل المتاهة
│   ├── ui/                         # مكونات الواجهة
│   └── util/                       # أدوات مساعدة
└── build.gradle                    # إعدادات Gradle
```

