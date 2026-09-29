package com.example.tropivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.tropivault.data.local.OrderEntity
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.StatusBadge
import com.example.tropivault.ui.theme.*

/**
 * Dedicated, separate, fully functional Rider Dashboard
 */
@Composable
fun RiderDashboardScreen(
    viewModel: TropiVaultViewModel
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val unassignedOrders by viewModel.unassignedDeliveries.collectAsStateWithLifecycle()
    val payoutNotice by viewModel.riderPayoutMessage.collectAsStateWithLifecycle()

    val riderId = user?.id ?: 5
    val myDeliveries = remember(allOrders, riderId) {
        allOrders.filter { it.riderId == riderId }
    }
    val activeDeliveries = remember(myDeliveries) {
        myDeliveries.filter { it.orderStatus != "DELIVERED" && it.orderStatus != "COMPLETED" && it.orderStatus != "CANCELLED" }
    }
    val completedDeliveries = remember(myDeliveries) {
        myDeliveries.filter { it.orderStatus == "DELIVERED" || it.orderStatus == "COMPLETED" }
    }

    var selectedOrderForProof by remember { mutableStateOf<OrderEntity?>(null) }
    var showPayoutDialog by remember { mutableStateOf(false) }

    val totalEarnings = completedDeliveries.size * 75.0

    // Dialog: Request Payout
    if (showPayoutDialog) {
        var gcashNumber by remember { mutableStateOf(user?.phone?.ifBlank { "0917-888-9999" } ?: "0917-888-9999") }
        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            title = { Text("Request Rider Payout", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Transfer your delivery fees directly to your verified e-wallet:")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Available Payout: ₱%.2f".format(totalEarnings), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gcashNumber,
                        onValueChange = { gcashNumber = it },
                        label = { Text("GCash Mobile Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.requestRiderPayout(totalEarnings)
                        showPayoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Text("Confirm Transfer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Complete Delivery Proof
    if (selectedOrderForProof != null) {
        val ord = selectedOrderForProof!!
        var codChecked by remember { mutableStateOf(ord.paymentMethod == "COD") }
        var proofNotes by remember { mutableStateOf("Delivered in pristine chilled thermal box. Received by customer.") }

        AlertDialog(
            onDismissRequest = { selectedOrderForProof = null },
            title = { Text("Complete Delivery & POD", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Order: ${ord.orderNumber}", fontWeight = FontWeight.Bold, color = DeepGreen)
                    Text("Customer: ${ord.customerName}", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (ord.paymentMethod == "COD") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GoldLight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = codChecked,
                                    onCheckedChange = { codChecked = it }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("COD Cash Collected", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepGreen)
                                    Text("Amount: ₱%.2f".format(ord.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = ForestGreen)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = proofNotes,
                        onValueChange = { proofNotes = it },
                        label = { Text("Proof of Delivery / Receiver Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.completeDelivery(ord.id, codChecked, proofNotes)
                        selectedOrderForProof = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Text("Confirm Delivery Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOrderForProof = null }) {
                    Text("Cancel")
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
            // 1. Rider Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GoldenYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user?.fullName ?: "Jun Morales",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "${user?.vehicleType ?: "Motorcycle (Insulated Vault)"} • License: ${user?.licenseNumber ?: "N02-18-994321"}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RiderStatItem("Active Trips", "${activeDeliveries.size}")
                            RiderStatItem("Completed", "${completedDeliveries.size}")
                            RiderStatItem("Earnings", "₱%.0f".format(totalEarnings))

                            if (totalEarnings > 0) {
                                Button(
                                    onClick = { showPayoutDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Payout", color = DeepGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Payout notification
            if (payoutNotice != null) {
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
                            Text(payoutNotice!!, color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 2. Cold-Box Telemetry Health Check
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LeafMint),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AcUnit, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Insulated Box Thermal Sensor", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepGreen)
                                Text("Active Seal: 11.8°C (Optimal Produce Safe)", fontSize = 10.sp, color = DeepGreen.copy(alpha = 0.8f))
                            }
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = FreshGreen) {
                            Text("CHILL OK", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // 3. Active Deliveries Section
            item {
                Text(
                    text = "My Active Trips (${activeDeliveries.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
            }

            if (activeDeliveries.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No active deliveries in progress. Accept an available trip below to begin transit.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(activeDeliveries, key = { it.id }) { ord ->
                    ActiveRiderOrderCard(
                        order = ord,
                        onUpdateStatus = { newStatus: String -> viewModel.updateOrderStatus(ord.id, newStatus) },
                        onCompletePOD = { selectedOrderForProof = ord }
                    )
                }
            }

            // 4. Available Orders to Accept
            item {
                Text(
                    text = "Available Orders for Pickup (${unassignedOrders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
            }

            if (unassignedOrders.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No unassigned orders at the moment. All dispatches are covered.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(unassignedOrders, key = { it.id }) { ord ->
                    UnassignedOrderCard(
                        order = ord,
                        onAccept = {
                            if (user != null) {
                                viewModel.assignRider(ord.id, user!!)
                            }
                        }
                    )
                }
            }

            // 5. Completed Deliveries History
            if (completedDeliveries.isNotEmpty()) {
                item {
                    Text(
                        text = "Completed Trips History (${completedDeliveries.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }

                items(completedDeliveries, key = { it.id }) { ord ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ord.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                                Text("Delivered to ${ord.customerName}", fontSize = 11.sp, color = Color.Gray)
                                if (ord.proofNotes != null) {
                                    Text("POD: ${ord.proofNotes}", fontSize = 10.sp, color = FreshGreen)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                                    Text("DELIVERED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                                }
                                Text("+₱75 Fee", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiderStatItem(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GoldenYellow
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun ActiveRiderOrderCard(
    order: OrderEntity,
    onUpdateStatus: (String) -> Unit,
    onCompletePOD: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepGreen)
                    Text("Recipient: ${order.customerName}", fontSize = 12.sp, color = Color.DarkGray)
                }
                StatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = ForestGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(order.customerPhone, fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(order.deliveryAddress, fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldLight
                ) {
                    Text(
                        text = "Payment: ${order.paymentMethod} (₱%.2f)".format(order.totalAmount),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                }
                Text("Trip Fee: +₱75.00", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (order.orderStatus != "IN_TRANSIT") {
                    Button(
                        onClick = { onUpdateStatus("IN_TRANSIT") },
                        colors = ButtonDefaults.buttonColors(containerColor = TropicalOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Transit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onCompletePOD,
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Deliver & POD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UnassignedOrderCard(
    order: OrderEntity,
    onAccept: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
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
                Column {
                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                    Text("Drop-off: ${order.customerName}", fontSize = 11.sp, color = Color.Gray)
                }
                Text("₱%.2f".format(order.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = ForestGreen)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(order.deliveryAddress, fontSize = 11.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(4.dp), color = LeafMint) {
                    Text("Rider Fee: ₱75.00", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = DeepGreen, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Accept Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
