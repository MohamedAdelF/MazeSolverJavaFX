# رفع المشروع على GitHub

## ✅ تم إعداد Git بنجاح!

تم إعداد Git وإضافة جميع الملفات. الآن تحتاج فقط لرفع المشروع.

## 🚀 طريقة الرفع

### الطريقة 1: باستخدام GitHub CLI (الأسهل)

```bash
cd /Users/tank/Downloads/Maze-Solver-Robot-main
gh auth login
git push -u origin main
```

### الطريقة 2: باستخدام Personal Access Token

1. اذهب إلى: https://github.com/settings/tokens
2. أنشئ Personal Access Token جديد
3. ثم:

```bash
cd /Users/tank/Downloads/Maze-Solver-Robot-main
git push -u origin main
# عندما يطلب Username: أدخل اسم المستخدم
# عندما يطلب Password: أدخل الـ Token (ليس كلمة المرور)
```

### الطريقة 3: استخدام SSH (موصى به)

1. أضف SSH key إلى GitHub
2. غير الـ remote:

```bash
cd /Users/tank/Downloads/Maze-Solver-Robot-main
git remote set-url origin git@github.com:MohamedAdelF/MazeSolverJavaFX.git
git push -u origin main
```

### الطريقة 4: من GitHub Desktop

1. افتح GitHub Desktop
2. File > Add Local Repository
3. اختر المجلد: `/Users/tank/Downloads/Maze-Solver-Robot-main`
4. Publish repository

## 📊 ما تم إعداده

✅ **30 ملف** تم إضافتها
✅ **2813 سطر** من الكود
✅ **Commit واحد** جاهز للرفع
✅ **Remote repository** مضاف: https://github.com/MohamedAdelF/MazeSolverJavaFX.git

## 📁 الملفات المرفوعة

- ✅ جميع ملفات Java (17 ملف)
- ✅ build.gradle
- ✅ README files
- ✅ .gitignore
- ✅ جميع ملفات التوثيق

## 🎯 بعد الرفع

بعد رفع المشروع بنجاح، سيكون متاحاً على:
**https://github.com/MohamedAdelF/MazeSolverJavaFX**

---

**ملاحظة**: إذا واجهت مشاكل في المصادقة، استخدم GitHub Desktop أو Personal Access Token.

