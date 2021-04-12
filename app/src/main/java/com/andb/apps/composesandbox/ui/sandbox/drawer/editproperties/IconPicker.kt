package com.andb.apps.composesandbox.ui.sandbox.drawer.editproperties

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.data.model.imageVector
import com.andb.apps.composesandbox.ui.common.Chip
import com.andb.apps.composesandbox.util.gridItems
import com.andb.apps.composesandbox.util.toggle
import com.andb.apps.composesandboxdata.model.PrototypeIcon
import com.andb.apps.composesandboxdata.model.icons

private data class SectionedIcon(val icon: PrototypeIcon, val section: String)
private val sectionedIcons: List<SectionedIcon> = icons.flatMap { section -> section.icons.map { SectionedIcon(it, section.name) } }

@Composable
fun IconPicker(icon: PrototypeIcon, onSelect: (PrototypeIcon) -> Unit) {
    val picking = remember { mutableStateOf(false) }
    GenericPropertyEditor(label = "Icon") {
        Row(
            modifier = Modifier
                .clickable { picking.value = true }
                .background(MaterialTheme.colors.secondary, CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(imageVector = icon.imageVector, contentDescription = icon.name)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = icon.name, color = MaterialTheme.colors.onSecondary)
        }
    }
    if (picking.value) {
        Dialog(onDismissRequest = { picking.value = false }) {
            Column(Modifier.background(MaterialTheme.colors.background, RoundedCornerShape(16.dp))) {
                val isGrid = remember { mutableStateOf(false) }
                IconPickerHeader(isGrid = isGrid.value) { isGrid.toggle() }
                IconPickerDialogContent(selected = icon, isGrid = isGrid.value) {
                    onSelect.invoke(it)
                    picking.value = false
                }
            }
        }
    }
}


@Composable
fun IconPickerHeader(isGrid: Boolean, onToggle: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
        Text(text = "Pick Icon", style = MaterialTheme.typography.h6)
        when (isGrid) {
            true -> Icon(imageVector = Icons.Default.List, contentDescription = "Switch to List View", tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onToggle))
            false -> Icon(imageVector = Icons.Default.GridOn, contentDescription = "Switch to Grid View", tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onToggle))
        }
    }
}

@Composable
private fun IconPickerFilter(allIcons: List<SectionedIcon>, onUpdateFilteredIcons: (List<PrototypeIcon>) -> Unit) {
    val searchTerm = remember { mutableStateOf("") }
    val selectedSections = remember { mutableStateOf(emptyList<String>()) }
    val onlyFavorites = remember { mutableStateOf(false) }
    fun update() {
        println("effect running")
        val newIcons = allIcons
            .filter { searchTerm.value in it.icon.name && (it.section in selectedSections.value || selectedSections.value.isEmpty()) }
            .map { it.icon }
            .sortedBy { it.name }
        onUpdateFilteredIcons.invoke(newIcons)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            label = { Text(text = "Search Icons") },
            value = searchTerm.value,
            onValueChange = {
                searchTerm.value = it
                update()
            },
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, contentPadding = PaddingValues(start = 32.dp, end = 32.dp)) {
            item {
                val modifier = Modifier.clickable {
                    onlyFavorites.value = !onlyFavorites.value
                    update()
                }
                when (onlyFavorites.value) {
                    true -> Icon(imageVector = Icons.Default.Star, contentDescription = "Remove from Favorites", tint = MaterialTheme.colors.primary, modifier = modifier)
                    false -> Icon(imageVector = Icons.Default.StarOutline, contentDescription = "Add to Favorites", tint = MaterialTheme.colors.onSecondary, modifier = modifier)
                }
            }
            item {
                Chip(label = "All", selected = selectedSections.value.isEmpty(), modifier = Modifier.clickable {
                    selectedSections.value = emptyList()
                    update()
                })
            }
            items(icons.map { it.name }) { sectionName ->
                val selected = sectionName in selectedSections.value
                Chip(label = sectionName, selected = selected, modifier = Modifier.clickable {
                    if (selected) selectedSections.value -= sectionName else selectedSections.value += sectionName
                    update()
                })
            }
        }
    }
}

