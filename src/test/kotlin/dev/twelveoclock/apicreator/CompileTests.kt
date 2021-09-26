package dev.twelveoclock.apicreator

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.load.kotlin.KotlinClassFinder
import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinaryClass
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.copyTo
import kotlin.io.path.name
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.test.Test

class CompileTests {

	@Test
	fun basicTest() {

		val inputClass = SourceFile.kotlin("KClass.kt",
			"""
            class KClass {
                private fun foo() {
                    // Classes from the test environment are visible to the compiled sources
                } 
			}
            """
		)

		val expectedClass = SourceFile.kotlin("KClass.kt",
			"""
            class KClass
            """
		)


		val compiled1 = KotlinCompilation().apply {
			sources = listOf(inputClass)
			inheritClassPath = true
			messageOutputStream = System.out
		}.compile()

		val compiled2 = KotlinCompilation().apply {
			sources = listOf(expectedClass)
			inheritClassPath = true
			messageOutputStream = System.out
		}.compile()

		println()

		println(compiled1.classLoader.loadClass("KClass").name)

		val compiledInputClass = compiled1.outputDirectory.toPath().resolve("KClass.class")
		val compiledExpectedClass = compiled2.outputDirectory.toPath().resolve("KClass.class")

		compiledExpectedClass.copyTo(Path.of(compiledExpectedClass.name))
	}

}