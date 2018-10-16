package com.jetbrains.gitalso.predict

import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile

object PredictionResultProcessor {
    fun getCommitTimesFromPrediction(
            commit: Commit,
            topPrediction: Collection<CommittedFile>
    ): Map<Pair<CommittedFile, CommittedFile>, Set<Commit>> {
        val commits = HashMap<Pair<CommittedFile, CommittedFile>, HashSet<Commit>>()
        val predictionSet = topPrediction.toSet()

        for (file in commit.files) {
            for (fileCommit in file.commits) {
                fileCommit.files
                        .filter { it in predictionSet }
                        .forEach {
                            commits.putIfAbsent(Pair(file, it), HashSet())
                            commits[Pair(file, it)]!!.add(fileCommit)
                        }
            }
        }

        return commits
    }

    fun getCommitsAuthorMask(
            commits: Map<Pair<CommittedFile, CommittedFile>, Set<Commit>>,
            author: String
    ): Map<Pair<CommittedFile, CommittedFile>, Set<Commit>> {
        val authorMask = HashMap<Pair<CommittedFile, CommittedFile>, HashSet<Commit>>()
        for ((pair, set) in commits) {
            authorMask.putIfAbsent(pair, hashSetOf())
            for (commit in set) {
                if (commit.author == author) {
                    authorMask[pair]!!.add(commit)
                }
            }
        }

        return authorMask
    }
}