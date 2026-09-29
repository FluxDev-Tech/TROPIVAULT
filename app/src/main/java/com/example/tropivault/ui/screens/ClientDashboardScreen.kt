package com.example.tropivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tropivault.data.local.OrderEntity
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.StatusBadge
import com.example.tropivault.ui.components.TropiVaultDeliveryMap
import com.example.tropivault.ui.theme.*

/**
 * Dedicated, separate, fully functional Client Dashboard
 */
@Composable
fun ClientDashboardScreen(
    viewModel: TropiVaultViewModel
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val products by viewModel.approvedProducts.collectAsStateWithLifecycle()

    val myOrders = remember(allOrders, user) {
        if (user != null) allOrders.filter { it.userId == user!!.id } else allOrders.take(3)
    }
    val activeOrders = remember(myOrders) {
        myOrders.filter { it.orderStatus != "DELIVERED" && it.orderStatus != "COMPLETED" }
    }

    var showEditAddressDialog by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    var walletCredits by remember { mutableStateOf(350.0) }
    var topUpSuccessNotice by remember { mutableStateOf<String?>(null) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var supportTicketSent by remember { mutableStateOf(false) }

    // Dialog: Edit Delivery Address
    if (showEditAddressDialog) {
        var newAddressText by remember { mutableStateOf(user?.address ?: "") }
        AlertDialog(
            onDismissRequest = { showEditAddressDialog = false },
            title = { Text("Update Default Address", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your preferred delivery location for thermal cold deliveries:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newAddressText,
                        onValueChange = { newAddressText = it },
                        label = { Text("Street, Unit, Barangay, City") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAddress(newAddressText)
                        showEditAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAddressDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Top Up Wallet Credits
    if (showTopUpDialog) {
        var topUpAmountText by remember { mutableStateOf("500") }
        var gcashReference by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTopUpDialog = false },
            title = { Text("Top Up FarmVault Credits", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Add store credits for fast 1-tap checkout via GCash:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = topUpAmountText,
                        onValueChange = { topUpAmountText = it },
                        label = { Text("Top-Up Amount (₱)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Send to GCash 0917-888-VAULT then enter reference:", fontSize = 11.sp, color = DeepGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = gcashReference,
                        onValueChange = { gcashReference = it },
                        label = { Text("GCash Reference Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = topUpAmountText.toDoubleOrNull() ?: 500.0
                        walletCredits += amount
                        topUpSuccessNotice = "₱%.2f added to your FarmVault wallet balance!".format(amount)
                        showTopUpDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Text("Confirm Top Up")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopUpDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Support Ticket
    if (showSupportDialog) {
        var ticketTopic by remember { mutableStateOf("Produce Quality / Shelf-Life Inquiry") }
        var ticketMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Agritech Client Support", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (supportTicketSent) {
                        Text("Ticket logged successfully! An agronomist will reach out via email or phone.", color = FreshGreen)
                    } else {
                        Text("Connect directly with FarmVault's post-harvest logistics specialists:")
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = ticketTopic,
                            onValueChange = { ticketTopic = it },
                            label = { Text("Topic") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ticketMessage,
                            onValueChange = { ticketMessage = it },
                            label = { Text("Describe your request or question") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (supportTicketSent) {
                            showSupportDialog = false
                            supportTicketSent = false
                        } else {
                            supportTicketSent = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text(if (supportTicketSent) "Done" else "Submit Ticket")
                }
            },
            dismissButton = {
                if (!supportTicketSent) {
                    TextButton(onClick = { showSupportDialog = false }) { Text("Cancel") }
                }
            }
        )
    }

    Scaffold(
        containerColor = TropiCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Client Identity & Wallet Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(GoldenYellow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = DeepGreen,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = user?.fullName ?: "Valued Client",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "Client Portal • ${user?.email ?: "Direct Buyer"}",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldenYellow
                            ) {
                                Text(
                                    text = "CLIENT",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Wallet & Quick Action
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("FarmVault Balance", fontSize = 11.sp, color = GoldenYellow)
                                    Text(
                                        text = "₱%.2f".format(walletCredits),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                                Button(
                                    onClick = { showTopUpDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Top Up", color = DeepGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Notice
            if (topUpSuccessNotice != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(topUpSuccessNotice!!, color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 2. Sustainability & Food-Waste Prevention Impact
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = FreshGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your Tropical Food-Waste Impact",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepGreen
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ClientImpactMetric("24.8 kg", "Fresh Crop Saved", Icons.Default.Inventory2)
                            ClientImpactMetric("₱2,840", "Direct Farmgate Paid", Icons.Default.Savings)
                            ClientImpactMetric("+18 Days", "Avg Extra Freshness", Icons.Default.AcUnit)
                        }
                    }
                }
            }

            // 3. Active Orders & Tracking Tracker
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Shipments & Orders (${activeOrders.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                    TextButton(onClick = { viewModel.navigateTo(Screen.ORDER_HISTORY) }) {
                        Text("All Orders →", color = ForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (activeOrders.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No pending deliveries right now.", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.navigateTo(Screen.PUBLIC_MARKETPLACE) },
                                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Browse Orchard Harvests")
                            }
                        }
                    }
                }
            } else {
                items(activeOrders, key = { it.id }) { ord ->
                    ActiveClientOrderCard(order = ord)
                }
            }

            // 4. Saved Delivery Address Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(LeafMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreen)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Default Delivery Address", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                                Text(
                                    text = user?.address?.ifBlank { "No address set yet" } ?: "Unit 14C, BGC, Taguig",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 2
                                )
                            }
                        }

                        IconButton(onClick = { showEditAddressDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Address", tint = ForestGreen)
                        }
                    }
                }
            }

            // 5. Recommended Produce for Fast Reorder
            item {
                Text(
                    text = "Recommended From Verified Orchards",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products.take(4)) { prod ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .width(160.dp)
                                .clickable {
                                    viewModel.selectProduct(prod)
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, color = DeepGreen)
                                Text(prod.farmName, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("₱%.0f".format(prod.price), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestGreen)
                                    IconButton(
                                        onClick = { viewModel.addToCart(prod, 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Add", tint = FreshGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Support & Agritech Hotline
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LeafMint),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Need Help with an Order?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                            Text("Connect with cold-chain support or submit an inquiry.", fontSize = 11.sp, color = Color.DarkGray)
                        }
                        Button(
                            onClick = { showSupportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Support", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientImpactMetric(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LeafMint),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = DeepGreen)
        Text(label, fontSize = 9.sp, color = Color.Gray)
    }
}

@Composable
fun ActiveClientOrderCard(order: OrderEntity) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.orderNumber, fontWeight = FontWeight.Bold, color = DeepGreen, fontSize = 14.sp)
                StatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step Progress Indicator
            val currentStep = when (order.orderStatus) {
                "PLACED" -> 1
                "PROCESSING" -> 2
                "READY_FOR_DISPATCH" -> 3
                "IN_TRANSIT" -> 4
                "DELIVERED" -> 5
                else -> 1
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { currentStep / 5f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = FreshGreen,
                    trackColor = Color.LightGray.copy(alpha = 0.4f),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Packed in Vault", fontSize = 9.sp, color = if (currentStep >= 2) ForestGreen else Color.Gray, fontWeight = FontWeight.SemiBold)
                Text("Dispatched", fontSize = 9.sp, color = if (currentStep >= 3) ForestGreen else Color.Gray, fontWeight = FontWeight.SemiBold)
                Text("With Rider", fontSize = 9.sp, color = if (currentStep >= 4) ForestGreen else Color.Gray, fontWeight = FontWeight.SemiBold)
                Text("Delivered", fontSize = 9.sp, color = if (currentStep >= 5) FreshGreen else Color.Gray, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            var showLiveMap by remember { mutableStateOf(order.orderStatus == "IN_TRANSIT" || order.orderStatus == "PROCESSING") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (order.riderName != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rider: ${order.riderName}", fontSize = 11.sp, color = DeepGreen, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Text("Cold Vault Packaging in Progress", fontSize = 11.sp, color = Color.Gray)
                }

                TextButton(
                    onClick = { showLiveMap = !showLiveMap },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showLiveMap) "Hide Map" else "Kung Saan Na? (Map)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                }
            }

            if (showLiveMap) {
                Spacer(modifier = Modifier.height(8.dp))
                TropiVaultDeliveryMap(
                    order = order,
                    isRiderView = false
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ₱%.2f via %s".format(order.totalAmount, order.paymentMethod),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text("Insulated Thermal Box", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
