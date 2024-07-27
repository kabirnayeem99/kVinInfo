package io.github.kabirnayeem99.viminfo.network

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinException
import io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException
import io.github.kabirnayeem99.viminfo.network.dto.NhtsaDecodeVinDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NhtsaUsaApi(private val vinNumber: String) {

    private val baseUrl by lazy {
        "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/$vinNumber?format=json"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    private var decodedValueMap = mutableMapOf<Long, String>()

    private var cachedApiResponse: NhtsaDecodeVinDto? = null

    private suspend fun decodeVinWithApi(): NhtsaDecodeVinDto? {
        try {
            if (cachedApiResponse != null) return cachedApiResponse

            decodedValueMap.clear()
            cachedApiResponse = httpClient.get(baseUrl).body<NhtsaDecodeVinDto>()
            val decodedValueList =
                cachedApiResponse?.results?.filterNotNull()?.filterNot { it.variableId != null }
                    ?: emptyList()
            if (decodedValueList.isNotEmpty()) {
                decodedValueList.forEach { decodedValue ->
                    val variableId = decodedValue.variableId
                    val variable = decodedValue.variable ?: ""
                    if (variableId != null && variable.isNotBlank()) {
                        decodedValueMap[variableId] = variable
                    }
                }
            }
            return cachedApiResponse
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun getMakeValue(): String {
        decodeVinWithApi()
        return decodedValueMap[NhtsaDecodeVinDto.MAKE_VARIABLE_ID]
            ?: throw NhtsaDatabaseFailedException()
    }

    suspend fun getModelValue(): String {
        decodeVinWithApi()
        return decodedValueMap[NhtsaDecodeVinDto.MODEL_VARIABLE_ID]
            ?: throw NhtsaDatabaseFailedException()
    }

    suspend fun getVehicleTypeValue(): String {
        decodeVinWithApi()
        return decodedValueMap[NhtsaDecodeVinDto.VEHICLE_TYPE_VARIABLE_ID]
            ?: throw NhtsaDatabaseFailedException()
    }

    suspend fun getBodyClassValue(): String {
        decodeVinWithApi()
        return decodedValueMap[NhtsaDecodeVinDto.BODY_CLASS_VARIABLE_ID]
            ?: throw NhtsaDatabaseFailedException()
    }

    suspend fun getInfoAsMap(): Map<String, String> {
        decodeVinWithApi()
        if (cachedApiResponse == null) return emptyMap()
        val decodedValueList =
            cachedApiResponse?.results?.filterNotNull()?.filterNot { it.variableId != null }
                ?: emptyList()
        val infoMap = mutableMapOf<String, String>()
        decodedValueList.forEach { value ->
            if (!value.value.isNullOrBlank() && !value.variable.isNullOrBlank()) {
                infoMap[value.variable] = value.value
            }
        }
        return infoMap
    }


    suspend fun isValid(): Result<String> {
        decodeVinWithApi()
        val errorMessage =
            cachedApiResponse?.results?.first { it?.variableId == NhtsaDecodeVinDto.ERROR_TEXT_VARIABLE_ID }?.value
                ?: ""
        if (errorMessage.isBlank()) return Result.success(vinNumber)
        return Result.failure(InvalidVinException(errorMessage))
    }

    fun dispose() {
        cachedApiResponse = null
        httpClient.close()
    }

}