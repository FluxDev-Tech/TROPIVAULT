package com.example.tropivault.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.tropivault.data.local.ProductEntity
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.ProductCard
import com.example.tropivault.ui.theme.*

@Composable
fun MarketplaceScreen(
    viewModel: TropiVaultViewModel,
    modifier: Modifier = Modifier
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allApproved by viewModel.approvedProducts.collectAsStateWithLifecycle()
    val featuredProducts = remember(allApproved) {
        allApproved.filter { it.isFeatured }
    }

    val categories = listOf(
        "All",
        "Mangoes",
        "Bananas & Plantains",
        "Citrus & Melons",
        "Exotic & Rare",
        "Preserved & Dehydrated",
        "Farm Bundles"
    )

    val locations = listOf(
        "All Locations",
        "Guimaras",
        "Davao",
        "Bukidnon",
        "Batangas"
    )

    var showSortMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TropiBackgroundLight),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Search Bar & Filter Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreen)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = filters.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Search Carabao mangoes, farm, location...",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (filters.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketplace_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenYellow,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GoldenYellow
                    ),
                    singleLine = true
                )
            }
        }

        // 2. Hero Preservation Vault Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tropivault_preservation_1790637424122),
                        contentDescription = "Preservation Vault",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        DeepGreen.copy(alpha = 0.92f),
                                        DeepGreen.copy(alpha = 0.65f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldenYellow
                        ) {
                            Text(
                                text = "CLIMATE-CONTROLLED VAULT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = DeepGreen
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Up to 3x Longer Freshness",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Nitrogen-purged & solar chilled storage direct from Philippine orchards.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // 3. Category Selector Chips
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                PaddingValues(horizontal = 16.dp)
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = filters.selectedCategory.equals(category, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = DeepGreen
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }
            }
        }

        // 4. Location & Sorting Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location selector chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(locations) { loc ->
                        val isSelected = filters.selectedLocation == loc
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) FreshGreen else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FreshGreen else Color.LightGray),
                            modifier = Modifier.clickable { viewModel.selectLocation(loc) }
                        ) {
                            Text(
                                text = loc,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Button
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .testTag("sort_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = ForestGreen
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Featured First") },
                            onClick = {
                                viewModel.selectSortBy("Featured")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Price: Low to High") },
                            onClick = {
                                viewModel.selectSortBy("PriceLow")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Price: High to Low") },
                            onClick = {
                                viewModel.selectSortBy("PriceHigh")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Longest Shelf-Life") },
                            onClick = {
                                viewModel.selectSortBy("ShelfLife")
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // 5. Featured Produce Spotlight
        if (filters.searchQuery.isBlank() && filters.selectedCategory == "All" && featuredProducts.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured Orchard Harvests",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldLight
                        ) {
                            Text(
                                text = "Grade AAA",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TropicalOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredProducts) { prod ->
                            ProductCard(
                                product = prod,
                                onClick = { viewModel.selectProduct(prod) },
                                onAddToCart = { viewModel.addToCart(prod, 1) },
                                modifier = Modifier.width(185.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (filters.selectedCategory != "All") "${filters.selectedCategory} (${products.size})" else "All Available Produce (${products.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
            }
        }

        // 7. Product List Grid Cards
        if (products.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No products found matching filters",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            // Display products in paired 2-column rows for smooth scrolling
            val chunkedProducts = products.chunked(2)
            items(chunkedProducts) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = pair[0],
                            onClick = { viewModel.selectProduct(pair[0]) },
                            onAddToCart = { viewModel.addToCart(pair[0], 1) }
                        )
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = pair[1],
                                onClick = { viewModel.selectProduct(pair[1]) },
                                onAddToCart = { viewModel.addToCart(pair[1], 1) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 8. About TropiVault Footer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = GoldenYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About TropiVault",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "TropiVault bridges Filipino fruit growers directly with conscious consumers and businesses. Our solar-assisted cold-chain preserves seasonal harvests, slashing food waste while guaranteeing maximum nutrition.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepGreen
                        ) {
                            Text(
                                text = "Orchard-Direct",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = GoldenYellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepGreen
                        ) {
                            Text(
                                text = "Cold-Chain Preserved",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = GoldenYellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepGreen
                        ) {
                            Text(
                                text = "Fair Farmgate",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = GoldenYellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "support@tropivault.com • Hotline: (02) 8888-VAULT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
