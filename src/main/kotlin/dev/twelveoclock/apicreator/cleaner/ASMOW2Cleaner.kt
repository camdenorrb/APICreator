package dev.twelveoclock.apicreator.cleaner

import dev.twelveoclock.apicreator.cleaner.base.Cleaner
import kotlinx.metadata.Flag
import kotlinx.metadata.internal.metadata.deserialization.Flags.IS_INLINE
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
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

		outputPath.writeBytes(
			classWriter.toByteArray(),
			StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING
		)
	}


	class APIClassVisitor(api: Int, classWriter: ClassWriter, val options: Set<Cleaner.Option>) : ClassVisitor(api, classWriter) {

		lateinit var kotlinMetadataVisitor: APIKotlinMetadataVisitor
			private set

		override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {

			if (descriptor == "Lkotlin/Metadata;") {

				// Remove Kotlin metadata
				if (Cleaner.Option.REMOVE_KOTLIN_HEADERS in options) {
					return null
				}

				kotlinMetadataVisitor = APIKotlinMetadataVisitor(api, options, super.visitAnnotation(descriptor, visible))
				return kotlinMetadataVisitor
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

			if (Cleaner.Option.KEEP_PRIVATE !in options && access and Opcodes.ACC_PRIVATE != 0) {
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
			if (Cleaner.Option.KEEP_PRIVATE !in options && access and Opcodes.ACC_PRIVATE != 0) {
				return null
			}

			//return super.visitMethod(access, name, descriptor, signature, exceptions)
			return APIMethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions), options)
		}

	}

	class APIMethodVisitor(api: Int, visitor: MethodVisitor, val options: Set<Cleaner.Option>) : MethodVisitor(api, visitor) {

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

		override fun visitLineNumber(line: Int, start: Label?) {
			return super.visitLineNumber(line, start)
		}

		override fun visitMaxs(maxStack: Int, maxLocals: Int) {
			return super.visitMaxs(maxStack, maxLocals)
		}

		override fun visitEnd() {
			return super.visitEnd()
		}

		override fun visitAnnotationDefault(): AnnotationVisitor? {

			if (Cleaner.Option.REMOVE_ANNOTATION_DEFAULTS !in options) {
				return super.visitAnnotationDefault()
			}

			return null
		}

		override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
			return super.visitAnnotation(descriptor, visible)
		}

		override fun visitTypeAnnotation(
			typeRef: Int,
			typePath: TypePath?,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
		}

		override fun visitParameterAnnotation(
			parameter: Int,
			descriptor: String?,
			visible: Boolean,
		): AnnotationVisitor? {
			return super.visitParameterAnnotation(parameter, descriptor, visible)
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
			return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible)
		}
	}


	class APIKotlinMetadataVisitor(api: Int, val options: Set<Cleaner.Option>, val writer: AnnotationVisitor) : AnnotationVisitor(api) {

		var metaDataVersion: IntArray? = null
			private set

		var kind: Int? = null

		var extraInt: Int? = null
			private set

		var data1: MutableList<String>? = null

		var data2: MutableList<String>? = null

		var extraString: String? = null
			private set

		// This was deprecated out
		//var byteCodeVersion: IntArray? = null
		//	private set

		var packageName: String? = null
			private set


		override fun visit(name: String, value: Any) {
			when(name) {
				"mv" -> metaDataVersion = value as IntArray
				"k" -> kind = value as Int
				"xi" -> extraInt = value as Int
				"xs" -> extraString = value as String
				"pn" -> packageName = value as String
				"bv" -> {/*NOOP*/} //byteCodeVersion = value as IntArray
				else -> throw IllegalStateException("Unknown value $name")
			}
		}

		override fun visitAnnotation(name: String?, descriptor: String?): AnnotationVisitor {
			error("Should never get here")
		}

		override fun visitEnum(name: String?, descriptor: String?, value: String?) {
			error("Should never get here")
		}

		override fun visitArray(name: String): AnnotationVisitor {
			return when (name) {
				"d1" -> {
					if (data1 == null) {
						data1 = mutableListOf()
					}
					ArrayVisitor(api, name, data1!!)
				}

				"d2" -> {
					if (data2 == null) {
						data2 = mutableListOf()
					}
					ArrayVisitor(api, name, data2!!)
				}

				else -> throw IllegalStateException("Unknown name $name")
			}
		}

		override fun visitEnd() {

			// https://github.com/JetBrains/kotlin/blob/master/libraries/kotlinx-metadata/jvm/ReadMe.md
			val header = KotlinClassHeader(kind, metaDataVersion, data1?.toTypedArray(), data2?.toTypedArray(), extraString, packageName, extraInt)

			if (Cleaner.Option.KEEP_KOTLIN_INLINE_METADATA in options) {
				writeKotlinMeta(header)
				return
			}

			val meta = KotlinClassMetadata.read(header)

			when (meta) {
				is KotlinClassMetadata.Class -> {
					val kmClass = meta.toKmClass()
					kmClass.functions.filter { Flag.Function.IS_INLINE(it.flags) }.forEach { it.flags = IS_INLINE.invert(it.flags) }
					writeKotlinMeta(KotlinClassMetadata.writeClass(kmClass))
				}
				is KotlinClassMetadata.FileFacade -> {
					val kmPackage = meta.toKmPackage()
					kmPackage.functions.filter { Flag.Function.IS_INLINE(it.flags) }.forEach { it.flags = IS_INLINE.invert(it.flags) }
					writeKotlinMeta(KotlinClassMetadata.writeFileFacade(kmPackage))
				}
				else -> writeKotlinMeta(header) // TODO: Support other types
			}
		}

		private fun writeKotlinMeta(header: Metadata) {

			header.metadataVersion.takeIf { it.isNotEmpty() }?.let { writer.visit("mv", it) }
			writer.visit("k", header.kind)
			@Suppress("KotlinConstantConditions")
			header.extraInt.takeIf { it != 0 }?.let { writer.visit("xi", it) }
			//header.extraInt.takeIf { it != 0 }?.let { writer.visit("xi", it) } // For some reason header seems to lack the extra int
			//byteCodeVersion?.let { writer.visit("bv", it) }

			if (header.data1.isNotEmpty()) {
				writer.visitArray("d1").apply {
					header.data1.forEach { visit(null, it) }
					visitEnd()
				}
			}

			if (header.data2.isNotEmpty()) {
				writer.visitArray("d2").apply {
					header.data2.forEach { visit(null, it) }
					visitEnd()
				}
			}
			header.extraString.takeIf { it.isNotBlank() }?.let { writer.visit("xs", it) }
			header.packageName.takeIf { it.isNotBlank() }?.let { writer.visit("pn", it) }

			writer.visitEnd()
		}

		class ArrayVisitor(api: Int, val name: String, val output: MutableList<String>) : AnnotationVisitor(api) {

			override fun visit(nullName: String?, value: Any) {
				output.add(value as String)
			}

		}
	}

}