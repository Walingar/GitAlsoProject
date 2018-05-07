package dataset

fun parseDataset(datasetString: String): List<PipeLineCommit> {
    val dataset = ArrayList<PipeLineCommit>()
    for (commit in datasetString.lines()) {
        if (commit.isBlank()) {
            continue
        }
        val (timeString, filesString, forgottenFilesString) = commit.split(';')
        val time = timeString.toLong()
        val files = filesString.split(", ").map { file -> file.toInt() }.toList()
        val forgottenFiles = if (forgottenFilesString.isNotBlank()) {
            forgottenFilesString.split(", ").map { file -> file.toInt() }.toList()
        } else {
            arrayListOf()
        }

        dataset.add(PipeLineCommit(time, files, forgottenFiles))
    }

    return dataset
}