package dev.twelveoclock.apicreator.cleaner

import dev.twelveoclock.apicreator.cleaner.base.Cleaner
import proguard.classfile.AccessConstants
import proguard.classfile.ProgramClass
import proguard.classfile.attribute.Attribute
import proguard.classfile.editor.AttributesEditor
import proguard.classfile.editor.ClassEditor
import proguard.classfile.io.ProgramClassReader
import proguard.classfile.io.ProgramClassWriter
import proguard.classfile.visitor.MultiClassVisitor
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

// TODO: Add support for removing kotlin headers
object ProGuardCleaner : Cleaner {

	override val name = "Proguard"


	override fun cleanUpClass(inputPath: Path, outputPath: Path, options: Set<Cleaner.Option>) {
		DataOutputStream(outputPath.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)).use { dataOutputStream ->

			val programClass = programClass(inputPath)

			val classEditor = ClassEditor(programClass)
			//val classAttributeEditor = AttributesEditor(programClass, true)

			// Remove fields that are private
			programClass.fields.reversed().forEach { field ->
				if (field.accessFlags and AccessConstants.PRIVATE != 0) {
					classEditor.removeField(field)
				}
			}

			// Remove methods that are private and the code otherwise
			programClass.methods.reversed().forEach { method ->
				if (method.accessFlags and AccessConstants.PRIVATE != 0) {
					classEditor.removeMethod(method)
				}
				else {
					AttributesEditor(programClass, method, true).deleteAttribute(Attribute.CODE)
				}
			}


			programClass.accept(
				MultiClassVisitor(
					/*KotlinMetadataInitializer(WarningPrinter(PrintWriter(System.err))),*/
					/*APIVisitor(),*/
					ProgramClassWriter(dataOutputStream)
				)
			)
		}
	}

	fun programClass(inputClazz: Path): ProgramClass {
		return ProgramClass().apply {
			accept(ProgramClassReader(DataInputStream(inputClazz.inputStream().buffered()), true))
		}
	}


	/*
	class APIVisitor : ClassVisitor, AttributeVisitor {

		lateinit var classEditor: ClassEditor

		lateinit var classAttributeEditor: AttributesEditor


		override fun visitAnyClass(clazz: Clazz?) {
			error("Only accepts program classes, not library ones!")
		}

		override fun visitProgramClass(programClass: ProgramClass) {

			classEditor = ClassEditor(programClass)
			classAttributeEditor = AttributesEditor(programClass, true)

			// Remove fields that are private
			programClass.fields.reversed().forEach { field ->
				if (field.accessFlags and AccessConstants.PRIVATE != 0) {
					classEditor.removeField(field)
				}
			}

			// Remove methods that are private and the code otherwise
			programClass.methods.reversed().forEach { method ->
				if (method.accessFlags and AccessConstants.PRIVATE != 0) {
					classEditor.removeMethod(method)
				}
				else {
					AttributesEditor(programClass, method, true).deleteAttribute(Attribute.CODE)
				}
			}

			//programClass.kotlinMetadataAccept(this)
		}
	}
	*/

}