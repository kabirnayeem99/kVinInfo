package io.github.kabirnayeem99.viminfo.entities

import io.github.kabirnayeem99.viminfo.data.manufacturers
import io.github.kabirnayeem99.viminfo.data.years
import io.github.kabirnayeem99.viminfo.exceptions.ChecksumNotAvailableException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinRegionCharException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException
import io.github.kabirnayeem99.viminfo.exceptions.UnknownManufacturerException

private fun String.normalize() = trim().uppercase().substringBefore("-")

data class Vin(val number: String) {

    private val normalizedNumber: String = number.normalize()
    val wmi: String = if (normalizedNumber.length >= 3) normalizedNumber.substring(0, 3) else ""
    val vds: String = if (normalizedNumber.length >= 9) normalizedNumber.substring(3, 9) else ""
    val vis: String = if (normalizedNumber.length >= 17) normalizedNumber.substring(9, 17) else ""

    val isValid: Boolean
        get() = VALID_VIN_REGEX.matches(normalizedNumber) && normalizedNumber.length == 17

    val modelYears: List<Int>
        get() = years[yearCharacter] ?: throw InvalidVinYearException(yearCharacter)

    val latestModelYear: Int
        get() = modelYears.last()

    val year: Int
        get() = latestModelYear

    val region: String
        get() = when (normalizedNumber.firstOrNull()?.uppercaseChar()) {
            in 'A'..'H' -> "AF"
            in 'J'..'R' -> "AS"
            in 'S'..'Z' -> "EU"
            in '1'..'5' -> "NA"
            in '6'..'7' -> "OC"
            in '8'..'9' -> "SA"
            null -> throw InvalidVinLengthException(normalizedNumber)
            else -> throw InvalidVinRegionCharException(normalizedNumber[0].toString())
        }

    val manufacturer: String
        get() {
            if (wmi.isBlank()) throw InvalidVinLengthException(normalizedNumber)
            return manufacturers[wmi]
                ?: manufacturers[wmi.take(2)]
                ?: throw UnknownManufacturerException(wmi)
        }

    val checksum: Char
        get() = if (region != "EU") normalizedNumber[8]
                else throw ChecksumNotAvailableException("EU")

    val assemblyPlant: Char
        get() = normalizedNumber.getOrNull(10)
            ?: throw InvalidVinLengthException(normalizedNumber)

    val serialNumber: String
        get() = normalizedNumber.substring(12, 17)

    private val yearCharacter: Char
        get() = normalizedNumber.getOrNull(9) ?: throw InvalidVinLengthException(normalizedNumber)

    override fun toString() = normalizedNumber.ifEmpty { number }

    companion object {
        private val VALID_VIN_REGEX = "^[a-zA-Z0-9]+$".toRegex()

        @JvmStatic
        fun fromNumber(number: String): Vin = Vin(number)
    }
}
