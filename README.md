# kVinInfo

A pure Kotlin Multiplatform library for VIN (Vehicle Identification Number) decoding, validation, and NHTSA API integration.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kabirnayeem99/kvininfo)](https://central.sonatype.com/artifact/io.github.kabirnayeem99/kvininfo)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-blue)](https://kotlinlang.org)

**Targets:** Android · iOS (arm64, x64, simulatorArm64) · JVM · Linux x64

> For production-grade VIN processing, complement this library with your own business logic or country-specific databases. VIN standards vary by region and manufacturer.

---

## Features

- **Offline decoding** — WMI, VDS, VIS, region, country, manufacturer, model year, assembly plant, serial number
- **Validation** — format, character set, region code, model-year code, ISO 3779 check digit (mandatory for NA/China, optional elsewhere)
- **NHTSA integration** — make, model, vehicle type, body class via the US NHTSA API (suspend fns, coroutines)
- **VIN generation** — generates syntactically valid VINs with real WMIs and correct check digits
- **Resource-safe API** — `VinInfo` implements `AutoCloseable`; use `.use {}` or `withVinInfo {}`

---

## Install

### Kotlin Multiplatform

```kotlin
// commonMain
implementation("io.github.kabirnayeem99:kvininfo:2.0.0")
```

### Android (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.kabirnayeem99:kvininfo:2.0.0")
}
```

### Android (Groovy DSL)

```groovy
dependencies {
    implementation "io.github.kabirnayeem99:kvininfo:2.0.0"
}
```

---

## Quick Start

### Decode a VIN

```kotlin
val result = VinInfo.fromNumber("WBA3A5G59DNP26082")

result.onSuccess { vin ->
    println(vin.vinNumber)       // WBA3A5G59DNP26082
    println(vin.wmi)             // WBA
    println(vin.vds)             // 3A5G59
    println(vin.vis)             // DNP26082
    println(vin.year)            // 2013
    println(vin.region)          // Europe
    println(vin.country)         // Germany
    println(vin.manufacturer)    // BMW AG
    println(vin.assemblyPlant)   // N
    println(vin.serialNumber)    // P26082
    println(vin.isValid)         // true
}

