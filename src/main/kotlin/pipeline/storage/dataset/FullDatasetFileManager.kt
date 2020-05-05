package storage.dataset

import commitInfo.PipeLineCommit
import repository.GitAlsoService

class FullDatasetFileManager(repositoryName: String) : DatasetFileManager(repositoryName) {

    override val type = DatasetType.FULL

    override fun createDataset(service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit> {
        val commits = ArrayList<PipeLineCommit>()
        for ((_, commit) in service.commits) {
            if (commit.time in startTime..endTime) {
                val files = commit.files
                        .filter { it.commits.any { it.time < commit.time } }
                        .map { it -> it.id }
                        .toList()
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

        return commits.sortedBy { it.time }
    }

}