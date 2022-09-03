package org.shchyrov.telegram

class MessageParser {

    private val parsers = listOf(
        MessageItem("особового складу", Losses.HUMANS),
        MessageItem("танків", Losses.TANKS),
        MessageItem("бойових броньованих машин", Losses.ARMORED_VEHICLES),
        MessageItem("артилерійських систем", Losses.ARTILLERY),
        MessageItem("РСЗВ", Losses.MLRS),
        MessageItem("засоби ППО", Losses.AIR_DEFENCE),
        MessageItem("літаків", Losses.AIRPLANES),
        MessageItem("гелікоптерів", Losses.HELICOPTERS),
        MessageItem("БПЛА", Losses.UAVS),
        MessageItem("ракети", Losses.MISSILES),
        MessageItem("катери", Losses.BOATS),
        MessageItem("автоцистерн", Losses.VEHICLES),
        MessageItem("спеціальна техніка", Losses.SPECIAL_EQUIPMENTS),
    )

    fun parse(message: String): List<ParsedItem> =
        message.split('\n').mapNotNull { parseLine(it) }

    private fun parseLine(line: String): ParsedItem? = parsers
        .mapNotNull { parser -> parser.parse(line)?.let { ParsedItem(parser.lossesEnum, it) } }
        .firstOrNull()

    data class MessageItem(
        val prefix: String,
        val lossesEnum: Losses
    ) {

        private val regex = Regex("$prefix .*?[-‒–] .*?(\\d+)")

        fun parse(string: String): Int? =
            regex.find(string)?.groups?.get(1)?.value?.toInt()
    }

    data class ParsedItem(
        val lossesEnum: Losses,
        val count: Int,
    )
}

fun main() {
    val message = "\uD83D\uDD25 Загальні бойові #втрати противника з 24.02 по 21.08 орієнтовно склали:\n" +
            "\n" +
            "• особового складу - ліквідовано близько 45200 (+300)\n" +
            "• танків ‒ 1912 (+5)\n" +
            "• бойових броньованих машин ‒ 4224 (+12)\n" +
            "• артилерійських систем – 1028 (+10)\n" +
            "• РСЗВ - 266\n" +
            "• засоби ППО - 141\n" +
            "• літаків – 234\n" +
            "• гелікоптерів – 197\n" +
            "• БПЛА оперативно-тактичного рівня - 806 (+3)\n" +
            "• крилаті ракети - 190\n" +
            "• кораблі/катери - 15\n" +
            "• автомобільної техніки та автоцистерн - 3143 (+6)\n" +
            "• спеціальна техніка - 99 (+2)\n" +
            "\n" +
            "Найбільших втрат противник зазнав на Донецькому та Миколаївському напрямках.\n" +
            "\n" +
            "Генштаб"
//    val item = MessageParser.MessageItem("танків", Losses.TANKS);
//    println(item.parse("• танків ‒ 1912 (+5)"))

    val parser = MessageParser()
    val res = parser.parse(message)
    println("[${res.size}]$res")
}