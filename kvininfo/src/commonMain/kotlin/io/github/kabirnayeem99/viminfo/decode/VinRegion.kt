package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinRegionCharException

/**
 * Region decoding from the first character of a VIN, per the ISO 3780 region blocks:
 * A–C Africa, H–R Asia, E and S–Z Europe, 1–5 and 7 North America, 6 Oceania, 8–9 South America.
 */
internal object VinRegion {

    private val names = mapOf(
        "AF" to "Africa",
        "AS" to "Asia",
        "EU" to "Europe",
        "NA" to "North America",
        "OC" to "Oceania",
        "SA" to "South America",
    )

    /**
     * The two-letter region code derived from the VIN's first character.
     *
     * @throws InvalidVinLengthException If the VIN is empty.
     * @throws InvalidVinRegionCharException If the first character is not an assigned region code.
     */
    fun code(vin: String): String {
        val regionChar = vin.getOrNull(0) ?: throw InvalidVinLengthException(vin)
        return when (regionChar) {
            in 'A'..'C' -> "AF"
            'E', 'F', 'G' -> "EU"  // E=Russia, F=France, G=Great Britain per ISO 3780
            in 'H'..'R' -> "AS"
            in 'S'..'Z' -> "EU"
            in '1'..'5' -> "NA"
            '6' -> "OC"
            '7' -> "NA"
            in '8'..'9' -> "SA"
            else -> throw InvalidVinRegionCharException(regionChar.toString())
        }
    }

    /**
     * The human-readable region name.
     *
     * @throws IllegalArgumentException If the region code is not recognised.
     */
    fun name(vin: String): String =
        names[code(vin)] ?: throw IllegalArgumentException("Wrong region code.")

    /**
     * Whether the check digit is mandatory for [vin]'s region.
     *
     * Compulsory only in North America (first character 1–5 or 7) and China (first character `H`
     * or `L`); optional everywhere else (e.g. Europe).
     *
     * @throws InvalidVinLengthException If the VIN is empty.
     * @throws InvalidVinRegionCharException If the first character is not an assigned region code.
     */
    fun requiresCheckDigit(vin: String): Boolean {
        val regionChar = vin.getOrNull(0) ?: throw InvalidVinLengthException(vin)
        return regionChar in '1'..'5' || regionChar == '7' || regionChar == 'H' || regionChar == 'L'
    }
}
