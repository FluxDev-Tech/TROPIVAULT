package com.example.tropivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.tropivault.data.local.CartItemEntity
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.theme.*

@Composable
fun CartScreen(
    viewModel: TropiVaultViewModel
) {
    val items by viewModel.cartItems.collectAsStateWithLifecycle()
    val subtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = TropiCream,
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Estimated Subtotal", color = Color.Gray, fontSize = 12.sp)
                                Text(
                                    text = "₱%.2f".format(subtotal),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ForestGreen
                                    )
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(Screen.CHECKOUT) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("cart_proceed_checkout_button")
                            ) {
                                Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GoldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = TropicalOrange,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your TropiVault Cart is Empty",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore fresh tropical harvests and climate-preserved fruit packs.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.PUBLIC_MARKETPLACE) },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explore Marketplace", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Shopping Cart (${items.sumOf { it.quantity }})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen
                            )
                        )
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Clear All", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.updateCartItemQuantity(item, 1) },
                        onDecrement = { viewModel.updateCartItemQuantity(item, -1) },
                        onRemove = { viewModel.removeCartItem(item.id) }
                    )
                }

                item {
                    // Preservation Packaging Assurance Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LeafMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AcUnit, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Guaranteed Cold-Chain Packed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DeepGreen
                                )
                                Text(
                                    text = "All items packed inside recyclable thermal insulation with eco-chilling pads at no extra fee.",
                                    fontSize = 11.sp,
                                    color = DeepGreen.copy(alpha = 0.85f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GoldLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DeepGreen,
                    maxLines = 1
                )
                Text(
                    text = item.farmName,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₱%.0f / %s".format(item.price, item.unit),
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen,
                    fontSize = 12.sp
                )
            }

            // Stepper & Delete
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(TropiBackgroundLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 2.dp)
                ) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = ForestGreen, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = ForestGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: TropiVaultViewModel
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val subtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()

    var fulfillmentType by remember { mutableStateOf("DELIVERY") }
    var customerName by remember(user) { mutableStateOf(user?.fullName ?: "Sofia Dela Cruz") }
    var customerPhone by remember(user) { mutableStateOf(user?.phone ?: "0917-999-2345") }
    var deliveryAddress by remember(user) { mutableStateOf(user?.address ?: "Unit 14C, Bellagio Tower 2, BGC, Taguig City") }
    var selectedPickupDepot by remember { mutableStateOf("BGC Cold Storage Hub (Open 8am-8pm)") }

    var paymentMethod by remember { mutableStateOf("GCASH") }
    var selectedBank by remember { mutableStateOf("BDO Unibank") }
    var paymentReference by remember { mutableStateOf("") }
    var orderSuccessDialogOrderId by remember { mutableStateOf<Long?>(null) }

    val deliveryFee = if (fulfillmentType == "DELIVERY") 75.0 else 0.0
    val totalAmount = subtotal + deliveryFee

    val pickupDepots = listOf(
        "BGC Cold Storage Hub (Open 8am-8pm)",
        "Ortigas Climate Vault Depot (Open 7am-9pm)",
        "Guimaras Central Harvest Depot (Jordan)",
        "Davao Bio-Valley Depot (Calinan)"
    )

    val bankList = listOf(
        "BDO Unibank",
        "BPI (Bank of the Philippine Islands)",
        "Metrobank",
        "UnionBank of the Philippines",
        "LandBank of the Philippines"
    )

    if (orderSuccessDialogOrderId != null) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FreshGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            },
            title = {
                Text(
                    text = "Order Placed Successfully!",
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Thank you for supporting sustainable tropical agriculture! Your produce is secured in our cold-chain system.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LeafMint,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Payment: $paymentMethod", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DeepGreen)
                            Text("Fulfillment: $fulfillmentType", fontSize = 11.sp, color = DeepGreen)
                            Text("Total: ₱%.2f".format(totalAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        orderSuccessDialogOrderId = null
                        viewModel.navigateTo(Screen.ORDER_HISTORY)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier.testTag("checkout_success_view_orders_button")
                ) {
                    Text("Track Order in Orders")
                }
            }
        )
    }

    Scaffold(
        containerColor = TropiCream,
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Amount", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = "₱%.2f".format(totalAmount),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForestGreen
                                )
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.placeOrder(
                                    customerName = customerName,
                                    customerPhone = customerPhone,
                                    fulfillmentType = fulfillmentType,
                                    deliveryAddress = deliveryAddress,
                                    pickupDepot = selectedPickupDepot,
                                    paymentMethod = paymentMethod,
                                    paymentReference = if (paymentReference.isNotBlank()) paymentReference else "REF-${System.currentTimeMillis() % 100000}",
                                    deliveryFee = deliveryFee,
                                    onSuccess = { orderId ->
                                        orderSuccessDialogOrderId = orderId
                                    }
                                )
                            },
                            enabled = customerName.isNotBlank() && customerPhone.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("checkout_place_order_button")
                        ) {
                            Text("Confirm & Place Order", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Fulfillment Type Selector
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Fulfillment Method",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = fulfillmentType == "DELIVERY",
                            onClick = { fulfillmentType = "DELIVERY" },
                            label = { Text("Thermal Delivery (₱75)", fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.DeliveryDining, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = fulfillmentType == "PICKUP",
                            onClick = { fulfillmentType = "PICKUP" },
                            label = { Text("Depot Pickup (Free)", fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (fulfillmentType == "DELIVERY") {
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Delivery Address (Street, Barangay, City)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )
                    } else {
                        Text(
                            text = "Select Regional Pickup Depot:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = DeepGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        pickupDepots.forEach { depot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPickupDepot = depot }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPickupDepot == depot,
                                    onClick = { selectedPickupDepot = depot }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(depot, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 2. Customer Contact Details
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Contact Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Full Name or Business Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Mobile Contact Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            // 3. Payment Method
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Payment Option",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val methods = listOf(
                        Triple("GCASH", "GCash e-Wallet", Icons.Default.QrCode),
                        Triple("BANK_TRANSFER", "Philippine Bank Transfer", Icons.Default.AccountBalance),
                        Triple("COD", "Cash on Delivery", Icons.Default.Payments),
                        Triple("COP", "Cash on Pickup", Icons.Default.PointOfSale)
                    )

                    methods.forEach { (key, label, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { paymentMethod = key }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = paymentMethod == key,
                                onClick = { paymentMethod = key }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Payment Configuration Display
                    when (paymentMethod) {
                        "GCASH" -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF007DFE).copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF007DFE).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF007DFE)
                                        ) {
                                            Text(
                                                text = "GCash",
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "TropiVault Agritech Inc.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Account Number: 0917-888-VAULT (82858)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Please send exact amount and enter reference number below.", fontSize = 11.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = paymentReference,
                                        onValueChange = { paymentReference = it },
                                        placeholder = { Text("e.g. 10294819284 (GCash Reference)") },
                                        label = { Text("GCash Transaction Reference No.") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        "BANK_TRANSFER" -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = LeafMint,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Select Bank Partner:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepGreen)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    bankList.forEach { bank ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedBank = bank }
                                                .padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(selected = selectedBank == bank, onClick = { selectedBank = bank })
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(bank, fontSize = 12.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Account Name: TropiVault Enterprise Ltd.", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Account No: 0041-8899-2311 (Savings)", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = paymentReference,
                                        onValueChange = { paymentReference = it },
                                        placeholder = { Text("Bank Deposit / Wire Reference No.") },
                                        label = { Text("Bank Transaction Reference") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        "COD" -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TropiBackgroundLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = ForestGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Please prepare ₱%.2f in cash upon rider delivery. The rider carries a sealed cold-box and will issue an e-receipt.".format(totalAmount),
                                        fontSize = 11.sp,
                                        color = DeepGreen
                                    )
                                }
                            }
                        }

                        "COP" -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TropiBackgroundLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = ForestGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Pay at the pickup depot cash counter when claiming your vault-insulated crate.",
                                        fontSize = 11.sp,
                                        color = DeepGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Order Summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.productName}", fontSize = 12.sp, color = Color.DarkGray)
                            Text("₱%.0f".format(item.price * item.quantity), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 12.sp, color = Color.Gray)
                        Text("₱%.2f".format(subtotal), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Climate-Vault Packaging", fontSize = 12.sp, color = Color.Gray)
                        Text("FREE (Eco-Ice included)", fontSize = 12.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fulfillment Fee (${fulfillmentType.lowercase()})", fontSize = 12.sp, color = Color.Gray)
                        Text("₱%.2f".format(deliveryFee), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount Due", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepGreen)
                        Text("₱%.2f".format(totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ForestGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
