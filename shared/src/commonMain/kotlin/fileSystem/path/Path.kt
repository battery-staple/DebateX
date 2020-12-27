//package com.rohengiralt.debatex.fileSystem.path
//
//import com.rohengiralt.debatex.xkcdError
//import com.suparnatural.core.fs.FileSystem
//
//sealed class Path<out This : Path<This>>(
//    @PublishedApi internal val topLevelDirectoryComponent: TopLevelDirectoryPathComponent
//) {
//    abstract val superDirectory: DirectoryPath
//    protected abstract val nameComponent: PathComponent<*>
//    open val name: String get() = nameComponent.toString()
//
//    abstract fun copy(
//        superDirectory: DirectoryPath? = null,
//        name: String? = null
//    ): This
//}
//
//class DirectoryPath @PublishedApi internal constructor(
//    topLevelDirectoryComponent: TopLevelDirectoryPathComponent,
//    internal val directoryComponents: List<SubDirectoryPathComponent> = emptyList()
//) : Path<DirectoryPath>(topLevelDirectoryComponent) {
//    val isTopLevelDirectoryPath: Boolean get() = directoryComponents.isEmpty()
//
//    inline operator fun <reified T : Path<*>> div(name: String): T =
//        when (T::class) { //TODO: ANNOTATION TO FIX THIS
//            DirectoryPath::class -> (this / SubDirectoryPathComponent(name)).also { println("DirectoryPath $it") }
//            FilePath::class -> (this / FilePathComponent(name)).also { println("FilePath $it") }
//            else -> throw xkcdError
//        } as T
//
//    operator fun div(directory: SubDirectoryPathComponent): DirectoryPath =
//        DirectoryPath(
//            topLevelDirectoryComponent,
//            directoryComponents + directory
//        )
//
//    operator fun div(file: FilePathComponent): FilePath =
//        FilePath(
//            topLevelDirectoryComponent,
//            directoryComponents,
//            file
//        )
//
//    override val nameComponent: PathComponent<*> =
//        directoryComponents.lastOrNull() ?: topLevelDirectoryComponent
//
//    override fun toString(): String =
//        topLevelDirectoryComponent.toString() +
//                DIRECTORY_SEPARATOR +
//                directoryComponents.joinToString(separator = DIRECTORY_SEPARATOR)
//
//    override val superDirectory: DirectoryPath by lazy { //TODO: If this DirectoryPath represents a top level directory, then this just returns itself. Is it valid to say that those are their own superdirectory?
//        DirectoryPath(
//            topLevelDirectoryComponent,
//            directoryComponents.dropLast(1)
//        )
//    }
//
//    override fun copy(
//        superDirectory: DirectoryPath?,
//        name: String?
//    ): DirectoryPath =
//        DirectoryPath(
//            topLevelDirectoryComponent = (superDirectory ?: this).topLevelDirectoryComponent,
//            directoryComponents =
//            (superDirectory ?: this.superDirectory)
//                .directoryComponents + SubDirectoryPathComponent(name ?: this.name)
//        )
//
//    companion object {
//        @Suppress("UNUSED")
//        val mainDirectoryPath: DirectoryPath =
//            DirectoryPath(
//                TopLevelDirectoryPathComponent(
//                    FileSystem.contentsDirectory.absolutePath!!.component.also { println("MainDir: $it") }
//                        ?: throw IllegalStateException("No main directory found (?)").also { println("What") }
//                )
//            )
//
//        @Suppress("UNUSED")
//        val cachesDirectoryPath: DirectoryPath =
//            DirectoryPath(
//                TopLevelDirectoryPathComponent(
//                    FileSystem.cachesDirectory.absolutePath!!.component
//                        ?: throw IllegalStateException("No caches directory found (?)")
//                )
//            )
//
//        @Suppress("UNUSED")
//        val temporaryDirectoryPath: DirectoryPath =
//            DirectoryPath(
//                TopLevelDirectoryPathComponent(
//                    FileSystem.cachesDirectory.absolutePath!!.component
//                        ?: throw IllegalStateException("No temporary directory found (?)")
//                )
//            )
//
//        const val DIRECTORY_SEPARATOR: String = "/"
//    }
//}
//
//@Suppress("UNUSED")
//class FilePath @PublishedApi internal constructor(
//    topLevelDirectoryComponent: TopLevelDirectoryPathComponent,
//    private val directoryComponents: List<SubDirectoryPathComponent>,
//    private val fileComponent: FilePathComponent
//) : Path<FilePath>(topLevelDirectoryComponent) {
//    override val nameComponent: FilePathComponent
//        get() = fileComponent
//
//    override val superDirectory: DirectoryPath
//        get() = DirectoryPath(
//            topLevelDirectoryComponent,
//            directoryComponents
//        )
//
//    override fun copy(superDirectory: DirectoryPath?, name: String?): FilePath =
//        FilePath(
//            topLevelDirectoryComponent = (superDirectory ?: this).topLevelDirectoryComponent,
//            directoryComponents = superDirectory?.directoryComponents ?: this.directoryComponents,
//            fileComponent = FilePathComponent(name ?: this.name)
//        )
//}
//
//@Suppress("UNUSED")
//sealed class PathComponent<This : PathComponent<This>>(
//    name: String,
//    validCharacters: Set<Char>,
//    coercingInvalidNames: Boolean = true
//) {
//    val name =
//        if (coercingInvalidNames)
//            name coercedToContainOnly validCharacters
//        else {
//            name.also {
//                require(it containsOnly validCharacters) { "$it contains invalid characters." }
//            }
//        }
//
//    @PublishedApi
//    internal abstract fun copy(
//        name: String? = null
//    ): This
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline infix fun String.coercedToContainOnly(characters: Set<Char>): String =
//        filter { it in characters }
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline infix fun String.containsOnly(characters: Set<Char>): Boolean =
//        all { it in characters }
//
//    override fun toString(): String = name
//}
//
//inline fun <reified T : PathComponent<T>> T.copy(name: String? = null): T =
//    copy(name)
//
//@Suppress("UNUSED")
//class SubDirectoryPathComponent(
//    name: String,
//    coercingInvalidNames: Boolean = true
//) : PathComponent<SubDirectoryPathComponent>(name, VALID_CHARACTERS, coercingInvalidNames) {
//
//    internal companion object {
//        val VALID_CHARACTERS: Set<Char>
//            @Suppress("SpellCheckingInspection") inline get() =
//                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-#".toSet()
//    }
//
//    override fun copy(
//        name: String?
//    ): SubDirectoryPathComponent =
//        SubDirectoryPathComponent(
//            name ?: this.name
//        )
//
//    fun copy(
//        name: String? = null,
//        coercingInvalidNames: Boolean = true
//    ): SubDirectoryPathComponent =
//        SubDirectoryPathComponent(
//            name ?: this.name,
//            coercingInvalidNames
//        )
//}
//
//@Suppress("UNUSED")
//class TopLevelDirectoryPathComponent(name: String) :
//    PathComponent<TopLevelDirectoryPathComponent>(
//        name,
//        VALID_CHARACTERS,
//        coercingInvalidNames = false
//    ) {
//
//    internal companion object {
//        val VALID_CHARACTERS: Set<Char>
//            inline get() =
//                SubDirectoryPathComponent.VALID_CHARACTERS + "*:<>?\\|+,.;=[]".toSet()
//    }
//
//    override fun copy(name: String?): TopLevelDirectoryPathComponent =
//        TopLevelDirectoryPathComponent(name ?: this.name)
//}
//
//@Suppress("UNUSED")
//class FilePathComponent(name: String) :
//    PathComponent<FilePathComponent>(
//        name,
//        VALID_CHARACTERS
//    ) {
//
//    internal companion object {
//        val VALID_CHARACTERS: Set<Char>
//            inline get() =
//                SubDirectoryPathComponent.VALID_CHARACTERS + '.'
//    }
//
//    override fun copy(name: String?): FilePathComponent =
//        FilePathComponent(name ?: this.name)
//}