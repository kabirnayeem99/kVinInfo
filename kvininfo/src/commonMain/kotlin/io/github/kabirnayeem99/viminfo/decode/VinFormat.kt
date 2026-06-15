package io.github.kabirnayeem99.viminfo.decode

/**
 * Format-level checks for a normalized VIN string.
 */
internal object VinFormat {

    /** The fixed length of an ISO 3779 VIN. */
    const val VIN_LENGTH = 17

    // VINs exclude the letters I, O and Q to avoid confusion with 1, 0 and 9.
    private val validVinRegex = "^[A-HJ-NPR-Z0-9]{17}$".toRegex()

    /** Whether [vin] is exactly 17 characters from the allowed VIN alphabet. */
    fun isValid(vin: String): Boolean = validVinRegex.matches(vin)
}
