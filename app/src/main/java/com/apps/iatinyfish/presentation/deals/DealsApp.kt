package com.apps.iatinyfish.presentation.deals

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.iatinyfish.data.model.ProductOffer

@Composable
fun DealsApp(state: DealsUiState, onEvent: (DealsEvent) -> Unit) {
    BackHandler(enabled = state.selectedOffer != null) {
        onEvent(DealsEvent.Back)
    }
    AnimatedContent(
        targetState = state.selectedOffer,
        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
        label = "screen",
    ) { offer ->
        if (offer == null) DealsSearchScreen(state, onEvent)
        else DealDetailScreen(offer, onBack = { onEvent(DealsEvent.Back) })
    }
}

@Composable
private fun DealsSearchScreen(state: DealsUiState, onEvent: (DealsEvent) -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FishMark()
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "TinyPrice",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            if (state.isPreviewData) "Búsqueda (datos de demostración)" else "Búsqueda web inteligente",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onEvent(DealsEvent.QueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("¿Qué quieres comparar?") },
                    leadingIcon = {
                        Text(
                            "⌕",
                            fontSize = 26.sp,
                            modifier = Modifier.semantics {
                                contentDescription = "Buscar productos"
                            },
                        )
                    },
                    trailingIcon = {
                        TextButton(onClick = { onEvent(DealsEvent.SubmitSearch) }) {
                            Text("Buscar", fontWeight = FontWeight.Bold)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { onEvent(DealsEvent.SubmitSearch) },
                    ),
                    shape = RoundedCornerShape(18.dp),
                )
            }
        },
    ) { padding ->
        if (state.hasSearched) {
            SearchResults(state, padding.calculateTopPadding(), onEvent)
        } else {
            DiscoveryHome(padding.calculateTopPadding()) {
                onEvent(DealsEvent.UseSuggestion(it))
            }
        }
    }
}

@Composable
private fun SearchResults(
    state: DealsUiState,
    topPadding: Dp,
    onEvent: (DealsEvent) -> Unit,
) {
    when {
        state.isLoading -> LoadingState(topPadding)
        state.offers.isEmpty() -> EmptyState(
            message = state.errorMessage ?: "No encontramos ofertas para “${state.query}”",
            topPadding = topPadding,
            onRetry = { onEvent(DealsEvent.SubmitSearch) },
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, topPadding + 6.dp, 20.dp, 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Resultados para “${state.query}”",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            items(state.offers, key = ProductOffer::id) { offer ->
                OfferCard(offer) { onEvent(DealsEvent.OpenOffer(offer)) }
            }
        }
    }
}

@Composable
private fun OfferCard(offer: ProductOffer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(
                        offer.store,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        offer.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    offer.price,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(offer.shipping, style = MaterialTheme.typography.bodySmall)
                Text("★ ${offer.rating}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text(offer.delivery, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun DealDetailScreen(offer: ProductOffer, onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    offer.store,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                offer.productName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                offer.price,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
            if (offer.originalPrice != null) {
                Text(
                    "Precio regular: ${offer.originalPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Información y resumen",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(offer.summary, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(offer.tags) { tag ->
                        SuggestionChip(onClick = {}, label = { Text(tag) })
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            if (offer.sourceUrl.startsWith("http://") || offer.sourceUrl.startsWith("https://")) {
                Button(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.sourceUrl))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Abrir fuente web ↗", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun DiscoveryHome(topPadding: Dp, onSelectSuggestion: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding + 20.dp, start = 20.dp, end = 20.dp)
    ) {
        Text(
            "Encuentra la mejor compra",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            "Compara fuentes web en tiempo real sin abrir diez pestañas.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Sugerencias populares:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        val suggestions = listOf(
            "Nintendo Switch OLED",
            "PlayStation 5 Slim",
            "MacBook Air M2",
            "iPhone 15 Pro",
            "Samsung Galaxy S24"
        )
        suggestions.forEach { suggestion ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectSuggestion(suggestion) },
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⌕", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(suggestion, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun LoadingState(topPadding: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Buscando ofertas en vivo...", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun EmptyState(message: String, topPadding: Dp, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding, start = 24.dp, end = 24.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("∅", fontSize = 48.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun FishMark() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text("◒", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}
