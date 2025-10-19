plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // --- PLUGIN DE GOOGLE SERVICES AÑADIDO ---
    alias(libs.plugins.google.gms.google.services) apply false
}