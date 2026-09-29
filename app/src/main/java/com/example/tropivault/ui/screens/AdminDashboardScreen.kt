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
import com.example.tropivault.data.local.ProductEntity
import com.example.tropivault.data.local.UserEntity
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.StatusBadge
import com.example.tropivault.ui.theme.*

/**
 * Dedicated, separate, fully functional Admin Control Center Dashboard
 */
@Composable
fun AdminDashboardScreen(
    viewModel: TropiVaultViewModel
) {
    val pendingUsers by viewModel.pendingUsers.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val approvedRiders by viewModel.approvedRiders.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var selectedAdminTab by remember { mutableStateOf("OVERVIEW") } // OVERVIEW, APPROVALS, PAYMENTS, DISPATCH, CATALOG, USERS
    var selectedOrderForRiderAssign by remember { mutableStateOf<OrderEntity?>(null) }
    var userSearchQuery by remember { mutableStateOf("") }

    // Dialog: Assign Rider
    if (selectedOrderForRiderAssign != null) {
        val order = selectedOrderForRiderAssign!!
        AlertDialog(
            onDismissRequest = { selectedOrderForRiderAssign = null },
            title = { Text("Assign Rider to ${order.orderNumber}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select from verified cold-chain delivery riders:")
                    Spacer(modifier = Modifier.height(10.dp))
                    if (approvedRiders.isEmpty()) {
                        Text("No approved riders available. Please approve pending riders first.", color = Color.Red, fontSize = 12.sp)
                    } else {
                        approvedRiders.forEach { rider ->
                            Button(
                                onClick = {
                                    viewModel.assignRider(order.id, rider)
                                    selectedOrderForRiderAssign = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(rider.fullName, fontWeight = FontWeight.Bold)
                                    Text(rider.vehicleType, fontSize = 11.sp, color = GoldenYellow)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedOrderForRiderAssign = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    val totalGMV = remember(allOrders) {
        allOrders.sumOf { it.totalAmount }
    }
    val pendingPayments = remember(allOrders) {
        allOrders.filter { it.paymentStatus == "PENDING_VERIFICATION" }
    }
    val unassignedOrders = remember(allOrders) {
        allOrders.filter { it.fulfillmentType == "DELIVERY" && it.riderId == null && it.orderStatus != "DELIVERED" && it.orderStatus != "CANCELLED" }
    }
    val filteredUsers = remember(allUsers, userSearchQuery) {
        if (userSearchQuery.isBlank()) allUsers
        else {
            val q = userSearchQuery.trim().lowercase()
            allUsers.filter {
                it.fullName.lowercase().contains(q) ||
                it.email.lowercase().contains(q) ||
                it.role.lowercase().contains(q)
            }
        }
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
            // 1. Admin Header
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepGreen),
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
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "TropiVault Control Center",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "System Administration • Full Platform Oversight",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = FreshGreen) {
                                Text("LIVE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AdminMetric("Platform GMV", "₱%.0f".format(totalGMV))
                            AdminMetric("Total Orders", "${allOrders.size}")
                            AdminMetric("Pending Approval", "${pendingUsers.size}")
                            AdminMetric("Active Crops", "${allProducts.size}")
                        }
                    }
                }
            }

            // 2. Navigation Section Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AdminFilterChip("Overview", selectedAdminTab == "OVERVIEW", Modifier.weight(1f)) { selectedAdminTab = "OVERVIEW" }
                        AdminFilterChip("Approvals (${pendingUsers.size})", selectedAdminTab == "APPROVALS", Modifier.weight(1f)) { selectedAdminTab = "APPROVALS" }
                        AdminFilterChip("Payments (${pendingPayments.size})", selectedAdminTab == "PAYMENTS", Modifier.weight(1f)) { selectedAdminTab = "PAYMENTS" }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AdminFilterChip("Dispatch (${unassignedOrders.size})", selectedAdminTab == "DISPATCH", Modifier.weight(1f)) { selectedAdminTab = "DISPATCH" }
                        AdminFilterChip("Catalog (${allProducts.size})", selectedAdminTab == "CATALOG", Modifier.weight(1f)) { selectedAdminTab = "CATALOG" }
                        AdminFilterChip("Users (${allUsers.size})", selectedAdminTab == "USERS", Modifier.weight(1f)) { selectedAdminTab = "USERS" }
                    }
                }
            }

            // 3. Section: Pending Partner Approvals (Farmers & Riders)
            if (selectedAdminTab == "OVERVIEW" || selectedAdminTab == "APPROVALS") {
                item {
                    Text(
                        text = "Partner Applications Awaiting Verification (${pendingUsers.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }

                if (pendingUsers.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "All partner registrations are verified and up to date.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(pendingUsers, key = { it.id }) { pendingUser ->
                        PendingUserCard(
                            user = pendingUser,
                            onApprove = { viewModel.approveUser(pendingUser.id) },
                            onReject = { viewModel.rejectUser(pendingUser.id) }
                        )
                    }
                }
            }

            // 4. Section: Payment Verification & Orders
            if (selectedAdminTab == "OVERVIEW" || selectedAdminTab == "PAYMENTS") {
                item {
                    Text(
                        text = "Orders & GCash/Bank Payment Verification (${allOrders.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }

                val ordersToShow = if (selectedAdminTab == "PAYMENTS") pendingPayments else allOrders
                if (ordersToShow.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No orders pending verification.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(ordersToShow, key = { it.id }) { order ->
                        AdminOrderCard(
                            order = order,
                            onVerifyPayment = { viewModel.verifyPayment(order.id) },
                            onAssignRider = { selectedOrderForRiderAssign = order }
                        )
                    }
                }
            }

            // 5. Section: Rider Dispatcher
            if (selectedAdminTab == "DISPATCH") {
                item {
                    Text(
                        text = "Unassigned Delivery Orders (${unassignedOrders.size})",
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
                                text = "All active delivery trips have an assigned driver.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(unassignedOrders, key = { it.id }) { order ->
                        AdminOrderCard(
                            order = order,
                            onVerifyPayment = { viewModel.verifyPayment(order.id) },
                            onAssignRider = { selectedOrderForRiderAssign = order }
                        )
                    }
                }
            }

            // 6. Section: Product Catalog Moderation
            if (selectedAdminTab == "OVERVIEW" || selectedAdminTab == "CATALOG") {
                item {
                    Text(
                        text = "Orchard Catalog Moderation (${allProducts.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }

                items(allProducts, key = { it.id }) { product ->
                    AdminProductModerationRow(
                        product = product,
                        onToggleApproval = { viewModel.toggleProductApproval(product.id, product.isApproved) }
                    )
                }
            }

            // 7. Section: User Management
            if (selectedAdminTab == "USERS") {
                item {
                    Column {
                        Text(
                            text = "Registered Platform Users (${allUsers.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userSearchQuery,
                            onValueChange = { userSearchQuery = it },
                            label = { Text("Search users by name, email or role") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForestGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                items(filteredUsers, key = { it.id }) { usr ->
                    UserManagementCard(
                        user = usr,
                        onToggleStatus = {
                            if (usr.status == "APPROVED") viewModel.rejectUser(usr.id)
                            else viewModel.approveUser(usr.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun PendingUserCard(
    user: UserEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
                    Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepGreen)
                    Text(user.email, fontSize = 11.sp, color = Color.Gray)
                }
                StatusBadge(status = user.role)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (user.role == "FARMER") {
                Text("Farm: ${user.farmName.ifBlank { "Independent Farm" }} • Location: ${user.farmLocation}", fontSize = 11.sp, color = Color.DarkGray)
            } else if (user.role == "RIDER") {
                Text("Vehicle: ${user.vehicleType} • License: ${user.licenseNumber}", fontSize = 11.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Reject", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Approve & Activate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: OrderEntity,
    onVerifyPayment: () -> Unit,
    onAssignRider: () -> Unit
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
                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepGreen)
                    Text("Customer: ${order.customerName} • ${order.customerPhone}", fontSize = 11.sp, color = Color.Gray)
                }
                Text("₱%.2f".format(order.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = ForestGreen)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = order.orderStatus)
                StatusBadge(status = order.paymentStatus)
                Surface(shape = RoundedCornerShape(4.dp), color = LeafMint) {
                    Text(order.paymentMethod, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = DeepGreen, fontWeight = FontWeight.Bold)
                }
            }

            if (order.paymentReference.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Payment Ref: ${order.paymentReference}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DeepGreen)
            }

            if (order.fulfillmentType == "DELIVERY") {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Address: ${order.deliveryAddress}", fontSize = 10.sp, color = Color.Gray)
                if (order.riderName != null) {
                    Text("Assigned Driver: ${order.riderName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = FreshGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (order.paymentStatus == "PENDING_VERIFICATION") {
                    Button(
                        onClick = onVerifyPayment,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenYellow),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Verify Payment", fontSize = 11.sp, color = DeepGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (order.fulfillmentType == "DELIVERY" && order.orderStatus != "DELIVERED") {
                    OutlinedButton(
                        onClick = onAssignRider,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(if (order.riderName == null) "Assign Rider" else "Reassign", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductModerationRow(
    product: ProductEntity,
    onToggleApproval: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                Text("${product.farmName} • ₱%.0f/%s • %dkg in vault".format(product.price, product.unit, product.stockKg.toInt()), fontSize = 11.sp, color = Color.Gray)
                Text("Cold Vault: ${product.storageTemp} • ${product.shelfLifeDaysRemaining}d remaining", fontSize = 10.sp, color = FreshGreen)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (product.isApproved) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = if (product.isApproved) "ACTIVE" else "DELISTED",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (product.isApproved) Color(0xFF15803D) else Color(0xFFB91C1C)
                    )
                }

                Switch(
                    checked = product.isApproved,
                    onCheckedChange = { onToggleApproval() }
                )
            }
        }
    }
}

@Composable
fun UserManagementCard(
    user: UserEntity,
    onToggleStatus: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(status = user.role)
                }
                Text(user.email, fontSize = 11.sp, color = Color.Gray)
                if (user.phone.isNotBlank()) {
                    Text("Phone: ${user.phone}", fontSize = 10.sp, color = Color.DarkGray)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = user.status)
                Spacer(modifier = Modifier.height(4.dp))
                if (user.role != "ADMIN") {
                    TextButton(
                        onClick = onToggleStatus,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (user.status == "APPROVED") "Suspend" else "Activate",
                            fontSize = 10.sp,
                            color = if (user.status == "APPROVED") Color.Red else FreshGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFilterChip(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) ForestGreen else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else Color.LightGray.copy(alpha = 0.5f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else DeepGreen
            )
        }
    }
}
