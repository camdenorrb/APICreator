package dev.twelveoclock.apicreator

import org.objectweb.asm.*
import java.net.URI
import java.nio.file.*
import kotlin.io.path.*

// TODO: Abstract and add a proguard-core variant to speedtest/compare
object Main {

	@JvmStatic
	fun main(args: Array<String>) {

		check(args.size == 1) {
			"Usage: java -jar ApiCreator.jar (PathToJarOrClass)"
		}

		val inputFile = Path.of(args[0]).toAbsolutePath()

		check(inputFile.exists()) {
			"Could not find jar file for path: '${inputFile.pathString}'"
		}
		check(inputFile.extension.equals("jar", true) || inputFile.extension.equals("class", true)) {
			"The jar file path provided isn't a .jar nor .class file"
		}

		val outputFile = inputFile.parent.resolve("API-${inputFile.name}")
		outputFile.deleteIfExists()

		if (outputFile.extension.equals("jar", true)) {
			cleanUpJar(inputFile, outputFile)
		}
		else {
			cleanUpClass(inputFile, outputFile)
		}
	}

	fun cleanUpJar(inputJar: Path, outputJar: Path) {

		val outputJarFileSystem =
			FileSystems.newFileSystem(URI.create("jar:file:${outputJar.pathString}"), mapOf("create" to true))
		val inputJarFileSystem =
			FileSystems.newFileSystem(URI.create("jar:file:${inputJar.pathString}"), mapOf("create" to true))

		Files.walk(inputJarFileSystem.rootDirectories.first()).filter { it.isRegularFile() }.forEach {

			val outputPath = outputJarFileSystem.getPath(it.pathString)
			outputPath.parent?.createDirectories()

			if (it.extension == "class") {
				cleanUpClass(it, outputPath)
			} else {
				it.copyTo(outputPath)
			}
		}

		outputJarFileSystem.close()
		inputJarFileSystem.close()
	}

	fun cleanUpClass(inputClazz: Path, outputClazz: Path) {

		val classReader = ClassReader(inputClazz.inputStream())
		val classWriter = ClassWriter(0)

		classReader.accept(APIClassVisitor(Opcodes.ASM9, classWriter), 0)

		outputClazz.writeBytes(classWriter.toByteArray(), StandardOpenOption.CREATE)
	}


	class APIClassVisitor(api: Int, classWriter: ClassWriter) : ClassVisitor(api, classWriter) {

		override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {

			if (descriptor == "Lkotlin/Metadata;") {
				return APIAnnotationVisitor(api, super.visitAnnotation(descriptor, visible))
			}

			return super.visitAnnotation(descriptor, visible)
		}

		override fun visitField(
			access: Int,
			name: String?,
			descriptor: String?,
			signature: String?,
			value: Any?
		): FieldVisitor? {

			// Write if the method is public or protected + abstract
			// TODO: Determine whether to keep protected members based on class visibility

			if (
				access and Opcodes.ACC_PUBLIC != 0 ||
				access and Opcodes.ACC_PROTECTED != 0
			) {
				return super.visitField(access, name, descriptor, signature, value)
			}

			return null
		}

		override fun visitMethod(
			access: Int,
			name: String,
			descriptor: String,
			signature: String?,
			exceptions: Array<String>?,
		): MethodVisitor? {

			// Write if the method is public or protected + abstract
			// TODO: Determine whether to keep protected members based on class visibility
			if (
				access and Opcodes.ACC_PUBLIC != 0 ||
				access and Opcodes.ACC_PROTECTED != 0
			) {
				super.visitMethod(access, name, descriptor, signature, exceptions)
			}

			return null
		}

	}

	class APIAnnotationVisitor(api: Int, visitor: AnnotationVisitor) : AnnotationVisitor(api, visitor) {

		override fun visitArray(name: String): AnnotationVisitor {
			return APIAnnotationArrayVisitor(api, name, super.visitArray(name))
		}

	}

	class APIAnnotationArrayVisitor(api: Int, val name: String, visitor: AnnotationVisitor) : AnnotationVisitor(api, visitor) {

		private var removeTypeNext = false

		override fun visit(name: String?, value: Any) {
			println("${this.name} $value")
			super.visit(name, value)
		}

	}

}