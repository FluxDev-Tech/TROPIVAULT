package com.example.tropivault.data.repository

import com.example.tropivault.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

class TropiVaultRepository(
    private val dao: TropiVaultDao,
    private val preferences: UserPreferences
) {

    // --- Preferences ---
    var isOnboardingCompleted: Boolean
        get() = preferences.isOnboardingCompleted
        set(value) {
            preferences.isOnboardingCompleted = value
        }

    var activeUserId: Long
        get() = preferences.activeUserId
        set(value) {
            preferences.activeUserId = value
        }

    fun getActiveUserFlow(): Flow<UserEntity?> {
        val id = activeUserId
        return if (id > 0) dao.getUserById(id) else flowOf(null)
    }

    fun logout() {
        preferences.clearSession()
    }

    // --- Users & Auth ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val pendingUsers: Flow<List<UserEntity>> = dao.getPendingUsers()
    val approvedRiders: Flow<List<UserEntity>> = dao.getApprovedRiders()

    suspend fun login(email: String, pass: String): UserEntity? {
        val user = dao.authenticate(email.trim(), pass)
        if (user != null) {
            activeUserId = user.id
        }
        return user
    }

    suspend fun register(user: UserEntity): Long {
        val id = dao.insertUser(user)
        if (user.status == "APPROVED") {
            activeUserId = id
        }
        return id
    }

    suspend fun updateUserStatus(userId: Long, status: String) {
        dao.updateUserStatus(userId, status)
        dao.insertNotification(
            NotificationEntity(
                userId = userId,
                targetRole = "ALL",
                title = "Account Status Updated",
                message = "Your FarmVault account has been marked as $status.",
                type = "APPROVAL"
            )
        )
    }

    // --- Products ---
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val approvedProducts: Flow<List<ProductEntity>> = dao.getApprovedProducts()

    fun getProductById(id: Long): Flow<ProductEntity?> = dao.getProductById(id)

    fun getProductsByFarm(farmId: Long): Flow<List<ProductEntity>> = dao.getProductsByFarm(farmId)

    suspend fun addProduct(product: ProductEntity): Long {
        val id = dao.insertProduct(product)
        dao.insertNotification(
            NotificationEntity(
                userId = 0,
                targetRole = "ADMIN",
                title = "New Product Submitted",
                message = "${product.name} from ${product.farmName} requires review.",
                type = "HARVEST"
            )
        )
        return id
    }

    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)

    suspend fun deleteProduct(product: ProductEntity) = dao.deleteProduct(product)

    suspend fun toggleProductApproval(productId: Long, isApproved: Boolean) {
        dao.updateProductApproval(productId, isApproved)
    }

    // --- Cart ---
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()

    suspend fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val existing = dao.getCartItemByProductId(product.id)
        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    productName = product.name,
                    price = product.price,
                    unit = product.unit,
                    quantity = quantity,
                    farmName = product.farmName,
                    imageUrl = product.imageUrl
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItem(cartItemId)
        } else {
            // Find and update
            // We can query all and update
        }
    }

    suspend fun removeCartItem(cartItemId: Long) = dao.deleteCartItem(cartItemId)

    suspend fun clearCart() = dao.clearCart()

    // --- Orders ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val unassignedDeliveries: Flow<List<OrderEntity>> = dao.getUnassignedDeliveries()

    fun getOrdersByUser(userId: Long): Flow<List<OrderEntity>> = dao.getOrdersByUser(userId)

    fun getOrdersByRider(riderId: Long): Flow<List<OrderEntity>> = dao.getOrdersByRider(riderId)

    fun getOrderById(orderId: Long): Flow<OrderEntity?> = dao.getOrderById(orderId)

    fun getOrderItems(orderId: Long): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

    suspend fun placeOrder(
        user: UserEntity?,
        customerName: String,
        customerPhone: String,
        fulfillmentType: String,
        deliveryAddress: String,
        pickupDepot: String,
        paymentMethod: String,
        paymentReference: String,
        items: List<CartItemEntity>,
        subtotal: Double,
        deliveryFee: Double
    ): Long {
        val randomSuffix = Random.nextInt(1000, 9999)
        val orderNumber = "TV-${System.currentTimeMillis() % 100000}-$randomSuffix"
        val paymentStatus = if (paymentMethod == "COD" || paymentMethod == "COP") "PENDING_COLLECTION" else "PENDING_VERIFICATION"

        val order = OrderEntity(
            orderNumber = orderNumber,
            userId = user?.id ?: 0,
            customerName = customerName,
            customerPhone = customerPhone,
            fulfillmentType = fulfillmentType,
            deliveryAddress = if (fulfillmentType == "DELIVERY") deliveryAddress else "Farm Depot Pickup",
            pickupDepot = pickupDepot,
            paymentMethod = paymentMethod,
            paymentReference = paymentReference,
            paymentStatus = paymentStatus,
            orderStatus = "PLACED",
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            totalAmount = subtotal + deliveryFee,
            createdAt = System.currentTimeMillis()
        )

        val orderId = dao.insertOrder(order)

        val orderItems = items.map { cartItem ->
            OrderItemEntity(
                orderId = orderId,
                productId = cartItem.productId,
                productName = cartItem.productName,
                price = cartItem.price,
                quantity = cartItem.quantity,
                unit = cartItem.unit,
                farmName = cartItem.farmName
            )
        }
        dao.insertOrderItems(orderItems)
        dao.clearCart()

        // Create Notifications
        dao.insertNotification(
            NotificationEntity(
                userId = user?.id ?: 0,
                targetRole = "CLIENT",
                title = "Order $orderNumber Placed!",
                message = "Thank you for supporting local tropical farmers. Total: ₱${subtotal + deliveryFee}",
                type = "ORDER"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = 0,
                targetRole = "ADMIN",
                title = "New Order $orderNumber Received",
                message = "$customerName placed an order via $paymentMethod.",
                type = "ORDER"
            )
        )
        return orderId
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        dao.updateOrderStatus(orderId, status)
    }

    suspend fun verifyPayment(orderId: Long) {
        dao.updatePaymentStatus(orderId, "VERIFIED")
        dao.updateOrderStatus(orderId, "PROCESSING")
    }

    suspend fun assignRider(orderId: Long, rider: UserEntity) {
        dao.assignRider(orderId, rider.id, "${rider.fullName} (${rider.vehicleType})")
        dao.insertNotification(
            NotificationEntity(
                userId = rider.id,
                targetRole = "RIDER",
                title = "New Delivery Assigned!",
                message = "You have been assigned order #$orderId.",
                type = "ORDER"
            )
        )
    }

    suspend fun completeDelivery(orderId: Long, collectedCod: Boolean, proofNotes: String) {
        dao.completeDelivery(orderId, collectedCod, proofNotes)
        if (collectedCod) {
            dao.updatePaymentStatus(orderId, "COLLECTED_BY_RIDER")
        }
    }

    // --- Notifications ---
    fun getNotifications(userId: Long, role: String): Flow<List<NotificationEntity>> =
        dao.getNotifications(userId, role)

    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
}
