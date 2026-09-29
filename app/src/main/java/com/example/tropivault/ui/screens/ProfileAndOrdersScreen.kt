package com.example.tropivault.ui.screens

import androidx.compose.foundation.background
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
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.StatusBadge
import com.example.tropivault.ui.theme.*

@Composable
fun OrderHistoryScreen(
    viewModel: TropiVaultViewModel
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()

    val myOrders = remember(allOrders, user) {
        if (user == null || user?.role == "ADMIN") {
            allOrders
        } else {
            allOrders.filter { it.userId == user?.id }
        }
    }

    Scaffold(
        containerColor = TropiCream
    ) { innerPadding ->
        if (myOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Orders Placed Yet", fontWeight = FontWeight.Bold, color = DeepGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Your placed orders and delivery tracking will appear here.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.PUBLIC_MARKETPLACE) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Browse Marketplace")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = if (user?.role == "ADMIN") "All Platform Orders (${myOrders.size})" else "My Orders & Deliveries (${myOrders.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }

                items(myOrders, key = { it.id }) { order ->
                    CustomerOrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(order: OrderEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.orderNumber, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = DeepGreen)
                    Text("Fulfillment: ${order.fulfillmentType}", fontSize = 11.sp, color = Color.Gray)
                }
                StatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            if (order.fulfillmentType == "DELIVERY") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TropicalOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(order.deliveryAddress, fontSize = 12.sp, color = Color.DarkGray)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(order.pickupDepot, fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            if (order.riderName != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dispatched via: ${order.riderName}", fontSize = 12.sp, color = DeepGreen, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Payment: ${order.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
                    StatusBadge(status = order.paymentStatus)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Grand Total", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = "₱%.2f".format(order.totalAmount),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = ForestGreen
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: TropiVaultViewModel,
    onOpenRoleSwitcher: () -> Unit
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = TropiCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Avatar & Name
            Surface(
                shape = CircleShape,
                color = ForestGreen,
                border = androidx.compose.foundation.BorderStroke(3.dp, GoldenYellow),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user?.fullName?.take(1)?.uppercase() ?: "T",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = Color.White
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = user?.fullName ?: "Guest User",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
                Text(
                    text = user?.email ?: "Browse mode",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldenYellow
                ) {
                    Text(
                        text = (user?.role ?: "CLIENT") + " ACCOUNT",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }
            }

            // Profile Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (user?.farmName?.isNotBlank() == true) {
                        ProfileInfoRow(Icons.Default.Agriculture, "Farm / Orchard", user!!.farmName)
                        ProfileInfoRow(Icons.Default.LocationOn, "Farm Location", user!!.farmLocation)
                    }
                    if (user?.vehicleType?.isNotBlank() == true) {
                        ProfileInfoRow(Icons.Default.TwoWheeler, "Delivery Vehicle", user!!.vehicleType)
                        ProfileInfoRow(Icons.Default.Badge, "Driver's License", user!!.licenseNumber)
                    }
                    if (user?.phone?.isNotBlank() == true) {
                        ProfileInfoRow(Icons.Default.Phone, "Mobile Contact", user!!.phone)
                    }
                    if (user?.address?.isNotBlank() == true) {
                        ProfileInfoRow(Icons.Default.Home, "Address", user!!.address)
                    }
                }
            }

            // Open Dedicated Role Dashboard
            if (user != null) {
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
                            Text("Dedicated ${user?.role} Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                            Text("Access your dedicated operations and live portal.", fontSize = 11.sp, color = DeepGreen.copy(alpha = 0.8f))
                        }
                        Button(
                            onClick = {
                                when (user?.role) {
                                    "ADMIN" -> viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                                    "FARMER" -> viewModel.navigateTo(Screen.FARMER_DASHBOARD)
                                    "RIDER" -> viewModel.navigateTo(Screen.RIDER_DASHBOARD)
                                    else -> viewModel.navigateTo(Screen.CLIENT_DASHBOARD)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open Hub", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout / Login button
            if (user != null) {
                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("profile_logout_button")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.navigateTo(Screen.LOGIN) },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Log In or Register", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DeepGreen)
        }
    }
}
