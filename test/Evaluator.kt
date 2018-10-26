import org.junit.Test
import world.Person
import world.Prediction
import kotlin.math.max
import kotlin.math.min

class Evaluator {

    private fun benchmarkPerson(f: (Double) -> Double) {
        for (step in 0..1000) {
            val was = ArrayList<Double>()
            println("TRY $step")
            val person = Person(f)
            for (commitIndex in 0..200) {
                val result = Prediction.makePrediction(person, person.info.threshold)
                person.info.updatePersonInfo(result)
                //println("${person.info.threshold} $result")
                was.add(person.info.threshold)
                if (person.info.threshold == 1.0) {
                    break
                }
            }
            println("\tMiddle: ${was.sortedBy { it }[was.size / 2]}")
            println("\tLast: ${was.last()}")
        }
    }

    @Test
    fun `test linear person`() {
        val f = { threshold: Double ->
            if (threshold > 0.9) {
                0.0
            } else {
                min(0.8, max(0.2, 0.6 / 0.55 * threshold + 0.02 / 0.55))
            }
        }
        benchmarkPerson(f)
    }

    @Test
    fun `test linear person2`() {
        val f = { threshold: Double ->
            when {
                threshold > 0.9 -> 0.0
                threshold < 0.3 -> 0.0
                else -> 1.5 * threshold - .45
            }
        }
        benchmarkPerson(f)
    }

    @Test
    fun `test always forgot person`() {
        val f = { threshold: Double ->
            when {
                threshold > 0.9 -> 0.0
                else -> (2.0 / 9.0) * threshold + 0.7
            }
        }
        benchmarkPerson(f)
    }

    @Test
    fun `test commit always person`() {
        val f = { threshold: Double ->
            if (threshold > 0.9) {
                0.0
            } else {
                0.001
            }
        }
        benchmarkPerson(f)
    }
}