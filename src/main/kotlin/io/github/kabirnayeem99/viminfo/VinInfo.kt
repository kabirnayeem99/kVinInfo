package io.github.kabirnayeem99.viminfo

import io.github.kabirnayeem99.viminfo.entities.Vin
import io.github.kabirnayeem99.viminfo.entities.VinDecoded

// Construction
fun String.toVin(): Vin = Vin.fromNumber(this)
fun String.toVinOrNull(): Vin? = Vin.fromNumber(this).takeIf { it.isValid }
fun String.isValidVin(): Boolean = Vin.fromNumber(this).isValid

// Null-safe property access
fun Vin.modelYearsOrNull(): List<Int>? = runCatching { modelYears }.getOrNull()
fun Vin.latestModelYearOrNull(): Int? = runCatching { latestModelYear }.getOrNull()
fun Vin.yearOrNull(): Int? = latestModelYearOrNull()
fun Vin.regionOrNull(): String? = runCatching { region }.getOrNull()
fun Vin.manufacturerOrNull(): String? = runCatching { manufacturer }.getOrNull()
fun Vin.checksumOrNull(): Char? = runCatching { checksum }.getOrNull()
fun Vin.assemblyPlantOrNull(): Char? = runCatching { assemblyPlant }.getOrNull()
fun Vin.serialNumberOrNull(): String? = runCatching { serialNumber }.getOrNull()

// Railway-style: decode everything at once
fun Vin.decode(): Result<VinDecoded> = runCatching {
    VinDecoded(
        vin = this,
        modelYears = modelYears,
        latestModelYear = latestModelYear,
        region = region,
        manufacturer = manufacturer,
        checksum = checksumOrNull(),
        assemblyPlant = assemblyPlant,
        serialNumber = serialNumber,
    )
}

// Java interop facade
object VinInfo {
    @JvmStatic fun parse(number: String): Vin = number.toVin()
    @JvmStatic fun parseOrNull(number: String): Vin? = number.toVinOrNull()
    @JvmStatic fun isValid(number: String): Boolean = number.isValidVin()
}
