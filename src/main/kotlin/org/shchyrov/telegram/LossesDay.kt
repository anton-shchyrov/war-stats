package org.shchyrov.telegram

import java.time.LocalDate

class LossesDay(val date: LocalDate, val messageId: Long, val losses: Map<LossesType, Int>) {

    override fun toString(): String = "${date}$SEPARATOR" + LossesType.values()
        .map { losses.getOrDefault(it, 0) }
        .joinToString(SEPARATOR)
        .plus("$SEPARATOR${messageId}")

    companion object {

        private const val SEPARATOR = ";"

        fun headerCaptions(): String = "Date$SEPARATOR" +
                LossesType.values().joinToString(SEPARATOR) +
                "${SEPARATOR}messageId"
    }
}