بناء تطبيق ماء المنبع عبر GitHub Actions

1) أنشئ حساباً في GitHub ثم أنشئ مستودعاً جديداً (Repository).
2) ارفع محتويات هذا المجلد إلى المستودع، بحيث يظهر build.gradle و settings.gradle ومجلد app في الجذر.
3) افتح تبويب Actions في المستودع.
4) سيبدأ البناء تلقائياً بعد رفع الملفات إلى main أو master. ويمكنك أيضاً اختيار Build Android APK ثم Run workflow.
5) بعد نجاح العملية، افتح نتيجة التشغيل (workflow run) ثم قسم Artifacts.
6) نزّل MaaAlManbaa-debug-apk.zip وافتحه؛ ستجد داخله app-debug.apk.
7) انقل ملف APK إلى هاتف أندرويد وثبّته. قد يطلب الهاتف السماح بالتثبيت من هذا المصدر.

ملاحظات:
- هذا الإعداد يبني نسخة Debug للتجربة، وليس نسخة Release موقعة للنشر في المتجر.
- GitHub لا يحتاج إلى Android Studio على حاسوبك؛ عملية البناء تتم على خوادم GitHub Actions.
- لم يتم رفع المشروع إلى حساب GitHub الخاص بك أو تنفيذ البناء بعد.
- اسم ملف APK الناتج: app-debug.apk.
