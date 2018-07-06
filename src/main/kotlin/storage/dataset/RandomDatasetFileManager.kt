package storage.dataset

import commitInfo.PipeLineCommit
import repository.GitAlsoService

class RandomDatasetFileManager(repositoryName: String) : DatasetFileManager(repositoryName) {

    override val type = DatasetType.RANDOM

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

        return commits.sortedBy { it.time }
    }

}