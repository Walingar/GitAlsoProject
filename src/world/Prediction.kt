package world

import random

object Prediction {
    fun makePrediction(person: Person, threshold: Double): PredictionResult {
        val precision = 1000
        val prob = (person.cancelProbability(threshold) * precision).toInt()
        val rand = (0..precision).random()
        return if (rand <= prob) {
            PredictionResult.CANCEL
        } else {
            PredictionResult.COMMIT
        }
    }
}