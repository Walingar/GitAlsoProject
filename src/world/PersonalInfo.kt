package world

import kotlin.math.max
import kotlin.math.min

class PersonalInfo {
    var lastResult = PredictionResult.COMMIT
    var step = 0.0
    var threshold = 0.3
    private val gamma = 0.9

    fun updatePersonInfo(type: PredictionResult) {
        if (type == PredictionResult.CANCEL && step <= 0) {
            threshold -= 0.005
            lastResult = type
            return
        }
        lastResult = type

        step *= gamma
        step = when (type) {
            PredictionResult.COMMIT -> {
                min(((1 - threshold) / 7), step + 0.05)
            }
            PredictionResult.CANCEL -> {
                max(-(threshold / 7), step - 0.05)
            }
        }
        threshold = min(max(threshold + step, 0.15), 1.0)
    }
}