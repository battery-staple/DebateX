//package com.rohengiralt.debatex.fileSystem
//
//import com.rohengiralt.debatex.fileSystem.path.Path
//import com.suparnatural.core.fs.FileSystem
//import com.suparnatural.core.fs.FileType
//
//fun Path<*>.delete() {
//    FileSystem.unlink(toString())
//}
//
//fun Path<*>.makeDirectory() {
//    delete()
//    FileSystem.mkdir(toString(), true)
//}
//
//fun Path<*>.makeFile() {
//    delete()
//    FileSystem.touch(toString())
//}
//
//fun Path<*>.makeFile(contents: ByteArray) {
//    delete()
//    FileSystem.writeFile(toString(), contents, true)
//}
//
//val Path<*>.exists: Boolean
//    get() = FileSystem.exists(toString())
//
//val Path<*>.isDirectory: Boolean
//    get() = FileSystem.stat(toString())?.type == FileType.Directory
//
//val Path<*>.fileContents: ByteArray? get() = FileSystem.readFile(toString())