@Composable
private fun IconPickerDialogContent(selected: PrototypeIcon, isGrid: Boolean, modifier: Modifier = Modifier, onSelect: (PrototypeIcon) -> Unit) {
    val filteredIcons = remember { mutableStateOf(sectionedIcons.map { it.icon }.sortedBy { it.name }) }
    Column(modifier) {
        IconPickerFilter(allIcons = sectionedIcons) {
            println("updating icons to $it")
            filteredIcons.value = it
        }
        when {
            filteredIcons.value.isEmpty() -> NoResults(Modifier.padding(32.dp).fillMaxWidth())
            isGrid -> IconPickerGrid(icons = filteredIcons.value, modifier = Modifier.padding(horizontal = 32.dp), favorites = emptyList(), selected = selected, onUpdateFavorites = {}, onSelect = onSelect)
            else -> IconPickerList(icons = filteredIcons.value, favorites = emptyList(), selected = selected, onUpdateFavorites = {}, onSelect = onSelect)
        }
    }
}

@Composable
private fun IconPickerList(icons: List<PrototypeIcon>, favorites: List<PrototypeIcon>, selected: PrototypeIcon, modifier: Modifier = Modifier, onUpdateFavorites: (List<PrototypeIcon>) -> Unit, onSelect: (PrototypeIcon) -> Unit) {
    LazyColumn(modifier, contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)) {
        items(icons) {
            IconPickerListItem(
                icon = it,
                selected = it == selected,
                isFavorite = it in favorites,
                onFavorite = {
                    if (it in favorites) {
                        onUpdateFavorites.invoke(favorites - it)
                    } else {
                        onUpdateFavorites.invoke(favorites + it)
                    }
                },
                onSelect = { onSelect.invoke(it) }
            )
        }
    }
}

@Composable
private fun IconPickerGrid(icons: List<PrototypeIcon>, favorites: List<PrototypeIcon>, selected: PrototypeIcon, modifier: Modifier = Modifier, onUpdateFavorites: (List<PrototypeIcon>) -> Unit, onSelect: (PrototypeIcon) -> Unit) {
    BoxWithConstraints(modifier) {
        val maxWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        println("maxWidth = $maxWidth")
        val columns = (maxWidth / 40.dp).toInt()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)) {
            gridItems(icons, columns = columns) {
                IconPickerGridItem(
                    icon = it,
                    selected = it == selected,
                    isFavorite = it in favorites,
                    onFavorite = {
                        if (it in favorites) {
                            onUpdateFavorites.invoke(favorites - it)
                        } else {
                            onUpdateFavorites.invoke(favorites + it)
                        }
                    },
                    onSelect = { onSelect.invoke(it) }
                )
            }
        }
    }
}


@Composable
fun IconPickerListItem(icon: PrototypeIcon, selected: Boolean, isFavorite: Boolean, modifier: Modifier = Modifier, onFavorite: () -> Unit, onSelect: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clickable(onClick = onSelect)
            .background(if (selected) MaterialTheme.colors.secondary else Color.Transparent)
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon.imageVector, contentDescription = icon.name,tint = if (selected) MaterialTheme.colors.onBackground else MaterialTheme.colors.onSecondary)
            Text(text = icon.name)
        }
        when (isFavorite) {
            true -> Icon(imageVector = Icons.Default.Star, contentDescription = "Remove from Favorites", tint = MaterialTheme.colors.primary, modifier = Modifier.clickable(onClick = onFavorite))
            false -> Icon(imageVector = Icons.Default.StarOutline, contentDescription = "Add to Favorites", tint = MaterialTheme.colors.onSecondary, modifier = Modifier.clickable(onClick = onFavorite))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.IconPickerGridItem(icon: PrototypeIcon, selected: Boolean, isFavorite: Boolean, modifier: Modifier = Modifier, onFavorite: () -> Unit, onSelect: () -> Unit) {
    Box(
        modifier = modifier
            .border(1.dp, if (isFavorite) MaterialTheme.colors.primary else Color.Transparent, RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colors.secondary else Color.Transparent, RoundedCornerShape(8.dp))
            .combinedClickable(onClick = onSelect, onLongClick = onFavorite)
            .weight(1f)
            .padding(8.dp)
            .height(32.dp)
    ) {
        Image(
            imageVector = icon.imageVector,
            contentDescription = icon.name,
            colorFilter = ColorFilter.tint(if (selected) MaterialTheme.colors.onBackground else MaterialTheme.colors.onSecondary),
            modifier = Modifier.align(Alignment.Center).size(32.dp)
        )
    }
}

@Composable
private fun NoResults(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.background(MaterialTheme.colors.secondary, CircleShape).padding(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "No Results", style = MaterialTheme.typography.h6)
            Text(text = "Try a different search term or different categories", color = MaterialTheme.colors.onSecondary, textAlign = TextAlign.Center)
        }
    }
}