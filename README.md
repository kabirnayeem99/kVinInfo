# kVINInfo: A Kotlin Library for VIN Processing

kVINInfo is a pure Kotlin library designed to simplify tasks related to Vehicle Identification
Numbers (VINs). It offers a comprehensive suite of functionalities, including:

- **VIN Validation**: Ensures the provided VIN adheres to the correct format and checksum.
- **Information Extraction**: Extracts valuable details from the VIN, such as manufacturer, model
  year, and region of origin.
- **Random VIN Generation**: Generates random or mocked VIN numbers for testing or educational
  purposes.

**Inspiration**: This library draws inspiration from the Dart
library [vin-decoder-dart](https://github.com/adaptant-labs/vin-decoder-dart)
by [Adaptant Labs](https://github.com/adaptant-labs)
and [vindecoder.js](https://gist.github.com/kevboutin/3ac029e336fc7cafd20c05adda42ffa5)
by [Kevin Boutin](https://gist.github.com/kevboutin).

# Usage

```kotlin
val vin = "WBA3A5G59DNP26082"
val vinInfo = VinInfo.fromNumber(vin)
vinInfo.use { println(it.year) } // 2013
```

## Key Features

- **Pure Kotlin**: Seamless integration with Kotlin projects, including Android and Kotlin
  Multiplatform (KMP) environments.
- **Detailed Information Extraction**: Uncovers a wide range of information from the VIN, depending
  on availability.
- **Checksum Verification**: Guarantees the validity of the VIN by verifying the checksum.
- **Random VIN Generation**: Creates random or mocked VINs for various use cases.

## Contributions

We welcome contributions!
If you have any suggestions or improvements for kVINInfo, feel free to open an issue or make a PR.