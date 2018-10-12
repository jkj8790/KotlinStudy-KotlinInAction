
data class Person(val name: String, val number: Int, var cash: Int = 0)

fun List<Person>.findNumber(number: Int): Person? {
    return this.firstOrNull { it.number == number }
}

object PersonGenerator {
    val faker = Faker(Locale("ko"))

    fun generatePeople(size: Int): List<Person> {
        return generatePeople(size, null)
    }

    fun generatePeople(size: Int, winner: Person?): List<Person> {
        if (size < 7) {
            throw RuntimeException("size는 7이상 이여야 합니다.")
        }
        val personList = mutableListOf<Person>()
        for (index in 1..size) {
            val name = faker.name().fullName()
            val number = index
            personList.add(Person(name, number))
        }
        winner?.let { personList.add(winner) }
        return personList.subList(0, size - 2).shuffled()
    }
}

sealed class SevenFindGame {
    class WinGame(winner: Person) : SevenFindGame() {
        val CASH = 10000

        companion object {
            var winner: Person? = null
        }

        init {
            if (WinGame.winner == null) {
                WinGame.winner = winner
            }
            WinGame.winner?.let {
                if (it.cash <= 0) {
                    it.cash = CASH
                } else {
                    it.cash *= 2
                }

                if (it != winner) {
                    winner.cash += it.cash
                    it.cash = 0
                }
            }
            WinGame.winner = winner
        }

        constructor(winner: Person, size: Int) : this(winner) {
            val people: List<Person> = PersonGenerator.generatePeople(size, winner)
            startGame(size, people)
        }

    }

    class LoseGame(people: List<Person>) : SevenFindGame() {
        init {
            people.forEach {
                it.cash = 0
            }
        }
    }



    companion object {
        fun startGame(size: Int) {
            val people: List<Person> = PersonGenerator.generatePeople(size)
            startGame(size, people)
        }

        private fun startGame(size: Int, people: List<Person>) {
            val winner = people.findNumber(7)!!
            if (size / 2 > 7) {
                WinGame(winner, size / 2)
            } else {
                WinGame(winner)
                println("winner : $winner")
            }
            LoseGame(people.filter { it != winner })
        }
    }
}


fun main(args: Array<String>) {
    SevenFindGame.startGame(100)
}
