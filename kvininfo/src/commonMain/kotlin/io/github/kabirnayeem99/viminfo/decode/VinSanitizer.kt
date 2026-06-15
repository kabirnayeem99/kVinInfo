package io.github.kabirnayeem99.viminfo.decode

object VinSanitizer {
    /**
     * Sanitizes a VIN string by trimming whitespace, removing hyphens and internal spaces,
     * converting to uppercase, and substituting ambiguous letters (I -> 1, O -> 0, Q -> 0).
     *
     * @param vin The raw VIN string to sanitize.
     * @return The sanitized VIN string.
     */
    fun sanitize(vin: String): String = buildString {
        for (c in vin.trim()) {
            when {
                c == '-' || c.isWhitespace() -> {}
                c == 'O' || c == 'o' -> append('0')
                c == 'Q' || c == 'q' -> append('0')
                c == 'I' || c == 'i' -> append('1')
                else -> append(c.uppercaseChar())
            }
        }
    }
}
