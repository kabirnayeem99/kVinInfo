package io.github.kabirnayeem99.viminfo.network

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinException
import io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException
import io.github.kabirnayeem99.viminfo.network.dto.NhtsaDecodeVinDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class NhtsaUsaApi(private val vinNumber: String) : AutoCloseable {

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
        install(HttpCache)
    }

    private var decodedValueMap = mutableMapOf<Long, String>()

    private var cachedApiResponse: NhtsaDecodeVinDto? = null

    /**
     * Decodes a VIN using the NHTSA API.
     *
     * This function fetches the decoded VIN information from the NHTSA API and caches the response.
     * It parses the response and populates the `decodedValueMap` with variable ID and value pairs.
     *
     * @return A `NhtsaDecodeVinDto` object containing the decoded VIN information, or `null` if an error occurs.
     */
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

    /**
     * Retrieves the decoded value for a given variable ID.
     *
     * This function fetches the decoded VIN information from the API if not already cached.
     * It then returns the decoded value associated with the specified variable ID.
     *
     * @param variableId The ID of the variable to retrieve the decoded value for.
     * @return The decoded value for the given variable ID.
     * @throws NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    private suspend fun getDecodedValue(variableId: Long): String {
        decodeVinWithApi()
        return decodedValueMap[variableId] ?: throw NhtsaDatabaseFailedException()
    }

    /**
     * Retrieves the make value from the decoded VIN information.
     *
     * @return The make value.
     * @throws NhtsaDatabaseFailedException If the make value cannot be found.
     */
    suspend fun getMakeValue() = getDecodedValue(NhtsaDecodeVinDto.MAKE_VARIABLE_ID)

    /**
     * Retrieves the model value from the decoded VIN information.
     *
     * @return The model value.
     * @throws NhtsaDatabaseFailedException If the model value cannot be found.
     */
    suspend fun getModelValue() = getDecodedValue(NhtsaDecodeVinDto.MODEL_VARIABLE_ID)

    /**
     * Retrieves the vehicle type value from the decoded VIN information.
     *
     * @return The vehicle type value.
     * @throws NhtsaDatabaseFailedException If the vehicle type value cannot be found.
     */
    suspend fun getVehicleTypeValue() = getDecodedValue(NhtsaDecodeVinDto.VEHICLE_TYPE_VARIABLE_ID)

    /**
     * Retrieves the body class value from the decoded VIN information.
     *
     * @return The body class value.
     * @throws NhtsaDatabaseFailedException If the body class value cannot be found.
     */
    suspend fun getBodyClassValue() = getDecodedValue(NhtsaDecodeVinDto.BODY_CLASS_VARIABLE_ID)

    /**
     * Retrieves the decoded VIN information as a map.
     *
     * This function decodes the VIN and returns a map of variable names to their corresponding values.
     *
     * @return A map of variable names to their corresponding values.
     * @throws NhtsaDatabaseFailedException If the decoded VIN information cannot be retrieved.
     */
    private suspend fun getInfoAsMap(): Map<String, String> {
        return decodeVinWithApi()?.results?.filterNotNull()?.filter { it.variableId != null }
            ?.mapNotNull { value ->
                value.value?.takeIf { it.isNotBlank() }?.let { nonBlankValue ->
                    value.variable?.takeIf { it.isNotBlank() }?.let { nonBlankVariable ->
                        nonBlankVariable to nonBlankValue
                    }
                }
            }?.toMap() ?: throw NhtsaDatabaseFailedException()
    }

    /**
     * Converts the decoded VIN information to a JSON string.
     *
     * This function retrieves the decoded VIN information as a map and serializes it into a JSON string.
     *
     * @return The decoded VIN information as a JSON string.
     * @throws NhtsaDatabaseFailedException If an error occurs during serialization.
     */
    suspend fun toStringAsJson() = try {
        Json.encodeToString(getInfoAsMap())
    } catch (e: Exception) {
        e.printStackTrace()
        throw NhtsaDatabaseFailedException(e.message)
    }


    /**
     * Validates the VIN against the NHTSA database.
     *
     * This function checks if the VIN is valid by decoding it using the NHTSA API and examining the error message.
     *
     * @return A `Result` object indicating success with the VIN number if valid, or failure with an `InvalidVinException` if invalid.
     */
    suspend fun isValidByNhtsa(): Result<String> {
        val errorMessage =
            decodeVinWithApi()?.results?.first { it?.variableId == NhtsaDecodeVinDto.ERROR_TEXT_VARIABLE_ID }?.value
                ?: ""
        if (errorMessage.isBlank()) return Result.success(vinNumber)
        return Result.failure(InvalidVinException(errorMessage))
    }


    /**
     * Closes the current instance of the class.
     *
     * This function releases any resources held by the object, such as network connections.
     * It clears the cached API response and closes the underlying HTTP client.
     */
    override fun close() {
        cachedApiResponse = null
        httpClient.close()
    }

}