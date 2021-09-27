package dev.twelveoclock.apicreator.cleaner

import dev.twelveoclock.apicreator.cleaner.base.Cleaner
import org.objectweb.asm.*
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream
import kotlin.io.path.writeBytes

object ASMOW2Cleaner : Cleaner {

	override val name = "ASM"


	override fun cleanUpClass(inputPath: Path, outputPath: Path, options: Set<Cleaner.Option>) {

		val classReader = ClassReader(inputPath.inputStream())
		val classWriter = ClassWriter(0)

		classReader.accept(APIClassVisitor(Opcodes.ASM9, classWriter), 0)

		outputPath.writeBytes(classWriter.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
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
			//println("${this.name} $value")
			super.visit(name, value)
		}

	}
}