package io.github.kabirnayeem99.viminfo

import io.github.kabirnayeem99.viminfo.data.getCountryFromWmi
import io.github.kabirnayeem99.viminfo.data.manufacturers
import io.github.kabirnayeem99.viminfo.data.yearCodeBaseValues
import io.github.kabirnayeem99.viminfo.decode.VinChecksum
import kotlin.random.Random

/**
 * Generates syntactically valid VIN strings.
 *
 * Each generated VIN:
 * - Uses a real 3-character WMI from the manufacturers dataset (guarantees manufacturer +
 *   country resolution)
 * - Has a valid model-year code at position 10
 * - Has a correct ISO 3779 check digit at position 9
 */
object VinGenerator {

    // VIN alphabet: A-Z excluding I, O, Q; plus 0-9
    private const val VIN_ALPHABET = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789"

    // Position 10 (idx 9): valid model-year codes from the data layer
    private val VALID_YEAR_CODES: List<Char> by lazy { yearCodeBaseValues.keys.toList() }

    // Real 3-char WMIs from the manufacturers dataset:
    //   - length == 3: skip 2-char prefix entries and 6-char small-volume keys
    //   - [2] != '9': skip small-volume manufacturer markers (their WMI 3rd char is '9')
    //   - [0] != 'D' && [0] != '0': skip the two VIN-alphabet chars that have no region mapping
    private val REAL_WMIS: List<String> by lazy {
        manufacturers.keys
            .filterIsInstance<String>()
            .filter { it.length == 3 && it[2] != '9' && it[0] != 'D' && it[0] != '0' && it.all { c -> c in VIN_ALPHABET } }
            .filter { wmi -> runCatching { getCountryFromWmi(wmi) }.isSuccess }
            .distinct()
    }

    /**
     * Generates a random valid VIN.
     *
     * @param random The [Random] source to use. Pass a seeded [Random] for reproducible output.
     * @return A 17-character VIN string that passes format, check-digit, manufacturer, and
     *   country validation.
     */
    fun generate(random: Random = Random.Default): String {
        require(REAL_WMIS.isNotEmpty()) { "REAL_WMIS list is empty" }
        require(VALID_YEAR_CODES.isNotEmpty()) { "VALID_YEAR_CODES list is empty" }

        val chars = CharArray(17)

        // Positions 1–3 (idx 0–2): real WMI — guarantees manufacturer + country resolution
        val wmi = REAL_WMIS.random(random)
        chars[0] = wmi[0]
        chars[1] = wmi[1]
        chars[2] = wmi[2]

        // Positions 4–8 (idx 3–7): free VIN-alphabet chars
        for (i in 3..7) {
            chars[i] = VIN_ALPHABET.random(random)
        }

        // Position 9 (idx 8): placeholder — positional weight is 0, doesn't affect the sum
        chars[8] = '0'

        // Position 10 (idx 9): valid model-year code
        chars[9] = VALID_YEAR_CODES.random(random)

        // Positions 11–17 (idx 10–16): free VIN-alphabet chars
        for (i in 10..16) {
            chars[i] = VIN_ALPHABET.random(random)
        }

        // Calculate and insert the correct check digit
        chars[8] = VinChecksum.calculate(chars.concatToString())

        return chars.concatToString()
    }
}
