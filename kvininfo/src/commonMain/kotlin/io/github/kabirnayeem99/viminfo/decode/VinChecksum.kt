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

    // ISO 3779 letter-to-number transliteration table, indexed by (char - 'A').
    // 0 = invalid (I, O, Q are excluded from the VIN alphabet).
    private val transliterationTable = intArrayOf(
        1, 2, 3, 4, 5, 6, 7, 8, // A-H
        0,                        // I (invalid)
        1, 2, 3, 4, 5,           // J-N
        0,                        // O (invalid)
        7,                        // P
        0,                        // Q (invalid)
        9,                        // R
        2, 3, 4, 5, 6, 7, 8, 9, // S-Z
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

        var sum = 0
        for (i in 0 until VinFormat.VIN_LENGTH) {
            sum += transliterate(vin[i]) * positionWeights[i]
        }

        val remainder = sum % CHECKSUM_MODULUS
        return if (remainder == X_CHECKSUM_VALUE) X_CHECK_DIGIT else remainder.digitToChar()
    }

    /** Whether the check digit in [vin] matches the value computed from the rest of the VIN. */
    fun matches(vin: String): Boolean = calculate(vin) == vin[CHECK_DIGIT_INDEX]

    private fun transliterate(char: Char): Int {
        val digit = char.digitToIntOrNull()
        if (digit != null) return digit
        val idx = char - 'A'
        if (idx < 0 || idx >= transliterationTable.size)
            throw IllegalArgumentException("Invalid VIN character: '$char'")
        val value = transliterationTable[idx]
        if (value == 0) throw IllegalArgumentException("Invalid VIN character: '$char'")
        return value
    }
}
