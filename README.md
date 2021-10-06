# APICreator
Make API stubs from compiled jars!

### Example Usage
`java -jar APICreator.jar -i path/to/jarOrClass`


### Options:
```
--input, -i -> Input .jar or .class file (always required) { String }
--output, -o -> Output file path (Default: API-{inputFileName}) { String }
--overwrite [false] -> Whether or not to overwrite the output if it already exists
--cleaner, -c [PROGUARD] -> The cleaner library to use { Value should be one of [asm_ow2, proguard] }
--options [REMOVE_NON_CLASS_FILES, STRIP_KOTLIN_HEADERS] -> The cleaner options to use { Value should be one of [remove_non_class_files, strip_kotlin_headers] }
--help, -h -> Usage info 
```