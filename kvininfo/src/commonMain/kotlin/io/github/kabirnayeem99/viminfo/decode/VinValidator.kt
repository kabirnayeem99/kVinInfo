package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.data.yearCodeBaseValues
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException
import io.github.kabirnayeem99.viminfo.exceptions.VinChecksumMismatchException

/**
 * Internal utility for validating sanitized VIN strings.
 */
internal object VinValidator {

    /**
     * Validates an already sanitized VIN string.
     *
     * @param vin The sanitized VIN string (17 chars, uppercase, no spaces/hyphens, I/O/Q substituted).
     * @return An [InvalidVinException] if the VIN is invalid, or null if it is valid.
     */
    fun validate(vin: String): InvalidVinException? {
        // 1. Blank check
        if (vin.isBlank()) return InvalidVinLengthException(vin)

        // 2. Length check
        if (vin.length != VinFormat.VIN_LENGTH) {
            return InvalidVinLengthException("VIN must be exactly 17 characters, got ${vin.length}: '$vin'")
        }

        // 3. Character set check (A-H, J-N, P-Z, 0-9)
        val invalidChars = vin.filter { !it.isValidVinChar() }
        if (invalidChars.isNotEmpty()) {
            return InvalidVinException("VIN '$vin' contains invalid characters: ${invalidChars.toSet()}")
        }

        // 4. Region code check
        try {
            VinRegion.code(vin)
        } catch (e: Exception) {
            return InvalidVinException("VIN '$vin' has invalid region code '${vin[0]}': ${e.message}")
        }

        // 5. Year character check (position 10, index 9)
        val yearChar = vin[9]
        if (yearChar !in yearCodeBaseValues) {
            return InvalidVinYearException(yearChar)
        }

        // 6. Checksum check (region-conditional)
        if (VinRegion.requiresCheckDigit(vin) && !VinChecksum.matches(vin)) {
            val expected = VinChecksum.calculate(vin)
            val actual = vin[VinChecksum.CHECK_DIGIT_INDEX]
            return VinChecksumMismatchException(vin, expected, actual)
        }

        return null
    }

    private fun Char.isValidVinChar(): Boolean =
        this in 'A'..'H' || this in 'J'..'N' || this in 'P'..'Z' || this.isDigit()
}
