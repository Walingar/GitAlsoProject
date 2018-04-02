package index

import commit.Commit

fun createIndexFromCommits(commits: Collection<Commit>, repoName: String): Index {
    val index = Index(repoName)
    for (commit in commits) {
        for (file in commit.getFiles()) {
            index.addCommitToFile(file, commit)
        }
    }

    return index
}