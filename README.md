# APICreator

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build Status](https://drone.12oclock.dev/api/badges/camdenorrb/APICreator/status.svg)](https://drone.12oclock.dev/camdenorrb/APICreator)

Remove the logic from your compiled code so people can build off it without the worry of them stealing your code

### Example Usage
`java -jar APICreator.jar -i path/to/jarOrClass`


### Options:
```
--input, -i -> Input .jar or .class file (always required) { String }
--output, -o -> Output file path (Default: API-{inputFileName}) { String }
--overwrite [false] -> Whether or not to overwrite the output if it already exists 
--cleaner, -c [ASM_OW2] -> The cleaner library to use { Value should be one of [noop_asm_ow2, asm_ow2, proguard] }
--options -> The cleaner options to use { Value should be one of [keep_kotlin_inline_metadata, keep_private, remove_kotlin_headers, remove_annotation_defaults, remove_non_class_files] }
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
