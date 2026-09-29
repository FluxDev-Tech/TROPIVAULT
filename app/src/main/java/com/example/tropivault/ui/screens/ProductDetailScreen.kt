package com.example.tropivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.layout.ContentScale
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.ProduceImage
import com.example.tropivault.ui.theme.*

@Composable
fun ProductDetailScreen(
    viewModel: TropiVaultViewModel
) {
    val product by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var quantity by remember { mutableStateOf(1) }
    var showAddedSnackbar by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No product selected")
        }
        return
    }

    val prod = product!!

    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showLoginRequiredDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("Login Required to Buy", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "You must log in to your FarmVault account first before buying fresh produce. Please log in or create an account to proceed.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginRequiredDialog = false
                        viewModel.addToCart(prod, quantity)
                        viewModel.requireLoginFor(Screen.CHECKOUT, "Please log in first to complete your purchase of ${prod.name}.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Log In to Buy", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginRequiredDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = TropiCream,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(TropiBackgroundLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = ForestGreen)
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = ForestGreen)
                        }
                    }

                    // Add to Cart
                    OutlinedButton(
                        onClick = {
                            if (currentUser == null) {
                                showLoginRequiredDialog = true
                            } else {
                                viewModel.addToCart(prod, quantity)
                                showAddedSnackbar = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_to_cart_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestGreen)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = ForestGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }

                    // Buy Now
                    Button(
                        onClick = {
                            if (currentUser == null) {
                                showLoginRequiredDialog = true
                            } else {
                                viewModel.addToCart(prod, quantity)
                                viewModel.navigateTo(Screen.CHECKOUT)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                    ) {
                        Text("Buy Now", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        snackbarHost = {
            if (showAddedSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.navigateTo(Screen.CART) }) {
                            Text("View Cart", color = GoldenYellow, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Added $quantity x ${prod.name} to cart!")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Hero Fruit Visual Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                ProduceImage(
                    product = prod,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )

                // Preservation grade pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ForestGreen,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text(
                        text = prod.preservationGrade,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        color = GoldenYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Category & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prod.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = FreshGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = TropicalOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.2f / 5.0".format(prod.rating),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = prod.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Price
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "₱%.0f".format(prod.price),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = prod.unit,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Preservation Vault Statistics Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FarmVault Preservation Metrics",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            VaultMetric(
                                label = "Shelf-Life",
                                value = "${prod.shelfLifeDaysRemaining} Days",
                                icon = Icons.Default.HourglassTop,
                                tint = GoldenYellow
                            )
                            VaultMetric(
                                label = "Vault Temp",
                                value = prod.storageTemp,
                                icon = Icons.Default.Thermostat,
                                tint = TropicalOrange
                            )
                            VaultMetric(
                                label = "Stock In Vault",
                                value = "%.0f kg".format(prod.stockKg),
                                icon = Icons.Default.Inventory,
                                tint = FreshGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LeafMint,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Preservation Protocol:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = DeepGreen
                                )
                                Text(
                                    text = prod.preservationNotes,
                                    fontSize = 11.sp,
                                    color = DeepGreen,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Farm Provenance Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TropiPrimaryContainerLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = ForestGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = prod.farmName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DeepGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified Farm", tint = FreshGreen, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${prod.location} • ${prod.harvestDate}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Product Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = prod.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun VaultMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
        Text(text = label, color = Color.Gray, fontSize = 10.sp)
    }
}
