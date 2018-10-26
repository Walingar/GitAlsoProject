package world

data class Person(val cancelProbability: (Double) -> Double) {
    val info = PersonalInfo()
}