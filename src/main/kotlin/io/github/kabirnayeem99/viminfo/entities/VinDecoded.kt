package io.github.kabirnayeem99.viminfo.entities

data class VinDecoded(
    val vin: Vin,
    val modelYears: List<Int>,
    val latestModelYear: Int,
    val region: String,
    val manufacturer: String,
    val checksum: Char?,
    val assemblyPlant: Char,
    val serialNumber: String,
)
