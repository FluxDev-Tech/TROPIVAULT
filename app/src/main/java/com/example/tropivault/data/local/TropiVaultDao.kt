package com.example.tropivault.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TropiVaultDao {

    // --- Users ---
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'RIDER' AND status = 'APPROVED'")
    fun getApprovedRiders(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun authenticate(email: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET status = :status WHERE id = :id")
    suspend fun updateUserStatus(id: Long, status: String)

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isApproved = 1 ORDER BY isFeatured DESC, id DESC")
    fun getApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE farmId = :farmId ORDER BY id DESC")
    fun getProductsByFarm(farmId: Long): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET isApproved = :isApproved WHERE id = :id")
    suspend fun updateProductApproval(id: Long, isApproved: Boolean)

    @Query("UPDATE products SET stockKg = :stock WHERE id = :id")
    suspend fun updateProductStock(id: Long, stock: Double)

    // --- Cart ---
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: Long): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUser(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE riderId = :riderId ORDER BY createdAt DESC")
    fun getOrdersByRider(riderId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE fulfillmentType = 'DELIVERY' AND riderId IS NULL ORDER BY createdAt DESC")
    fun getUnassignedDeliveries(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: Long): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String)

    @Query("UPDATE orders SET paymentStatus = :status WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, status: String)

    @Query("UPDATE orders SET riderId = :riderId, riderName = :riderName, orderStatus = 'READY_FOR_DISPATCH' WHERE id = :orderId")
    suspend fun assignRider(orderId: Long, riderId: Long, riderName: String)

    @Query("UPDATE orders SET codCollected = :collected, proofNotes = :proof, orderStatus = 'DELIVERED' WHERE id = :orderId")
    suspend fun completeDelivery(orderId: Long, collected: Boolean, proof: String)

    // --- Order Items ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: Long): Flow<List<OrderItemEntity>>

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId OR targetRole = :role OR targetRole = 'ALL' ORDER BY timestamp DESC")
    fun getNotifications(userId: Long, role: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)
}
