package org.shchyrov.telegram

import java.time.LocalDate

class LossesDay(val date: LocalDate, val losses: Map<LossesType, Int>) {

    override fun toString(): String = "${date.toString()}$SEPARATOR" + LossesType.values()
        .map { losses.getOrDefault(it, 0) }
        .joinToString(SEPARATOR)

    companion object {

        private const val SEPARATOR = ";"

        fun headerCaptions(): String = "Date$SEPARATOR" + LossesType.values().joinToString(SEPARATOR)
    }
}