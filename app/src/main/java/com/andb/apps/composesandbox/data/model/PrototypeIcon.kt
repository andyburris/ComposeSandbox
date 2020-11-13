package com.andb.apps.composesandbox.data.model

import androidx.compose.ui.graphics.vector.VectorAsset

data class IconSection(val sectionName: String, val icons: List<VectorAsset>)

val icons = listOf(
    IconSection("Action", actionIcons),
    IconSection("Alert", alertIcons),
    IconSection("Av", avIcons),
    IconSection("Communication", communicationIcons),
    IconSection("Content", contentIcons),
    IconSection("Device", deviceIcons),
    IconSection("Editor", editorIcons),
    IconSection("File", fileIcons),
    IconSection("Hardware", hardwareIcons),
    IconSection("Home", homeIcons),
    IconSection("Image", imageIcons),
    IconSection("Maps", mapsIcons),
    IconSection("Navigation", navigationIcons),
    IconSection("Notification", notificationIcons),
)

val VectorAsset.readableName get() = this.name.removePrefix("Filled.")