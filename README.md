# kVINInfo: A Kotlin Library for VIN Processing

License: [MIT License](LICENSE)

kVINInfo is a pure Kotlin library designed to simplify tasks related to Vehicle Identification
Numbers (VINs). It offers a comprehensive suite of functionalities, including:

- **VIN Validation**: Ensures the provided VIN adheres to the correct format and checksum.
- **Information Extraction**: Extracts valuable details from the VIN, such as manufacturer, model
  year, and region of origin.
- **Random VIN Generation**: Generates random or mocked VIN numbers for testing or educational
  purposes.
- **NHTSA Integration**: You can use NHTSA data which internally uses their API to get more detailed
  information. For more note about available data head over to their documentation.

**Inspiration**: This library draws inspiration from the Dart
library [vin-decoder-dart](https://github.com/adaptant-labs/vin-decoder-dart)
by [Adaptant Labs](https://github.com/adaptant-labs)
and [vindecoder.js](https://gist.github.com/kevboutin/3ac029e336fc7cafd20c05adda42ffa5)
by [Kevin Boutin](https://gist.github.com/kevboutin).

**Note**: For more complex VIN processing, professional-grade validation, or in-depth information
extraction, consider implementing custom logic or integrating with country-specific databases or
APIs.

# Install

To install this library in your Kotlin Multiplatform Application:

```kotlin
val commonMain by getting {
    dependencies {
        // all other dependencies
        implementation("io.github.kabirnayeem99:kvininfo:1.0.0")
    }
}
```

To Install on Android project:

For Kotlin DSL:

```kotlin
   dependencies {
    // all other dependencies
    implementation("io.github.kabirnayeem99:kvininfo:1.0.0")
}
```

For Groovy DSL:

```groovy
   dependencies {
    // all other dependencies
    implementation "io.github.kabirnayeem99:kvininfo:1.0.0"
}
```

# Usage

```kotlin
val vin = "WBA3A5G59DNP26082"
val vinInfo = VinInfo.fromNumber(vin)
vinInfo.use { println(it.year) } // 2013
```

Or do it in more **Kotlin** way:

```kotlin
val vin = "WBA3A5G59DNP26082"
val vinInfo = VinInfo.fromNumber(vin)
"WBA3A5G59DNP26082".withVinInfo {
    println(year)  // 2013
    println(region)  // Europe
    println(manufacturer)  // BMW AG
    println(getMakeFromNhtsa())  // BMW
    println(getModelFromNhtsa())  // 328i
}
```

## Key Features

- **Pure Kotlin**: Seamless integration with Kotlin projects, including Android and Kotlin
  Multiplatform (KMP) environments.
- **Detailed Information Extraction**: Uncovers a wide range of information from the VIN, depending
  on availability.
- **Checksum Verification**: Guarantees the validity of the VIN by verifying the checksum.
- **Random VIN Generation**: Creates random or mocked VINs for various use cases.

## Limitations

- **Basic Validation**: The library offers basic VIN format checks but might not cover all
  validation
  scenarios.
- **Limited Information Extraction**: In-depth VIN decoding and data enrichment might require
  additional
  logic or external data sources.
- **NHTSA API Usage (Optional)**: The getMakeFromNhtsa and getModelFromNhtsa methods rely on the
  NHTSA API, which works best only on USA, in other regions, it may not give valid results.

## Contributions

We welcome contributions!
If you have any suggestions or improvements for kVINInfo, feel free to open an issue or make a PR.