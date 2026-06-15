# kVinInfo Architecture

**KMP library:** VIN parsing, validation, decoding | **Targets:** JVM, Android, iOS, Linux | **Path:** `/Users/kabir/Projects/kVinInfo`

## Index

1. [Files](#files) | 2. [Deps](#deps) | 3. [API](#api) | 4. [Validation](#validation) | 5. [Exceptions](#exceptions) | 6. [Tests](#tests) | 7. [Design](#design)

---

## Files

| Component | Path | Lines |
|-----------|------|-------|
| API | `commonMain/.../VinInfo.kt` | 357 |
| Format | `commonMain/.../decode/VinFormat.kt` | 16 |
| Checksum | `commonMain/.../decode/VinChecksum.kt` | 52 |
| Region | `commonMain/.../decode/VinRegion.kt` | 63 |
| Year | `commonMain/.../decode/VinModelYear.kt` | 57 |
| Manufacturer | `commonMain/.../decode/VinManufacturer.kt` | 53 |
| Year Data | `commonMain/.../data/Years.kt` | 48 |
| Exceptions | `commonMain/.../exceptions/*.kt` | 9 files |
| Network | `commonMain/.../network/NhtsaUsaApi.kt` | ~120 |
| Tests | `commonTest/kotlin/...` | 1373 |
| Build | `kvininfo/build.gradle.kts` | 82 |

---

## Deps

- Kotlin 2.3.21 | Ktor 3.5.0 | Coroutines 1.11.0 | Serialization 1.11.0
- AGP 9.2.1 | compileSdk 37, minSdk 24

---

## API

**Factory:** `VinInfo.fromNumber(vin: String)` → throws `InvalidVinLengthException` if blank

**Properties:** `wmi` (0-2) | `vds` (3-8) | `vis` (9-16) | `year` | `manufacturer` | `serialNumber` | `region` | `country`

**Validation:** `isFormatValid` | `isCheckDigitRequired` | `isCheckDigitValid` | `isValid`

---

## Validation

**Format** (16L): `^[A-HJ-NPR-Z0-9]{17}$` excludes I,O,Q

**Checksum** (52L, ISO 3779): Position 8, weights [8,7,6,5,4,3,2,10,0,9,8,7,6,5,4,3,2], 'X' if remainder=10

**Region** (63L, ISO 3780):
| A-C | E,F,G,S-Z | H-R | 1-5,7 | 6 | 8-9 |
| AF | EU | AS | NA | OC | SA |

Checksum mandatory: NA (1-5,7), China (H,L)

**Year** (57L): Pos 10 char, 30-yr cycle. Numeric (7)→1980-2009, Alpha (10)→2010-2039. `Years.kt` maps A-Z,0-9.

**Manufacturer** (53L): Lookup → Extended ID (small-vol) → Full WMI → 2-char prefix. `isSmallVolume()` checks pos 2=='9'.

---

## Flow

`fromNumber()` → blank? throw `InvalidVinLengthException` → normalize (uppercase, remove "-") → store → lazy validate on property access

---

## Exceptions

**Base:** `InvalidVinException`

| Name | Trigger |
|------|---------|
| `InvalidVinLengthException` | ≠17 chars |
| `InvalidVinRegionCharException` | Bad region code |
| `InvalidVinYearException` | Bad year char |
| `InvalidWmiException` | Unknown WMI |
| `InvalidWmiForCountryException` | WMI mismatch |
| `NoChecksumForEuException` | EU checksum access |
| `NhtsaDatabaseFailedException` | API fail |
| `NhtsaDatabaseAlreadyClosedException` | Closed API use |

---

## Network

`NhtsaUsaApi.kt` (~120L): Ktor HTTP client, vehicle type/make/model lookup, caching. Platform-specific clients (Android, Darwin/iOS).

---

## Tests

1373L total, 8 files:

| File | L | Focus |
|------|---|-------|
| VinInfoTest | 190 | API, format, checksum, year, region, mfr |
| VinFormatTest | 136 | Alphabet, length, case, chars |
| VinChecksumTest | 250 | All-ones/zeros, 'X', known VINs, transliteration |
| VinRegionTest | 215 | All 6 regions, boundaries, rules |
| VinModelYearTest | 303 | 30-yr cycle, both cycles |
| VinManufacturerTest | 163 | Small-vol, extended ID, fallback |
| VinGeneratorTest | 113 | Valid checksum+region gen |
| NhtsaUsaApiTest | 3 | Stub |

Coverage: happy path, failure path (I,O,Q, length, checksum), edge cases (all-ones/zeros, 'X', empty, unassigned regions)

---

## Design

**Normalization:** Uppercase + remove "-" only (no trim, implicit in `fromNumber()`)

**Validation:** Strict 17-char regex, conditional checksum (NA/China), lazy eval on property access, exception-based

**Mfr Resolution:** Extended ID → Full WMI → 2-char prefix fallback; '9' pos 2 = small-vol marker

**Year:** 30-yr cycle disambiguated by pos 7 (numeric 1980-2009) vs pos 10 (alpha 2010-2039)

**Error Handling:** Early blank check, runtime exceptions, no Result pattern

---

## Extensibility

- Modify `String.normalize()` for region rules
- Replace `Manufacturers.kt` data
- Custom `NhtsaUsaApi` or offline-only
- Extend `VinModelYear.possibleYears()` for adjacent years
- Add region-specific rules in `VinRegion.requiresCheckDigit()`

---

## Limitations

- No whitespace trimming
- Exception-based (no Result/sealed class)
- NHTSA API optional; works offline
- Small-vol detection: '9' hardcoded
- Strict validation (no historical/invalid VINs)

---

## Platforms

**JVM/Android/iOS/Linux:** commonMain validation (platform-agnostic) + platform-specific Ktor HTTP clients
