package com.example.tropivault.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.tropivault.data.local.ProductEntity
import com.example.tropivault.data.local.UserEntity
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.theme.*

@Composable
fun ProduceImage(
    product: ProductEntity,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val resId = when {
        product.imageUrl.contains("banana") || product.name.contains("Banana", ignoreCase = true) -> R.drawable.banana_cavendish_1790639668532
        product.imageUrl.contains("dragon") || product.imageUrl.contains("pitahaya") || product.name.contains("Pitahaya", ignoreCase = true) || product.name.contains("Mangosteen", ignoreCase = true) -> R.drawable.dragonfruit_pitahaya_1790639685341
        product.imageUrl.contains("pomelo") || product.imageUrl.contains("citrus") || product.imageUrl.contains("pineapple") || product.name.contains("Pomelo", ignoreCase = true) || product.name.contains("Pineapple", ignoreCase = true) -> R.drawable.pomelo_citrus_1790639699116
        product.imageUrl.contains("preservation") || product.category.contains("Preserved", ignoreCase = true) -> R.drawable.tropivault_preservation_1790637424122
        product.imageUrl.contains("mango") || product.name.contains("Mango", ignoreCase = true) -> R.drawable.mango_guimaras_1790639653425
        else -> R.drawable.tropivault_hero_harvest_1790637412571
    }

    Image(
        painter = painterResource(id = resId),
        contentDescription = product.name,
        modifier = modifier,
        contentScale = contentScale
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TropiVaultTopBar(
    currentScreen: Screen,
    currentUser: UserEntity?,
    cartCount: Int,
    onNavigateBack: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
    onOpenRoleSwitcher: () -> Unit
) {
    val canGoBack = currentScreen != Screen.PUBLIC_MARKETPLACE && currentScreen != Screen.ONBOARDING_START

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ForestGreen,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.farmvault_farm_logo_1790669532613),
                    contentDescription = "FarmVault Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                )
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FarmVault",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 21.sp,
                                letterSpacing = 0.8.sp,
                                color = GoldenYellow
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "VAULT",
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = DeepGreen
                                )
                            )
                        }
                    }
                    Text(
                        text = "Good Harvests. Longer Tomorrows.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        },
        navigationIcon = {
            if (canGoBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Cart Button
            IconButton(
                onClick = { onNavigateTo(Screen.CART) },
                modifier = Modifier.testTag("top_bar_cart_button")
            ) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = GoldenYellow,
                                contentColor = DeepGreen
                            ) {
                                Text(
                                    text = "$cartCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            }

            // User Dashboard / Login Button
            IconButton(
                onClick = {
                    if (currentUser == null) {
                        onNavigateTo(Screen.LOGIN)
                    } else {
                        when (currentUser.role) {
                            "ADMIN" -> onNavigateTo(Screen.ADMIN_DASHBOARD)
                            "FARMER" -> onNavigateTo(Screen.FARMER_DASHBOARD)
                            "RIDER" -> onNavigateTo(Screen.RIDER_DASHBOARD)
                            else -> onNavigateTo(Screen.CLIENT_DASHBOARD)
                        }
                    }
                },
                modifier = Modifier.testTag("top_bar_user_profile_button")
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (currentUser != null) GoldenYellow else Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (currentUser != null) {
                            val roleLetter = when (currentUser.role) {
                                "ADMIN" -> "A"
                                "FARMER" -> "F"
                                "RIDER" -> "R"
                                else -> "C"
                            }
                            Text(
                                text = roleLetter,
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen,
                                fontSize = 14.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Log In",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TropiVaultBottomBar(
    currentScreen: Screen,
    currentUser: UserEntity?,
    onNavigateTo: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // 1. Marketplace
        NavigationBarItem(
            selected = currentScreen == Screen.PUBLIC_MARKETPLACE || currentScreen == Screen.PRODUCT_DETAIL,
            onClick = { onNavigateTo(Screen.PUBLIC_MARKETPLACE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.PUBLIC_MARKETPLACE) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                    contentDescription = "Marketplace"
                )
            },
            label = { Text("Market", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ForestGreen,
                indicatorColor = TropiPrimaryContainerLight,
                selectedTextColor = ForestGreen
            ),
            modifier = Modifier.testTag("nav_item_marketplace")
        )

        // 2. Cart
        NavigationBarItem(
            selected = currentScreen == Screen.CART || currentScreen == Screen.CHECKOUT,
            onClick = { onNavigateTo(Screen.CART) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.CART) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                    contentDescription = "Cart"
                )
            },
            label = { Text("Cart", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ForestGreen,
                indicatorColor = TropiPrimaryContainerLight,
                selectedTextColor = ForestGreen
            ),
            modifier = Modifier.testTag("nav_item_cart")
        )

        // 3. Orders
        NavigationBarItem(
            selected = currentScreen == Screen.ORDER_HISTORY,
            onClick = { onNavigateTo(Screen.ORDER_HISTORY) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.ORDER_HISTORY) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                    contentDescription = "Orders"
                )
            },
            label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ForestGreen,
                indicatorColor = TropiPrimaryContainerLight,
                selectedTextColor = ForestGreen
            ),
            modifier = Modifier.testTag("nav_item_orders")
        )

        // 4. Dedicated Role Dashboard
        val (dashboardLabel, targetScreen, iconVector) = when (currentUser?.role) {
            "ADMIN" -> Triple("Admin Hub", Screen.ADMIN_DASHBOARD, Icons.Default.AdminPanelSettings)
            "FARMER" -> Triple("Farm Vault", Screen.FARMER_DASHBOARD, Icons.Default.Agriculture)
            "RIDER" -> Triple("Rider Hub", Screen.RIDER_DASHBOARD, Icons.Default.TwoWheeler)
            "CLIENT" -> Triple("Client Hub", Screen.CLIENT_DASHBOARD, Icons.Default.Dashboard)
            else -> Triple("Log In", Screen.LOGIN, Icons.Default.Person)
        }
        val isSelected = currentScreen in listOf(Screen.CLIENT_DASHBOARD, Screen.ADMIN_DASHBOARD, Screen.FARMER_DASHBOARD, Screen.RIDER_DASHBOARD, Screen.USER_PROFILE, Screen.LOGIN)

        NavigationBarItem(
            selected = isSelected,
            onClick = { onNavigateTo(targetScreen) },
            icon = {
                Icon(
                    imageVector = iconVector,
                    contentDescription = dashboardLabel
                )
            },
            label = { Text(dashboardLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ForestGreen,
                indicatorColor = TropiPrimaryContainerLight,
                selectedTextColor = ForestGreen
            ),
            modifier = Modifier.testTag("nav_item_dashboard")
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header Image Placeholder or Tropical Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                ProduceImage(
                    product = product,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient scrim overlay so text is crystal clear
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )

                // Category icon & shelf life badge
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ForestGreen
                        ) {
                            Text(
                                text = product.category,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Preservation Shelf Life Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldenYellow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = "Cold storage",
                                    tint = DeepGreen,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${product.shelfLifeDaysRemaining}d fresh",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepGreen
                                )
                            }
                        }
                    }

                    // Storage temperature badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Temp: ${product.storageTemp}",
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = GoldenYellow,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "%.1f".format(product.rating),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Body Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Farm Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = product.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₱%.0f".format(product.price),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen
                            )
                        )
                        Text(
                            text = product.unit,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 9.sp
                            )
                        )
                    }

                    FilledIconButton(
                        onClick = onAddToCart,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = FreshGreen),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("add_to_cart_button_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "APPROVED", "VERIFIED", "DELIVERED", "COMPLETED" -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), status)
        "PENDING", "PENDING_VERIFICATION", "PENDING_COLLECTION", "PROCESSING" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), status.replace("_", " "))
        "READY_FOR_DISPATCH", "IN_TRANSIT" -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), status.replace("_", " "))
        "REJECTED", "CANCELLED" -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), status)
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), status)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = textColor
            )
        )
    }
}
