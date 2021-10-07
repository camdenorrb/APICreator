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

		classReader.accept(APIClassVisitor(Opcodes.ASM9, classWriter, options), 0)

		outputPath.writeBytes(classWriter.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
	}


	class APIClassVisitor(api: Int, classWriter: ClassWriter, val options: Set<Cleaner.Option>) : ClassVisitor(api, classWriter) {

		override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {

			// Remove Kotlin metadata
			if (Cleaner.Option.KEEP_KOTLIN_HEADERS !in options && descriptor == "Lkotlin/Metadata;") {
				return null
				//return APIAnnotationVisitor(api, super.visitAnnotation(descriptor, visible))
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
			if (access and Opcodes.ACC_PRIVATE != 0) {
				return null
			}

			return super.visitField(access, name, descriptor, signature, value)
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
			if (access and Opcodes.ACC_PRIVATE != 0) {
				return null
			}

			return APIMethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions))
		}

	}

	class APIMethodVisitor(api: Int, visitor: MethodVisitor) : MethodVisitor(api, visitor) {

		override fun visitParameter(name: String?, access: Int) {}

		override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean) {}

		override fun visitAttribute(attribute: Attribute?) {}

		override fun visitCode() {}

		override fun visitFrame(
			type: Int,
			numLocal: Int,
			local: Array<out Any>?,
			numStack: Int,
			stack: Array<out Any>?,
		) {}

		override fun visitInsn(opcode: Int) {}

		override fun visitIntInsn(opcode: Int, operand: Int) {}

		override fun visitVarInsn(opcode: Int, `var`: Int) {}

		override fun visitTypeInsn(opcode: Int, type: String?) {}

		override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {}

		override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {}

		override fun visitMethodInsn(
			opcode: Int,
			owner: String?,
			name: String?,
			descriptor: String?,
			isInterface: Boolean,
		) {}

		override fun visitInvokeDynamicInsn(
			name: String?,
			descriptor: String?,
			bootstrapMethodHandle: Handle?,
			vararg bootstrapMethodArguments: Any?,
		) {}

		override fun visitJumpInsn(opcode: Int, label: Label?) {}

		override fun visitLabel(label: Label?) {}

		override fun visitLdcInsn(value: Any?) {}

		override fun visitIincInsn(`var`: Int, increment: Int) {}

		override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {}

		override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {}

		override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {}

		override fun visitInsnAnnotation(
			typeRef: Int,
			typePath: TypePath?,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return null
		}

		override fun visitTryCatchBlock(start: Label?, end: Label?, handler: Label?, type: String?) {}

		override fun visitLocalVariable(
			name: String?,
			descriptor: String?,
			signature: String?,
			start: Label?,
			end: Label?,
			index: Int,
		) {}

		override fun visitLineNumber(line: Int, start: Label?) {}

		override fun visitMaxs(maxStack: Int, maxLocals: Int) {}

		override fun visitEnd() {}

		override fun visitAnnotationDefault(): AnnotationVisitor? {
			return null
		}

		override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
			return null
		}

		override fun visitTypeAnnotation(
			typeRef: Int,
			typePath: TypePath?,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return null
		}

		override fun visitParameterAnnotation(
			parameter: Int,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return null
		}

		override fun visitTryCatchAnnotation(
			typeRef: Int,
			typePath: TypePath?,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return null
		}

		override fun visitLocalVariableAnnotation(
			typeRef: Int,
			typePath: TypePath?,
			start: Array<out Label>?,
			end: Array<out Label>?,
			index: IntArray?,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return null
		}
	}

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