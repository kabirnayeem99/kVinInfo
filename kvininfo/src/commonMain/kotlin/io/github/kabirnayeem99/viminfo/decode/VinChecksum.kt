package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException

/**
 * ISO 3779 check-digit (position 9) computation and verification.
 */
internal object VinChecksum {

    /** Zero-based index of the check digit within the VIN. */
    const val CHECK_DIGIT_INDEX = 8

    private const val CHECKSUM_MODULUS = 11
    private const val X_CHECKSUM_VALUE = 10
    private const val X_CHECK_DIGIT = 'X'

    // ISO 3779 positional weights; index 8 (the check digit) carries weight 0.
    private val positionWeights = intArrayOf(8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2)

    // ISO 3779 letter-to-number transliteration table.
    private val transliterationValues = mapOf(
        'A' to 1, 'B' to 2, 'C' to 3, 'D' to 4, 'E' to 5, 'F' to 6, 'G' to 7, 'H' to 8,
        'J' to 1, 'K' to 2, 'L' to 3, 'M' to 4, 'N' to 5, 'P' to 7, 'R' to 9,
        'S' to 2, 'T' to 3, 'U' to 4, 'V' to 5, 'W' to 6, 'X' to 7, 'Y' to 8, 'Z' to 9,
    )

    /**
     * Calculates the expected check digit for [vin].
     *
     * Each character is transliterated to a numeric value, multiplied by its positional weight,
     * summed, and reduced modulo 11. A remainder of 10 is represented by the character `X`.
     *
     * @throws InvalidVinLengthException If the VIN is not 17 characters long.
     */
    fun calculate(vin: String): Char {
        if (vin.length != VinFormat.VIN_LENGTH) throw InvalidVinLengthException(vin)

        val sum = vin.foldIndexed(0) { index, total, char ->
            total + transliterate(char) * positionWeights[index]
        }

        val remainder = sum % CHECKSUM_MODULUS
        return if (remainder == X_CHECKSUM_VALUE) X_CHECK_DIGIT else remainder.digitToChar()
    }

    /** Whether the check digit in [vin] matches the value computed from the rest of the VIN. */
    fun matches(vin: String): Boolean = calculate(vin) == vin[CHECK_DIGIT_INDEX]

    private fun transliterate(char: Char): Int =
        char.digitToIntOrNull() ?: transliterationValues[char]
        ?: throw IllegalArgumentException("Invalid VIN character: '$char'")
}
