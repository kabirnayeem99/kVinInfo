package io.github.kabirnayeem99.viminfo.network

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinException
import io.github.kabirnayeem99.viminfo.network.dto.NhtsaDecodeVinDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NhtsaUsaApi(private val vinNumber: String) {

    private val baseUrl =
        "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/1HGCM82635A123456?format=json"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    private var cachedApiResponse: NhtsaDecodeVinDto? = null

    suspend fun decodeVinWithApi(): NhtsaDecodeVinDto? {
        try {
            if (cachedApiResponse != null) return cachedApiResponse
            cachedApiResponse = httpClient.get(baseUrl).body<NhtsaDecodeVinDto>()
            return cachedApiResponse
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun isValid(): Result<String> {
        val errorMessage =
            cachedApiResponse?.results?.first { it?.variableId == NhtsaDecodeVinDto.ERROR_TEXT_VARIABLE_ID }?.value
                ?: ""
        if (errorMessage.isBlank()) return Result.success(vinNumber)
        return Result.failure(InvalidVinException(errorMessage))
    }

    suspend fun dispose() {
        cachedApiResponse = null
        httpClient.close()
    }

}