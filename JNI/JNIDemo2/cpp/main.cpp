#include <jni.h>
#include <iostream>

int main() {
    JavaVM *jvm;
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options[1];

    // Путь к папке с скомпилированными .class файлами
    options[0].optionString = (char*)"-Djava.class.path=.";

    vm_args.version = JNI_VERSION_1_8;
    vm_args.nOptions = 1;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = JNI_FALSE;

    // 1. Создаём JVM
    jint res = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
    
    if (res != JNI_OK) {
        std::cerr << "Ошибка создания JVM, код: " << res << std::endl;
        return -1;
    }

    // 2. Находим Java-класс
    jclass mathClass = env->FindClass("com/example/SimpleMath");
    if (mathClass == nullptr) {
        std::cerr << "Класс com/example/SimpleMath не найден!" << std::endl;
        jvm->DestroyJavaVM();
        return -1;
    }

    // 3. Находим статический метод add
    jmethodID addMethod = env->GetStaticMethodID(mathClass, "add", "(II)I");
    if (addMethod == nullptr) {
        std::cerr << "Метод add не найден!" << std::endl;
        jvm->DestroyJavaVM();
        return -1;
    }

    // 4. Вызываем метод add(5, 3)
    jint result = env->CallStaticIntMethod(mathClass, addMethod, 5, 3);
    
    std::cout << "Результат вызова Java-метода add(5, 3) = " << result << std::endl;

    // 5. Закрываем JVM
    jvm->DestroyJavaVM();
    return 0;
}