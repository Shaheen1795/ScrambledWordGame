package com.fairytale.scrambledwordgame.data
enum class Level{
    L1
}

object Words {

    val wordsList_level1 :List<String> = listOf("abjure","abscond","admonish","bolster","catalyst","condone","eminent","discern","guile","frugal",
     "iconoclast","munificent","opprobrium","salubrious","soporific","ubiquitous","prodigious","prevaricate","perfunctory","innocuous",
    "inimical","indefatigable","fortuitous","bolster","catalyst","condone","eminent","discern","guile","frugal")

    var mapOfWords = mapOf(Pair(Level.L1,wordsList_level1))

    var mapOfMeanings = mapOf(
        Pair("abjure","verb - to reject or renounce"),
        Pair("abscond","verb - to hide or conceal"),
        Pair("admonish","verb - to praise or reward"),
        Pair("bolster","verb - to shore up or support"),
        Pair("catalyst","noun - something that speeds up a process or causes action"),
        Pair("discern","verb - To differentiate"),
        Pair("condone", "verb -  to approve or allow"),
        Pair("guile", "noun - craftiness and cunning"),
        Pair("frugal", "adj - economical, thrifty"),
        Pair("eminent", "adj - well-known, respected, distinguished"),
        Pair("iconoclast", "noun - a person who attacks traditional religious and cultural institutions"),
        Pair("munificent", "adj -very generous"),
        Pair("opprobrium", "noun - criticism or condemnation"),
        Pair("salubrious", "adj - health-promoting"),
        Pair("soporific", "adj - makes sleepy"),
        Pair("ubiquitous", "adj - ever-present or universal"),
        Pair("prodigious", "adj - enormous, immense, gigantic"),
        Pair("prevaricate", "verb - to evade or deceive without outright lying"),
        Pair("perfunctory", "adj - done without much effort, care, or thought"),
        Pair("innocuous", "adj - harmless"),
        Pair("inimical", "adj - harmful or hostile"),
        Pair("indefatigable", "adj - cannot be made tired"),
        Pair("fortuitous", "adj - fortunate and lucky"),
    )

}