package dataset

import GitAlsoService
import java.io.File


fun createSimpleDataset(service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit> {
    val commits = ArrayList<PipeLineCommit>()
    for (commit in service.getCommits()) {
        if (commit.time in startTime..endTime) {
            val files = commit
                    .getFiles()
                    .filter { it.getCommits().any { it.time < commit.time } }
                    .map { it.id }
                    .shuffled()
                    .toList()
            if (files.isEmpty()) {
                continue
            }
            val pipeLineCommit = PipeLineCommit(
                    commit.time,
                    files,
                    arrayListOf())
            commits.add(pipeLineCommit)
        }
    }

    return commits
}

fun createRandomDataset(service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit> {
    val commits = ArrayList<PipeLineCommit>()
    for (commit in service.getCommits()) {
        if (commit.time in startTime..endTime) {
            val files = commit
                    .getFiles()
                    .filter { it.getCommits().any { it.time < commit.time } }
                    .map { it.id }
                    .shuffled()
                    .toList()
            if (files.size < 2) {
                continue
            }
            val pipeLineCommit = PipeLineCommit(
                    commit.time,
                    files.subList(1, files.size),
                    arrayListOf(files[0]))
            commits.add(pipeLineCommit)
        }
    }

    return commits
}

fun createFullDataset(service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit> {
    val commits = ArrayList<PipeLineCommit>()
    for (commit in service.getCommits()) {
        if (commit.time in startTime..endTime) {
            val files = commit.getFiles().map { it -> it.id }.toList()
            if (files.size > 1) {
                for (i in 0 until files.size) {
                    val pipeLineCommit = PipeLineCommit(
                            commit.time,
                            files.subList(0, i) + files.subList(i + 1, files.size),
                            arrayListOf(files[i]))
                    commits.add(pipeLineCommit)
                }
            }
        }
    }

    return commits
}

fun printDataset(directory: File, datasetName: String, dataset: List<PipeLineCommit>) {
    directory.mkdirs()
    val file = directory.resolve(datasetName)
    file.createNewFile()
    file.writeText("")
    for (commit in dataset) {
        file.appendText(commit.toString() + System.lineSeparator())
    }
}