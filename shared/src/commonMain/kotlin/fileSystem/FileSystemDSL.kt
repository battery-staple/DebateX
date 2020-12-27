//package com.rohengiralt.debatex.fileSystem
//
//package com.rohengiralt.debatex.fileSystem
//
//import com.rohengiralt.debatex.Logger
//import com.rohengiralt.debatex.defaultLogger
//import com.rohengiralt.debatex.fileSystem.path.DirectoryPath
//import com.rohengiralt.debatex.fileSystem.path.FilePath
//import com.rohengiralt.debatex.fileSystem.path.Path
//import com.rohengiralt.debatex.loggerForClass
//import io.ktor.utils.io.charsets.Charset
//import io.ktor.utils.io.charsets.Charsets
//import io.ktor.utils.io.core.toByteArray
//import io.ktor.utils.io.errors.IOException
//import kotlin.properties.ReadWriteProperty
//import kotlin.reflect.KProperty
//
//sealed class FileSystemElement<P : Path<P>>(
//    path: P
//) {
//    var path: P = path
//        set(newPath) {
//            createCopyAt(newPath)
//            field.delete()
//            field = newPath
//        }
//
//    var directoryPath: DirectoryPath
//        get() = path.superDirectory
//        set(newDirectory) {
//            path = path.copy(newDirectory)
//        }
//
//    open var canOverwriteAt: (oldElementPath: P) -> Boolean = { true }
//
//    protected abstract fun createCopyAt(newPath: P)
//    protected abstract fun newElementAt(path: P)
//
//    fun createAndConfigure() {
//        if (canOverwriteAt(path)) {
//            newElementAt(path)
//            configureAll()
//        }
//    }
//
//    private fun configureAll() {
//        for (configurator in configurators) {
//            configurator.initialize()
//        }
//    }
//
//    private val configurators = mutableListOf<Configurator<*>>()
//
//    protected fun <T> configuring(
//        initialValue: T,
//        get: () -> T,
//        set: (T) -> Unit
//    ): Configurator<T> =
//        ConfiguratorImpl(initialValue, get, set)
//            .also {
//                configurators.add(it)
//            }
//
//    protected abstract class Configurator<T>(
//        private var initialValue: T
//    ) : ReadWriteProperty<FileSystemElement<*>, T> {
//        abstract val get: () -> T
//        abstract val set: (T) -> Unit
//
//        private var initialized = false
//
//        fun initialize() {
//            if (!initialized) {
//                set(initialValue)
//                initialized = true
//            }
//        }
//
//        fun reset() {
//            set(initialValue)
//        }
//
//        override fun getValue(thisRef: FileSystemElement<*>, property: KProperty<*>): T = get()
//
//        override fun setValue(thisRef: FileSystemElement<*>, property: KProperty<*>, value: T) {
//            if (initialized) {
//                set(value)
//            } else {
//                logger.warn("Configuration attempted before initialization of file element; configuration from $initialValue to $value will only occur on initialization.")
//                initialValue = value
//            }
//        }
//
//        private val logger: Logger = defaultLogger()
//    }
//
//    private class ConfiguratorImpl<T>(
//        initialValue: T,
//        override val get: () -> T,
//        override val set: (T) -> Unit
//    ) : Configurator<T>(initialValue)
//}
//
////inline fun <reified T : FileSystemElement<*>> T.add(config: T.() -> Unit) {
//////    canOverwrite = { false } TODO: FIX THIS VERY IMPORTANT
////    config()
////    createAndConfigure()
////}
//
//class FileNotFoundException(message: String) : IOException(message)
//
//@Suppress("UNUSED")
//class Directory( //TODO: all setters should modify file system, all getters should get from file system, no fields should be stored
//    path: DirectoryPath
//) : FileSystemElement<DirectoryPath>(path) {
//
//    operator fun get(name: String): FileSystemElement<*>? = children[path / name]
//
//    fun <T : FileSystemElement<*>> getByName(name: String): T? =
//        get(name)?.let { it as? T }
//
//    internal fun <T : FileSystemElement<*>> addFileSystemElement(
//        file: T,
//        init: T.() -> Unit
//    ): T =
//        file.apply {
//            init()
//            children.add(this)
//        }
//
//    override fun newElementAt(path: DirectoryPath) {
//        path.makeDirectory()
//    }
//
//    private val children = linkedMapOf<Path<*>, FileSystemElement<*>>()
//
//    private fun LinkedHashMap<Path<*>, FileSystemElement<*>>.add(value: FileSystemElement<*>) {
//        value.directoryPath = this@Directory.path
//        value.createAndConfigure()
//        this[value.path] = value
//    }
//
//    override fun createCopyAt(newPath: DirectoryPath) {
//        newElementAt(newPath)
//        children.values.forEach {
//            it.directoryPath = newPath
//        }
//    }
//
//    companion object TopLevelDirectories {
//        @Suppress("UNUSED")
//        val Main: Directory = Directory(DirectoryPath.mainDirectoryPath).apply {
//            createAndConfigure()
//        }
//
//        @Suppress("UNUSED")
//        val Caches: Directory = Directory(DirectoryPath.cachesDirectoryPath).apply {
//            createAndConfigure()
//        }
//
//        @Suppress("UNUSED")
//        val Temporary: Directory = Directory(DirectoryPath.temporaryDirectoryPath).apply {
//            createAndConfigure()
//        }
//    }
//}
//
//
//@Suppress("UNUSED")
//fun Directory.directory(
//    name: String,
//    init: Directory.() -> Unit
//): Directory =
//    addFileSystemElement(
//        Directory(this.path / name),
//        init
//    )
//
//@Suppress("UNUSED")
//fun Directory.file(
//    name: String,
//    init: File.() -> Unit
//): File =
//    addFileSystemElement(
//        File(this.path / name),
//        init
//    )
//
//@Suppress("UNUSED")
//fun Directory.file(
//    name: String,
//    contents: ByteArray,
//    init: File.() -> Unit
//): File =
//    addFileSystemElement(
//        File(this.path / name, contents),
//        init
//    )
//
//@Suppress("UNUSED")
//fun Directory.file(
//    name: String,
//    contents: String,
//    init: File.() -> Unit
//): File =
//    addFileSystemElement(
//        File(this.path / name, contents),
//        init
//    )
//
//@Suppress("UNUSED")
//open class File(
//    path: FilePath,
//    contents: ByteArray? = null
//) : FileSystemElement<FilePath>(path) {
//    constructor(path: FilePath, contents: String, charset: Charset = Charsets.UTF_8) : this(
//        path,
//        contents.toByteArray(charset)
//    )
//
//    val name: String get() = path.name
//
//    open fun onUpdate(old: ByteArray, new: ByteArray) {}
//
//    @Suppress("UNUSED")
//    var contents: ByteArray by configuring(
//        initialValue = contents ?: ByteArray(0),
//        get = {
//            path.fileContents?.copyOf() //TODO: Is copyOf memory overhead worth the immutability gained?
//                ?: throw FileNotFoundException("Expected file at $path")
//        },
//        set = {
//            path.makeFile(it)
//        }
//    )
//
//    override fun createCopyAt(newPath: FilePath) {
//        path.makeFile(contents)
//    }
//
//    override fun newElementAt(path: FilePath) {
//        path.makeFile(contents)
//    }
//
//    companion object {
//        private val logger = loggerForClass<File>()
//    }
//}
//
////val dir = Directory.Main.apply {
////    directory("Events") {
////        val customEvents = directory("CustomEvents") {
////            creationType = FileSystemElement.CreationType.INITIALIZING
////        }
////
////        val presetEvents = directory("Presets") {
////            creationType = FileSystemElement.CreationType.INITIALIZING
////
////            serialized(com.rohengiralt.debatex.presets.presetEvents)
////        }
////
////    }
////}