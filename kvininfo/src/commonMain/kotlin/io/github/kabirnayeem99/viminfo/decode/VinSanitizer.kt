package io.github.kabirnayeem99.viminfo.decode

object VinSanitizer {
    /**
     * Sanitizes a VIN string by trimming whitespace, removing hyphens and internal spaces,
     * converting to uppercase, and substituting ambiguous letters (I -> 1, O -> 0, Q -> 0).
     *
     * @param vin The raw VIN string to sanitize.
     * @return The sanitized VIN string.
     */
    fun sanitize(vin: String): String = vin
        .trim()
        .replace(Regex("[\\s\\-]"), "")
        .uppercase()
        .replace('O', '0')
        .replace('Q', '0')
        .replace('I', '1')
}
