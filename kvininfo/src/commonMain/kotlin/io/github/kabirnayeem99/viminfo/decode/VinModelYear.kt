package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.data.YEAR_CODE_CYCLE_LENGTH
import io.github.kabirnayeem99.viminfo.data.yearCodeBaseValues
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException

/**
 * Model-year decoding from VIN position 10, disambiguated by position 7.
 */
internal object VinModelYear {

    private const val YEAR_CHARACTER_INDEX = 9
    private const val POSITION_SEVEN_INDEX = 6

    /**
     * The character at position 10 that encodes the model year.
     *
     * @throws InvalidVinLengthException If the VIN is too short.
     */
    fun yearCharacter(vin: String): Char =
        vin.getOrNull(YEAR_CHARACTER_INDEX) ?: throw InvalidVinLengthException(vin)

    /**
     * All calendar years this VIN's year code could represent (both 30-year cycles).
     *
     * Returns exactly two years: the base year (1980–2009 cycle) and base + 30 (2010–2039 cycle).
     * Use this when you cannot rely on the position-7 heuristic in [decode].
     *
     * @throws InvalidVinLengthException If the VIN is too short to read position 10.
     * @throws InvalidVinYearException If the year character is not a valid model-year code.
     */
    fun possibleYears(vin: String): List<Int> {
        val yearChar = yearCharacter(vin)
        val baseYear = yearCodeBaseValues[yearChar] ?: throw InvalidVinYearException(yearChar)
        return listOf(baseYear, baseYear + YEAR_CODE_CYCLE_LENGTH)
    }

    /**
     * Decodes the model year using a position-7 heuristic to disambiguate the 30-year cycle.
     *
     * A numeric position 7 selects the 1980–2009 cycle; an alphabetic position 7 selects the
     * 2010–2039 cycle. This heuristic is unreliable for vehicles whose VDS happens to place a
     * digit at position 7 while belonging to the newer cycle. Use [possibleYears] when you need
     * both candidates.
     *
     * @throws InvalidVinLengthException If the VIN is too short to read position 7.
     * @throws InvalidVinYearException If the year character is not a valid model-year code.
     */
    fun decode(vin: String): Int {
        val yearChar = yearCharacter(vin)
        val baseYear = yearCodeBaseValues[yearChar] ?: throw InvalidVinYearException(yearChar)
        val positionSeven =
            vin.getOrNull(POSITION_SEVEN_INDEX) ?: throw InvalidVinLengthException(vin)
        return if (positionSeven.isDigit()) baseYear else baseYear + YEAR_CODE_CYCLE_LENGTH
    }
}
