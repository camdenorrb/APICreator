# APICreator

[![Build Status](https://drone.12oclock.dev/api/badges/camdenorrb/APICreator/status.svg)](https://drone.12oclock.dev/camdenorrb/APICreator)

Remove the logic from your compiled code so people can build off it without the worry of them stealing your code

### Example Usage
`java -jar APICreator.jar -i path/to/jarOrClass`


### Options:
```
--input, -i -> Input .jar or .class file (always required) { String }
--output, -o -> Output file path (Default: API-{inputFileName}) { String }
--overwrite [false] -> Whether or not to overwrite the output if it already exists 
--cleaner, -c [ASM_OW2] -> The cleaner library to use { Value should be one of [asm_ow2, proguard] }
--options [KEEP_NON_CLASS_FILES, KEEP_KOTLIN_HEADERS] -> The cleaner options to use { Value should be one of [keep_non_class_files, keep_kotlin_headers] }
--help, -h -> Usage info 
```

### Example 1
```kotlin
fun doAThing() { 
    println("I do this thing")
}
```
Turns into
```kotlin
fun doAThing() {}
```
---
### Example 2

```kotlin
private fun doAThing() { 
    println("I do this thing")
}
```
Gets deleted since it's private
