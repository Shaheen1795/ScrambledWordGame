package com.example.scrambledwordgame.data
enum class Level{
    L1, L2, L3
}

object Words {

    val wordsList_level1 :List<String> = listOf("abjure","abscond","admonish","bolster","catalyst","condone","eminent","discern","guile","frugal")
    val wordsList_level2 :List<String> = listOf("iconoclast","munificent","opprobrium","salubrious","soporific","ubiquitous","prodigious","prevaricate","perfunctory","innocuous")
    val wordsList_level3 :List<String> = listOf("inimical","indefatigable","fortuitous","bolster","catalyst","condone","eminent","discern","guile","frugal")

    const val LIST_SIZE = 10

    var mapOfWords = mapOf(Pair(Level.L1,wordsList_level1),Pair(Level.L2, wordsList_level2),Pair(Level.L3,wordsList_level3))

    var mapOfMeanings = mapOf(Pair("abjure","verb - to reject or renounce"),Pair("abscond","verb - to hide or conceal"),Pair("admonish","verb - to praise or reward"),Pair("bolster","verb - to shore up or support"),Pair("catalyst","noun - something that speeds up a process or causes action"))

}