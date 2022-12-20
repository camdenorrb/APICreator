package dev.twelveoclock.apicreator.cleaner

import dev.twelveoclock.apicreator.cleaner.base.Cleaner
import org.objectweb.asm.*
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream
import kotlin.io.path.writeBytes

object NOOPASMOW2Cleaner : Cleaner {

	override val name = "NOOP_ASM"


	override fun cleanUpClass(inputPath: Path, outputPath: Path, options: Set<Cleaner.Option>) {

		val classReader = ClassReader(inputPath.inputStream())
		val classWriter = ClassWriter(0)

		classReader.accept(APIClassVisitor(Opcodes.ASM9, classWriter, options), 0)

		outputPath.writeBytes(classWriter.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
	}


	class APIClassVisitor(api: Int, classWriter: ClassWriter, val options: Set<Cleaner.Option>) : ClassVisitor(api, classWriter)

	/*
	class APIAnnotationVisitor(api: Int, visitor: AnnotationVisitor) : AnnotationVisitor(api, visitor) {

		override fun visitArray(name: String): AnnotationVisitor? {
			return null
			//return APIAnnotationArrayVisitor(api, name, super.visitArray(name))
		}

	}

	class APIAnnotationArrayVisitor(api: Int, val name: String, visitor: AnnotationVisitor) : AnnotationVisitor(api, visitor) {

		private var removeTypeNext = false

		override fun visit(name: String?, value: Any) {
			//println("${this.name} $value")
			super.visit(name, value)
		}

	}
	*/
}