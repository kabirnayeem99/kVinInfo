package io.github.kabirnayeem99.kvininfosample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kabirnayeem99.viminfo.VinGenerator
import io.github.kabirnayeem99.viminfo.VinInfo
import io.github.kabirnayeem99.viminfo.VinInfo.Companion.withVinInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocalVinData(
    val vinNumber: String,
    val wmi: String,
    val vds: String,
    val vis: String,
    val isFormatValid: Boolean,
    val isCheckDigitRequired: Boolean,
    val isCheckDigitValid: Boolean,
    val isValid: Boolean,
    val calculatedChecksum: Char,
    val checksum: String,
    val year: Int,
    val region: String,
    val regionCode: String,
    val country: String,
    val manufacturer: String,
    val isSmallVolumeManufacturer: Boolean,
    val assemblyPlant: Char,
    val serialNumber: String,
    val dslOutput: String,
)

data class NhtsaVinData(
    val isValidByNhtsa: String,
    val make: String,
    val model: String,
    val vehicleType: String,
    val bodyClass: String,
    val json: String,
)

data class VinUiState(
    val vin: String = "WBA3A5G59DNP26082",
    val localData: LocalVinData? = null,
    val parseError: String? = null,
    val nhtsaEnabled: Boolean = true,
    val nhtsaLoading: Boolean = false,
    val nhtsaData: NhtsaVinData? = null,
    val nhtsaError: String? = null,
)

class VinViewModel : ViewModel() {
    private var vinInfo: VinInfo? = null

    private val _uiState = MutableStateFlow(VinUiState())
    val uiState: StateFlow<VinUiState> = _uiState.asStateFlow()

    init {
        parseVin(_uiState.value.vin)
    }

    fun onVinChanged(vin: String) {
        _uiState.update { it.copy(vin = vin, nhtsaData = null, nhtsaError = null) }
        parseVin(vin)
    }

    private fun parseVin(vin: String) {
        vinInfo?.close()
        vinInfo = null
        if (vin.isBlank()) {
            _uiState.update { it.copy(localData = null, parseError = null) }
            return
        }
        try {
            val vi = VinInfo.fromNumber(vin)
            vinInfo = vi
            _uiState.update {
                it.copy(
                    localData = buildLocalData(vi, vin),
                    parseError = null,
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    localData = null,
                    parseError = "${e::class.simpleName}: ${e.message}",
                )
            }
        }
    }

    private fun buildLocalData(
        vi: VinInfo,
        vin: String,
    ): LocalVinData {
        val dsl = StringBuilder()
        vin.withVinInfo {
            dsl.appendLine("year                      = $year")
            dsl.appendLine("region                    = $region")
            dsl.appendLine("regionCode                = $regionCode")
            dsl.appendLine("country                   = $country")
            dsl.appendLine("manufacturer              = $manufacturer")
            dsl.appendLine("isSmallVolumeManufacturer = $isSmallVolumeManufacturer")
            dsl.appendLine("isValid                   = $isValid")
            dsl.appendLine("isFormatValid             = $isFormatValid")
            dsl.appendLine("isCheckDigitRequired      = $isCheckDigitRequired")
            dsl.appendLine("isCheckDigitValid         = $isCheckDigitValid")
            dsl.appendLine("assemblyPlant             = $assemblyPlant")
            dsl.appendLine("serialNumber              = $serialNumber")
        }
        return LocalVinData(
            vinNumber = vi.vinNumber,
            wmi = vi.wmi,
            vds = vi.vds,
            vis = vi.vis,
            isFormatValid = vi.isFormatValid,
            isCheckDigitRequired = vi.isCheckDigitRequired,
            isCheckDigitValid = vi.isCheckDigitValid,
            isValid = vi.isValid,
            calculatedChecksum = vi.calculatedChecksum,
            checksum =
                runCatching { vi.checksum.toString() }
                    .getOrElse { "N/A — NoChecksumForEuException (EU region)" },
            year = vi.year,
            region = vi.region,
            regionCode = vi.regionCode,
            country = vi.country,
            manufacturer = vi.manufacturer,
            isSmallVolumeManufacturer = vi.isSmallVolumeManufacturer,
            assemblyPlant = vi.assemblyPlant,
            serialNumber = vi.serialNumber,
            dslOutput = dsl.toString().trimEnd(),
        )
    }

    fun generateRandomVin() {
        onVinChanged(VinGenerator.generate())
    }

    fun toggleNhtsaEnabled() {
        _uiState.update { it.copy(nhtsaEnabled = !it.nhtsaEnabled) }
    }

    fun fetchNhtsa() {
        val vi = vinInfo ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(nhtsaLoading = true, nhtsaError = null, nhtsaData = null) }
            try {
                val data =
                    NhtsaVinData(
                        isValidByNhtsa = vi.isValidByNhtsa().toString(),
                        make = vi.getMakeFromNhtsa(),
                        model = vi.getModelFromNhtsa(),
                        vehicleType = vi.getVehicleTypeFromNhtsa(),
                        bodyClass = vi.getBodyClassFromNhtsa(),
                        json = vi.toJsonString(),
                    )
                _uiState.update { it.copy(nhtsaData = data) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(nhtsaError = "${e::class.simpleName}: ${e.message}")
                }
            } finally {
                _uiState.update { it.copy(nhtsaLoading = false) }
            }
        }
    }

    override fun onCleared() {
        vinInfo?.close()
    }
}
