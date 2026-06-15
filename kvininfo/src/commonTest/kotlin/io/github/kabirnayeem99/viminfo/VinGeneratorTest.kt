package io.github.kabirnayeem99.viminfo

import io.github.kabirnayeem99.viminfo.decode.VinChecksum
import io.github.kabirnayeem99.viminfo.decode.VinFormat
import io.github.kabirnayeem99.viminfo.decode.VinModelYear
import io.github.kabirnayeem99.viminfo.decode.VinRegion
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VinGeneratorTest {

    @Test
    fun generated_vin_is_17_characters() {
        assertEquals(17, VinGenerator.generate().length)
    }

    @Test
    fun generated_vin_uses_valid_alphabet() {
        repeat(200) {
            val vin = VinGenerator.generate()
            assertTrue(VinFormat.isValid(vin), "Invalid VIN alphabet: $vin")
        }
    }

    @Test
    fun generated_vin_has_correct_check_digit() {
        repeat(200) {
            val vin = VinGenerator.generate()
            assertTrue(VinChecksum.matches(vin), "Bad check digit in: $vin")
        }
    }

    @Test
    fun generated_vin_has_valid_region_code() {
        repeat(200) {
            val vin = VinGenerator.generate()
            VinRegion.code(vin) // must not throw
        }
    }

    @Test
    fun generated_vin_has_valid_model_year_code() {
        repeat(200) {
            val vin = VinGenerator.generate()
            VinModelYear.possibleYears(vin) // must not throw
        }
    }

    @Test
    fun vin_info_from_generated_vin_is_fully_valid() {
        repeat(100) {
            VinInfo.fromNumber(VinGenerator.generate()).getOrThrow().use { vi ->
                assertTrue(vi.isFormatValid, "isFormatValid false for: ${vi.vinNumber}")
                assertTrue(vi.isCheckDigitValid, "isCheckDigitValid false for: ${vi.vinNumber}")
                assertTrue(vi.isValid, "isValid false for: ${vi.vinNumber}")
            }
        }
    }

    @Test
    fun vin_info_random_factory_produces_valid_vin() {
        repeat(50) {
            VinInfo.random().use { vi ->
                assertTrue(vi.isValid, "VinInfo.random() produced invalid VIN: ${vi.vinNumber}")
            }
        }
    }

    @Test
    fun seeded_random_produces_deterministic_vin() {
        val seed = 12345L
        val vin1 = VinGenerator.generate(Random(seed))
        val vin2 = VinGenerator.generate(Random(seed))
        assertEquals(vin1, vin2, "Same seed must produce same VIN")
    }

    @Test
    fun multiple_calls_produce_distinct_vins() {
        val distinct = (1..30).map { VinGenerator.generate() }.toSet()
        assertTrue(distinct.size > 1, "Expected distinct VINs across calls")
    }

    @Test
    fun generated_vin_has_valid_year_in_known_range() {
        repeat(100) {
            val vin = VinGenerator.generate()
            val years = VinModelYear.possibleYears(vin)
            assertTrue(years.all { it in 1980..2039 }, "Year out of range: $years for $vin")
        }
    }

    @Test
    fun generated_vin_resolves_manufacturer() {
        repeat(100) {
            VinInfo.fromNumber(VinGenerator.generate()).getOrThrow().use { vi ->
                val m = vi.manufacturer // must not throw InvalidWmiException
                assertNotNull(m, "Null manufacturer for: ${vi.vinNumber}")
                assertTrue(m.isNotBlank(), "Blank manufacturer for: ${vi.vinNumber}")
            }
        }
    }

    @Test
    fun generated_vin_resolves_country() {
        repeat(100) {
            VinInfo.fromNumber(VinGenerator.generate()).getOrThrow().use { vi ->
                val c = vi.country // must not throw InvalidWmiForCountryException
                assertNotNull(c, "Null country for: ${vi.vinNumber}")
                assertTrue(c.isNotBlank(), "Blank country for: ${vi.vinNumber}")
            }
        }
    }
}
