package com.jetbrains.gitalso.commitHandle

import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import java.util.*

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start

fun getExecutionTime(task: () -> Unit): Long {
    val startTime = System.currentTimeMillis()
    task()
    return System.currentTimeMillis() - startTime
}

fun preparePredictionData(map: Map<Pair<CommittedFile, CommittedFile>, Set<Commit>>): Map<Pair<CommittedFile, CommittedFile>, Set<Long>> =
        map.map { (key, value) ->
            key to value
                    .map {
                        it.id.toLong()
                    }
                    .toSet()
        }.toMap()