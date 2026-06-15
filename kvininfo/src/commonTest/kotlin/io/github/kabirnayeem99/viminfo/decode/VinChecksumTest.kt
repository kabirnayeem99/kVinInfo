package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VinChecksumTest {
    // ── matches — happy path ──────────────────────────────────────────────────

    @Test
    fun `all-ones VIN has numeric check digit 1`() {
        // sum of weights = 89; 89 mod 11 = 1. vin[8] = '1'. Must match.
        assertTrue(VinChecksum.matches("11111111111111111"))
    }

    @Test
    fun `calculate returns 1 for all-ones VIN`() {
        assertEquals('1', VinChecksum.calculate("11111111111111111"))
    }

    @Test
    fun `known valid VIN with numeric check digit matches`() {
        assertTrue(VinChecksum.matches("5GZCZ43D13S812715"))
    }

    // ── check digit 'X' (remainder 10) ───────────────────────────────────────

    @Test
    fun `spec example VIN produces check digit X`() {
        // ISO 3779 worked example: sum 351, 351 mod 11 = 10 → 'X'.
        assertEquals('X', VinChecksum.calculate("1M8GDM9AXKP042788"))
    }

    @Test
    fun `VIN with check digit X matches`() {
        assertTrue(VinChecksum.matches("1M8GDM9AXKP042788"))
    }

    // ── matches — fail path ───────────────────────────────────────────────────

    @Test
    fun `corrupting one non-checkdigit character causes matches to return false`() {
        // Change position 0 from '1' to '2': sum shifts by +8, check digit becomes '7' not 'X'.
        assertFalse(VinChecksum.matches("2M8GDM9AXKP042788"))
    }

    @Test
    fun `VIN with wrong check digit at position 8 does not match`() {
        // "1HGBH41J0MN109186" — the check digit '0' is deliberately wrong.
        assertFalse(VinChecksum.matches("1HGBH41J0MN109186"))
    }

    // ── length guard ─────────────────────────────────────────────────────────

    @Test
    fun `calculate throws for VIN shorter than 17 chars`() {
        assertFailsWith<InvalidVinLengthException> {
            VinChecksum.calculate("WBA3A5G59DNP2608") // 16 chars
        }
    }

    @Test
    fun `calculate throws for VIN longer than 17 chars`() {
        assertFailsWith<InvalidVinLengthException> {
            VinChecksum.calculate("WBA3A5G59DNP26082X") // 18 chars
        }
    }

    @Test
    fun `calculate throws for empty string`() {
        assertFailsWith<InvalidVinLengthException> {
            VinChecksum.calculate("")
        }
    }

    @Test
    fun `matches delegates to calculate so also throws for wrong length`() {
        // Confirm matches never silently returns false on bad length; it throws first.
        assertFailsWith<InvalidVinLengthException> {
            VinChecksum.matches("WBA3A5G59") // 9 chars — too short to even read vin[8]
        }
    }

    // ── position-8 weight is 0 (idempotent to its own slot) ──────────────────

    @Test
    fun `changing only the check-digit slot does not alter calculate result`() {
        // Position 8 weight is 0; any char there contributes 0 to the sum.
        val base = VinChecksum.calculate("1M8GDM9AXKP042788") // vin[8] = 'X'
        val altNum = VinChecksum.calculate("1M8GDM9A1KP042788") // vin[8] = '1'
        val altLet = VinChecksum.calculate("1M8GDM9ABKP042788") // vin[8] = 'B'
        assertEquals(base, altNum)
        assertEquals(base, altLet)
    }

    // ── transliteration table correctness ────────────────────────────────────

    @Test
    fun `known VINs with various letters produce correct check digits`() {
        // Each assertion exercises a different letter's transliteration value.
        // WBA: W=6, B=2, A=1 (confirmed by BMW VIN passing checksum)
        assertEquals('9', VinChecksum.calculate("WBA3A5G59DNP26082"))
        assertTrue(VinChecksum.matches("WBA3A5G59DNP26082"))

        // 1HGBH41JXMN109186 — exercises H=8, G=7, J=1
        assertEquals('X', VinChecksum.calculate("1HGBH41JXMN109186"))
        assertTrue(VinChecksum.matches("1HGBH41JXMN109186"))
    }

    @Test
    fun `letter transliteration values match ISO 3779 table`() {
        // Build a 17-char VIN: target letter at position 0 (weight 8),
        // all '1's elsewhere (except pos 8 = check digit we compute).
        // Template: LETTER + "111111" + CHECK + "11111111"
        // Rest sum (positions 1-7, 9-16, all '1'): 7+6+5+4+3+2+10 + 9+8+7+6+5+4+3+2 = 81
        // expected check digit = (translit(letter)*8 + 81) % 11
        val restSum = 81
        val weights8 = 8

        fun expectedDigit(translit: Int): Char {
            val remainder = (translit * weights8 + restSum) % 11
            return if (remainder == 10) 'X' else remainder.digitToChar()
        }

        fun vinFor(letter: Char): String {
            val expected =
                expectedDigit(
                    when (letter) {
                        'A' -> 1
                        'B' -> 2
                        'C' -> 3
                        'D' -> 4
                        'E' -> 5
                        'F' -> 6
                        'G' -> 7
                        'H' -> 8
                        'J' -> 1
                        'K' -> 2
                        'L' -> 3
                        'M' -> 4
                        'N' -> 5
                        'P' -> 7
                        'R' -> 9
                        'S' -> 2
                        'T' -> 3
                        'U' -> 4
                        'V' -> 5
                        'W' -> 6
                        'X' -> 7
                        'Y' -> 8
                        'Z' -> 9
                        else -> error("unexpected")
                    },
                )
            return "${letter}1111111${expected}11111111"
        }

        val letters =
            listOf(
                'A',
                'B',
                'C',
                'D',
                'E',
                'F',
                'G',
                'H',
                'J',
                'K',
                'L',
                'M',
                'N',
                'P',
                'R',
                'S',
                'T',
                'U',
                'V',
                'W',
                'X',
                'Y',
                'Z',
            )
        for (letter in letters) {
            val vin = vinFor(letter)
            assertTrue(
                VinChecksum.matches(vin),
                "Letter '$letter': VIN $vin should have matching check digit",
            )
        }
    }

    // ── invalid-alphabet chars throw ─────────────────────────────────────────

    @Test
    fun `forbidden letter I throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            VinChecksum.calculate("I" + "1".repeat(15) + "X")
        }
    }

    @Test
    fun `forbidden letter O throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            VinChecksum.calculate("O" + "1".repeat(15) + "X")
        }
    }

    @Test
    fun `forbidden letter Q throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            VinChecksum.calculate("Q" + "1".repeat(15) + "X")
        }
    }

    @Test
    fun `space character throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            VinChecksum.calculate(" " + "1".repeat(15) + "X")
        }
    }

    // ── lowercase throws (no normalisation) ──────────────────────────────────

    @Test
    fun `lowercase letter throws — callers must normalise to uppercase first`() {
        assertFailsWith<IllegalArgumentException> {
            VinChecksum.calculate("a" + "1".repeat(15) + "X")
        }
    }

    // ── sum=0 edge: all-zero VIN ──────────────────────────────────────────────

    @Test
    fun `all-zero VIN has check digit 0`() {
        // Sum = 0, 0 mod 11 = 0 → '0'. vin[8] = '0'. matches must be true.
        assertEquals('0', VinChecksum.calculate("00000000000000000"))
        assertTrue(VinChecksum.matches("00000000000000000"))
    }

    // ── CHECK_DIGIT_INDEX constant ────────────────────────────────────────────

    @Test
    fun `CHECK_DIGIT_INDEX is 8`() {
        assertEquals(8, VinChecksum.CHECK_DIGIT_INDEX)
    }
}
