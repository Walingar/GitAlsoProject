package storage.dataset

import commitInfo.PipeLineCommit
import repository.GitAlsoService

class SimpleDatasetFileManager(repositoryName: String) : DatasetFileManager(repositoryName) {

    override val type = DatasetType.SIMPLE

    override fun createDataset(service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit> {
        val commits = ArrayList<PipeLineCommit>()
        for (commit in service.commits) {
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

        return commits.sortedBy { it.time }
    }

}