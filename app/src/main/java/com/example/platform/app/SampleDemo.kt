package com.example.platform.app

import androidx.compose.runtime.Composable

// Este arquivo foi simplificado para evitar conflitos de compilação.
// As interfaces CatalogItem e ApiSurface já estão definidas em outros arquivos deste pacote.

interface SampleDemo : CatalogItem {
    val apiSurface: ApiSurface
}

class ActivitySampleDemo(
    override val id: String,
    override val name: String,
    override val description: String?,
    override val apiSurface: ApiSurface,
    val content: Class<*>,
) : SampleDemo

class ComposableSampleDemo(
    override val id: String,
    override val name: String,
    override val description: String?,
    override val apiSurface: ApiSurface,
    val content: @Composable () -> Unit,
) : SampleDemo

// As listas reais de amostras foram desativadas para focar no VidaAPP
val SAMPLE_DEMOS = emptyMap<String, SampleDemo>()
