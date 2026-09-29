package com.example.tropivault.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tropivault.data.local.OrderEntity
import com.example.tropivault.ui.theme.*

/**
 * In-app interactive delivery route map showing origin farm, rider position, and client destination ("Kung Saan Na").
 */
@Composable
fun TropiVaultDeliveryMap(
    order: OrderEntity,
    isRiderView: Boolean = false,
    modifier: Modifier = Modifier,
    onStatusUpdate: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    // Pulsing radar animation for rider icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Animated rider position along the route (0.0 to 1.0)
    val riderProgress = when (order.orderStatus) {
        "PLACED" -> 0.1f
        "PROCESSING" -> 0.25f
        "READY_FOR_DISPATCH" -> 0.35f
        "IN_TRANSIT" -> 0.68f
        "DELIVERED", "COMPLETED" -> 1.0f
        else -> 0.5f
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Status & Kung Saan Na
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = GoldenYellow, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isRiderView) "Active Navigation Corridor" else "Live Tracking (Kung Saan Na)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepGreen
                        )
                        Text(
                            text = "Order #${order.orderNumber}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (order.orderStatus) {
                        "IN_TRANSIT" -> Color(0xFFE0E7FF)
                        "DELIVERED" -> Color(0xFFDCFCE7)
                        else -> GoldLight
                    }
                ) {
                    Text(
                        text = if (order.orderStatus == "IN_TRANSIT") "EN ROUTE • 12 MINS" else order.orderStatus,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.orderStatus) {
                            "IN_TRANSIT" -> Color(0xFF4338CA)
                            "DELIVERED" -> Color(0xFF15803D)
                            else -> TropicalOrange
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual GPS Vector Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B)) // Modern dark slate map theme
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid / Street lines
                    val streetColor = Color(0xFF334155)
                    val highwayColor = Color(0xFF475569)

                    // Secondary streets
                    for (i in 1..4) {
                        val y = height * (i / 5f)
                        drawLine(streetColor, Offset(0f, y), Offset(width, y), strokeWidth = 2f)
                    }
                    for (i in 1..5) {
                        val x = width * (i / 6f)
                        drawLine(streetColor, Offset(x, 0f), Offset(x, height), strokeWidth = 2f)
                    }

                    // Main highway diagonal
                    drawLine(highwayColor, Offset(0f, height * 0.7f), Offset(width, height * 0.3f), strokeWidth = 7f)

                    // River / Waterfront curve
                    val riverPath = Path().apply {
                        moveTo(0f, height * 0.9f)
                        cubicTo(width * 0.3f, height * 0.8f, width * 0.7f, height * 0.95f, width, height * 0.85f)
                    }
                    drawPath(riverPath, Color(0xFF0F766E).copy(alpha = 0.4f), style = Stroke(width = 10f))

                    // Route Points
                    val originX = width * 0.15f
                    val originY = height * 0.72f
                    val destX = width * 0.85f
                    val destY = height * 0.28f

                    // Curved Route Corridor
                    val routePath = Path().apply {
                        moveTo(originX, originY)
                        cubicTo(width * 0.35f, height * 0.75f, width * 0.6f, height * 0.32f, destX, destY)
                    }

                    // Route Shadow & Line
                    drawPath(routePath, Color(0xFFF5B700).copy(alpha = 0.25f), style = Stroke(width = 10f, cap = StrokeCap.Round))
                    drawPath(routePath, FreshGreen, style = Stroke(width = 4f, cap = StrokeCap.Round))

                    // Origin Pin (Farm Vault)
                    drawCircle(Color(0xFF075B45), radius = 10f, center = Offset(originX, originY))
                    drawCircle(GoldenYellow, radius = 5f, center = Offset(originX, originY))

                    // Destination Pin (Client)
                    drawCircle(Color(0xFFDC2626), radius = 10f, center = Offset(destX, destY))
                    drawCircle(Color.White, radius = 5f, center = Offset(destX, destY))

                    // Current Rider Position along route
                    val riderX = originX + (destX - originX) * riderProgress
                    val riderY = originY + (destY - originY) * riderProgress

                    if (order.orderStatus != "DELIVERED") {
                        // Pulsing radar wave
                        drawCircle(
                            color = GoldenYellow.copy(alpha = pulseAlpha),
                            radius = pulseRadius,
                            center = Offset(riderX, riderY)
                        )
                    }

                    // Rider marker
                    drawCircle(Color.White, radius = 9f, center = Offset(riderX, riderY))
                    drawCircle(FreshGreen, radius = 6f, center = Offset(riderX, riderY))
                }

                // Map Waypoint Floating Badges
                Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    // Origin Badge (Bottom Left)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ForestGreen.copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = GoldenYellow, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Farm Vault Origin", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Destination Badge (Top Right)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF991B1B).copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delivery Destination", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Telemetry & Details Row
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = LeafMint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = order.riderName ?: "Assigned Rider: Jun Morales",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen
                            )
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = FreshGreen) {
                            Text("11.8°C CHILLED", modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (order.orderStatus) {
                            "IN_TRANSIT" -> "🛵 En route on C5 Express Corridor • 1.4 km from destination"
                            "DELIVERED", "COMPLETED" -> "✅ Successfully handed over in pristine cold-vault condition"
                            "READY_FOR_DISPATCH" -> "📦 Sealed in motorcycle cold box at Solar Hub Gate 2"
                            else -> "🌱 Harvest packed and chilling in Farm Vault at 12°C"
                        },
                        fontSize = 11.sp,
                        color = DeepGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Drop-off Address: ${order.deliveryAddress}",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        openExternalMap(context, order.deliveryAddress)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open in Maps", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                if (isRiderView && onStatusUpdate != null) {
                    if (order.orderStatus != "IN_TRANSIT" && order.orderStatus != "DELIVERED") {
                        Button(
                            onClick = { onStatusUpdate("IN_TRANSIT") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TropicalOrange),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Transit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (order.orderStatus == "IN_TRANSIT") {
                        Button(
                            onClick = { onStatusUpdate("DELIVERED") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Arrived & Deliver", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            val phone = order.customerPhone.ifBlank { "0917-888-0001" }
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            try {
                                context.startActivity(callIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Contact: $phone", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRiderView) "Call Customer" else "Call Rider", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun openExternalMap(context: Context, address: String) {
    try {
        val uri = Uri.parse("geo:0,0?q=" + Uri.encode(address))
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address)))
            context.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Destination: $address", Toast.LENGTH_SHORT).show()
    }
}
