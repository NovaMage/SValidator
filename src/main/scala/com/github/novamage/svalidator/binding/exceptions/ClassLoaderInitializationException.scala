package com.github.novamage.svalidator.binding.exceptions

class ClassLoaderInitializationException extends RuntimeException("You must initialize MapToObjectBinder using `initializeModelClassLoader` with the class loader that loads your models"){

}
