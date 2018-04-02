package index

import com.intellij.util.io.createDirectories
import java.io.File
import java.nio.file.Paths


// TODO: useless??
fun printIndexToFile(index: Index) {
    val dir = Paths.get("./index/" + index.getRepoName())
    dir.createDirectories()
    File("./index/" + index.getRepoName() + "/index.json").bufferedWriter().use { out ->
        index.getIndex().forEach {
            out.write(it.key.toString() + " {")
            out.write(it.value.joinToString(" "))
            out.write("}" + System.lineSeparator())
        }
    }
}