result.onFailure { e ->
    println("Invalid VIN: ${e.message}")
}
```

### Extension function (concise style)

```kotlin
"WBA3A5G59DNP26082".withVinInfo {
    println(year)            // 2013
    println(region)          // Europe
    println(manufacturer)    // BMW AG
}
// VinInfo is automatically closed after the block
```

### Resource-safe with `use`

```kotlin
VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow().use { vin ->
    println(vin.year)
    println(vin.manufacturer)
}
```

### NHTSA API (suspend, US VINs)

```kotlin
VinInfo.fromNumber("1HGCM82633A004352").getOrThrow().use { vin ->
    println(vin.getMakeFromNhtsa())          // HONDA
    println(vin.getModelFromNhtsa())         // Accord
    println(vin.getVehicleTypeFromNhtsa())   // PASSENGER CAR
    println(vin.getBodyClassFromNhtsa())     // Sedan/Saloon
    println(vin.isValidByNhtsa())            // Result<String>
    println(vin.toJsonString())              // full JSON from NHTSA
}
```

> NHTSA functions are `suspend` — call from a coroutine or `runBlocking`. Optimized for US VINs; accuracy varies for other regions.

### Generate a valid VIN

```kotlin
val vin = VinInfo.random()
println(vin.vinNumber)   // e.g. "WBA4X3C09K1234567"
println(vin.isValid)     // true
```

For reproducible output (tests):

```kotlin
val vin = VinInfo.fromNumber(VinGenerator.generate(Random(seed = 42))).getOrThrow()
```

---

## API Reference

### `VinInfo`

| Member | Type | Description |
|---|---|---|
| `vinNumber` | `String` | Normalized 17-char VIN |
| `wmi` | `String` | World Manufacturer Identifier (chars 1–3) |
| `vds` | `String` | Vehicle Descriptor Section (chars 4–9) |
| `vis` | `String` | Vehicle Identification Section (chars 10–17) |
| `year` | `Int` | Model year decoded from position 10 |
| `region` | `String` | Region name (e.g. `"Europe"`, `"North America"`) |
| `regionCode` | `String` | Region code (e.g. `"EU"`, `"NA"`) |
| `country` | `String?` | Country derived from WMI, or null if unknown |
| `manufacturer` | `String?` | Manufacturer name from WMI, or null if unknown |
| `isSmallVolumeManufacturer` | `Boolean` | True if WMI 3rd char is `9` (<500 vehicles/year) |
| `assemblyPlant` | `Char` | Assembly plant code (position 11) |
| `serialNumber` | `String` | Production serial (positions 12–17, or 15–17 for small-volume) |
| `checksum` | `Char` | Check digit at position 9 (throws for EU region) |
| `calculatedChecksum` | `Char` | ISO 3779 computed check digit |
| `isFormatValid` | `Boolean` | 17 chars, valid alphabet (no I/O/Q) |
| `isCheckDigitRequired` | `Boolean` | True for NA and China regions |
| `isCheckDigitValid` | `Boolean` | Computed check digit matches position 9 |
| `isValid` | `Boolean` | Format valid + check digit valid where required |
| `isValidByNhtsa()` | `suspend Result<String>` | Validates against NHTSA database |
| `getMakeFromNhtsa()` | `suspend String` | Make (e.g. `"BMW"`) |
| `getModelFromNhtsa()` | `suspend String` | Model (e.g. `"328i"`) |
| `getVehicleTypeFromNhtsa()` | `suspend String` | Vehicle type (e.g. `"PASSENGER CAR"`) |
| `getBodyClassFromNhtsa()` | `suspend String` | Body class (e.g. `"Sedan/Saloon"`) |
| `toJsonString()` | `suspend String` | Full NHTSA response as JSON |

### `VinInfo` companion

| Member | Description |
|---|---|
| `VinInfo.fromNumber(vin: String): Result<VinInfo>` | Parse a VIN; returns `Result.failure` with a typed exception on error |
| `VinInfo.random(): VinInfo` | Generate a random valid VIN |
| `String.withVinInfo { }` | Extension: parse + run block + auto-close |

### `VinGenerator`

```kotlin
VinGenerator.generate()                        // random valid VIN
VinGenerator.generate(random = Random(seed))   // seeded, reproducible
```

---

## Error Handling

`VinInfo.fromNumber` returns `Result.failure` — never throws. The exception type indicates what failed:

| Exception | Cause |
|---|---|
| `InvalidVinLengthException` | Not exactly 17 characters |
| `InvalidVinException` | Invalid characters (I, O, Q or non-alphanumeric) |
| `InvalidVinRegionCharException` | First character is not a valid region code |
| `InvalidVinYearException` | Position 10 is not a valid model-year code |
| `VinChecksumMismatchException` | Check digit wrong for NA/China VINs |
| `NoChecksumForEuException` | Accessing `checksum` on a European VIN |
| `NhtsaDatabaseFailedException` | NHTSA API returned no result for the requested field |
| `NhtsaDatabaseAlreadyClosedException` | NHTSA client used after `VinInfo.close()` |

```kotlin
VinInfo.fromNumber("BADINPUT").onFailure { e ->
    when (e) {
        is InvalidVinLengthException -> println("Wrong length")
        is VinChecksumMismatchException -> println("Bad check digit")
        else -> println(e.message)
    }
}
```

---

## VIN Structure Reference

```
 W  B  A  3  A  5  G  5  9  D  N  P  2  6  0  8  2
 ↑──────↑  ↑──────────↑  ↑───────────────────────↑
   WMI         VDS                  VIS
[1-3]        [4-9]               [10-17]

Position  1     : Region code
Position  1–3   : WMI — World Manufacturer Identifier
Position  4–8   : VDS — Vehicle Descriptor Section
Position  9     : Check digit (mandatory: NA, China)
Position  10    : Model year code
Position  11    : Assembly plant
Position  12–17 : Serial number (12–14 = extended WMI for small-volume makers)
```

---

## License

[MIT License](LICENSE) © 2026 [Naimul Kabir](https://github.com/kabirnayeem99)

Inspired by [vin-decoder-dart](https://github.com/adaptant-labs/vin-decoder-dart) by Adaptant Labs and [vindecoder.js](https://gist.github.com/kevboutin/3ac029e336fc7cafd20c05adda42ffa5) by Kevin Boutin.
