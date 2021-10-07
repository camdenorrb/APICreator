package dev.twelveoclock.apicreator.cleaner.base

import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

/**
 * The base for cleaning up the classes so that they are ready to be used as APIs
 *
 * @property name String, The name of the cleaner
 */
interface Cleaner {

	val name: String


	/**
	 * Cleans the class of private methods and fields and removes the inner code otherwise
	 *
	 * @param inputPath Path, The path to the input class
	 * @param outputPath Path, The path to the output class
	 * @param options Set<Option>, Which options to use while cleaning the class
	 */
	fun cleanUpClass(inputPath: Path, outputPath: Path, options: Set<Option>)

	/**
	 *
	 * @param inputPath Path, The path to the input jar
	 * @param outputPath Path, The path to the output jar
	 * @param options Set<Option>, Which options to use while cleaning the classes
	 */
	fun cleanUpJar(inputPath: Path, outputPath: Path, options: Set<Option>) {

		val outputJarFS = FileSystems.newFileSystem(URI.create("jar:file:${outputPath.absolute().pathString}"), mapOf("create" to true))
		val inputJarFS = FileSystems.newFileSystem(URI.create("jar:file:${inputPath.absolute().pathString}"), mapOf("create" to true))

		Files.walk(inputJarFS.rootDirectories.first()).filter { it.isRegularFile() }.forEach {

			if (it.name == "module-info.class") {
				return@forEach
			}

			val outputClassPath = outputJarFS.getPath(it.pathString).apply {
				parent?.createDirectories()
			}

			if (it.extension == "class") {
				cleanUpClass(it, outputClassPath, options)
			}
			else if (Option.KEEP_NON_CLASS_FILES in options){
				it.copyTo(outputClassPath)
			}
		}

		outputJarFS.close()
		inputJarFS.close()
	}


	/**
	 * Cleaner options
	 */
	enum class Option {
		KEEP_NON_CLASS_FILES,
		STRIP_KOTLIN_HEADERS,
	}

}