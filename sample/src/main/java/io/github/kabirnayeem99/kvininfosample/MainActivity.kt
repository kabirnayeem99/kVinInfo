package io.github.kabirnayeem99.kvininfosample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.kabirnayeem99.kvininfosample.ui.theme.KvininfoTheme

private val SAMPLE_VINS =
    listOf(
        "WBA3A5G59DNP26082" to "BMW (Europe — no checksum required)",
        "1HGBH41JXMN109186" to "Honda (USA — checksum required)",
        "JN1CV6AR0AM450000" to "Nissan (Japan)",
        "BADVIN" to "Invalid VIN (error demo)",
    )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KvininfoTheme {
                val vm: VinViewModel = viewModel()
                VinDemoApp(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinDemoApp(vm: VinViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("kVinInfo Demo") }) },
    ) { padding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            // ── VIN input (standalone) ─────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = state.vin,
                    onValueChange = { vm.onVinChanged(it.uppercase().trim()) },
                    label = { Text("VIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    trailingIcon = {
                        IconButton(onClick = { vm.generateRandomVin() }) {
                            Icon(
                                imageVector = Icons.Filled.Shuffle,
                                contentDescription = "Generate random VIN",
                            )
                        }
                    },
                )
            }

            // ── 1. Factory: VinInfo.fromNumber() ──────────────────────────────
            item {
                DemoSection("1. VinInfo.fromNumber(number)") {
                    Text("Quick picks:", style = MaterialTheme.typography.labelSmall)
                    SAMPLE_VINS.forEach { (sampleVin, label) ->
                        TextButton(
                            onClick = { vm.onVinChanged(sampleVin) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        ) {
                            Text(
                                "$sampleVin  — $label",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            // ── Parse error ───────────────────────────────────────────────────
            state.parseError?.let { err ->
                item {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                    ) {
                        Text(
                            text = err,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            state.localData?.let { local ->

                // ── 2. VIN Segments ───────────────────────────────────────────
                item {
                    DemoSection("2. VIN Segments") {
                        PropRow("vinNumber", local.vinNumber)
                        PropRow("wmi  (chars 1–3)", local.wmi)
                        PropRow("vds  (chars 4–9)", local.vds)
                        PropRow("vis  (chars 10–17)", local.vis)
                    }
                }

                // ── 3. Validation ─────────────────────────────────────────────
                item {
                    DemoSection("3. Validation") {
                        PropRow("isFormatValid", local.isFormatValid)
                        PropRow("isCheckDigitRequired", local.isCheckDigitRequired)
                        PropRow("isCheckDigitValid", local.isCheckDigitValid)
                        PropRow("isValid", local.isValid)
                        PropRow("calculatedChecksum", local.calculatedChecksum)
                        PropRow("checksum (pos 9)", local.checksum)
                    }
                }

                // ── 4. Vehicle Info ───────────────────────────────────────────
                item {
                    DemoSection("4. Vehicle Info") {
                        PropRow("year", local.year)
                        PropRow("region", local.region)
                        PropRow("regionCode", local.regionCode)
                        PropRow("country", local.country)
                        PropRow("manufacturer", local.manufacturer)
                        PropRow("isSmallVolumeManufacturer", local.isSmallVolumeManufacturer)
                        PropRow("assemblyPlant", local.assemblyPlant)
                        PropRow("serialNumber", local.serialNumber)
                    }
                }

                // ── 5. toString() ─────────────────────────────────────────────
                item {
                    DemoSection("5. toString()") {
                        PropRow("toString()", local.vinNumber)
                    }
                }

                // ── 6. NHTSA suspend functions ────────────────────────────────
                item {
                    DemoSection("6. NHTSA Suspend Functions") {
                        CodeBlock(
                            "suspend fun isValidByNhtsa(): Result<String>\n" +
                                "suspend fun getMakeFromNhtsa(): String\n" +
                                "suspend fun getModelFromNhtsa(): String\n" +
                                "suspend fun getVehicleTypeFromNhtsa(): String\n" +
                                "suspend fun getBodyClassFromNhtsa(): String\n" +
                                "suspend fun toJsonString(): String",
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = { vm.toggleNhtsaEnabled() },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(if (state.nhtsaEnabled) "API: Enabled" else "API: Disabled")
                            }
                            Button(
                                onClick = { vm.fetchNhtsa() },
                                enabled = state.nhtsaEnabled && !state.nhtsaLoading,
                                modifier = Modifier.weight(1f),
                            ) {
                            if (state.nhtsaLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Fetching from NHTSA…")
                            } else {
                                Text("Fetch from NHTSA API")
                            }
                        }
                        }
                        state.nhtsaError?.let { err ->
                            Spacer(Modifier.height(4.dp))
                            Text(
                                err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        state.nhtsaData?.let { nhtsa ->
                            PropRow("isValidByNhtsa()", nhtsa.isValidByNhtsa)
                            PropRow("getMakeFromNhtsa()", nhtsa.make)
                            PropRow("getModelFromNhtsa()", nhtsa.model)
                            PropRow("getVehicleTypeFromNhtsa()", nhtsa.vehicleType)
                            PropRow("getBodyClassFromNhtsa()", nhtsa.bodyClass)
                            Spacer(Modifier.height(4.dp))
                            Text("toJsonString():", style = MaterialTheme.typography.labelSmall)
                            CodeBlock(nhtsa.json)
                        }
                    }
                }

                // ── 7. String.withVinInfo { } DSL ─────────────────────────────
                item {
                    DemoSection("7. String.withVinInfo { } DSL") {
                        CodeBlock(
                            "// import io.github.kabirnayeem99.viminfo.VinInfo.Companion.withVinInfo\n" +
                                "\"${state.vin}\".withVinInfo {\n" +
                                "    println(year)          // this = VinInfo\n" +
                                "    println(region)\n" +
                                "    println(manufacturer)\n" +
                                "    // VinInfo auto-closed after block exits\n" +
                                "}",
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Live output:", style = MaterialTheme.typography.labelSmall)
                        CodeBlock(local.dslOutput.ifEmpty { "(empty — VIN error)" })
                    }
                }

                // ── 8. AutoCloseable — use { } ────────────────────────────────
                item {
                    DemoSection("8. AutoCloseable — use { }") {
                        CodeBlock(
                            "VinInfo.fromNumber(\"${state.vin}\").use { vi ->\n" +
                                "    println(vi.year)          // ${local.year}\n" +
                                "    println(vi.manufacturer)  // ${local.manufacturer}\n" +
                                "    println(vi.isValid)       // ${local.isValid}\n" +
                                "    // vi.close() called automatically\n" +
                                "}",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DemoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            HorizontalDivider()
            content()
        }
    }
}

@Composable
fun PropRow(
    label: String,
    value: Any?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value?.toString() ?: "null",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
fun CodeBlock(code: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = code,
            modifier = Modifier.padding(10.dp),
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
