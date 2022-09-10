package org.shchyrov.telegram

class MessageParser {

    private val parsers = listOf(
        MessageItem("особового складу", LossesType.HUMANS),
        MessageItem("танків", LossesType.TANKS),
        MessageItem("[Бб]ойових броньованих машин|ББМ", LossesType.ARMORED_VEHICLES),
        MessageItem("артилерійських систем", LossesType.ARTILLERY),
        MessageItem("РСЗВ", LossesType.MLRS),
        MessageItem("засоби ППО", LossesType.AIR_DEFENCE),
        MessageItem("літаків", LossesType.AIRPLANES),
        MessageItem("гелікоптерів", LossesType.HELICOPTERS),
        MessageItem("БПЛА", LossesType.UAVS),
        MessageItem("ракети", LossesType.MISSILES),
        MessageItem("катери", LossesType.BOATS),
        // автомобільної техніки та автоцистерн
        // автомобільної техніки + цистерн з ПММ
        MessageItem("техніки|цистерн", LossesType.VEHICLES),
        MessageItem("спеціальна техніка", LossesType.SPECIAL_EQUIPMENTS),
    )

    fun parse(message: String): Map<LossesType, Int> =
        message.split('\n')
            .mapNotNull { parseLine(it) }
            .groupingBy { it.first }
            .aggregate { _, accum, el, _ -> el.second + (accum ?: 0) }

    private fun parseLine(line: String): Pair<LossesType, Int>? = parsers
        .mapNotNull { parser -> parser.parse(line)?.let { parser.lossesTypeEnum to it } }
        .firstOrNull()

    data class MessageItem(
        val prefix: String,
        val lossesTypeEnum: LossesType
    ) {

        private val regex = Regex("($prefix) .*?[-‒–] ?.*?(\\d+)")

        fun parse(string: String): Int? =
            regex.find(string)?.groups?.get(2)?.value?.toInt()
    }
}

fun main() {
    val message1 = "\uD83D\uDD25 Загальні бойові #втрати противника з 24.02 по 21.08 орієнтовно склали:\n" +
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

    val message2 = "❗️Загальні бойові #втрати противника з 24.02 по 30.04 орієнтовно склали:\n" +
            "\n" +
            "• особового складу - ліквідовано близько 23200 (+200), \n" +
            "• танків ‒ 1008 (+22),\n" +
            "• бойових броньованих машин  ‒ 2445 (+27),\n" +
            "• артилерійських систем – 436 (+1),\n" +
            "• РСЗВ - 151,\n" +
            "• засоби ППО - 77 (+4),\n" +
            "• літаків – 190 (+1),\n" +
            "• гелікоптерів – 155,\n" +
            "• автомобільної техніки - 1701 (+6),\n" +
            "• кораблі /катери - 8,\n" +
            "• цистерн з ПММ - 76, \n" +
            "• БПЛА оперативно-тактичного рівня - 232 (+3),\n" +
            "• спеціальна техніка - 32 (+1),\n" +
            "• пускові установки ОТРК/ТРК - 4.\n" +
            "\n" +
            "Генштаб\n" +
            "\n" +
            "В скобках - изменение за сутки.\n" +
            "___\n" +
            "Юбилей - 1000 танков \uD83E\uDD73\n"
//    val item = MessageParser.MessageItem("танків", Losses.TANKS);
//    println(item.parse("• танків ‒ 1912 (+5)"))

    val parser = MessageParser()
    val res = parser.parse(message2)
    println("[${res.size}]$res")
}