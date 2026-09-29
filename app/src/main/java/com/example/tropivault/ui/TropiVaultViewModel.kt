package com.example.tropivault.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tropivault.data.local.*
import com.example.tropivault.data.repository.TropiVaultRepository
import com.example.tropivault.data.repository.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    ONBOARDING_START,
    ONBOARDING_HOW_IT_WORKS,
    PUBLIC_MARKETPLACE,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    LOGIN,
    REGISTER,
    CLIENT_DASHBOARD,
    FARMER_DASHBOARD,
    RIDER_DASHBOARD,
    ADMIN_DASHBOARD,
    ORDER_HISTORY,
    USER_PROFILE,
    NOTIFICATIONS
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedLocation: String = "All Locations",
    val sortBy: String = "Featured" // Featured, PriceLow, PriceHigh, ShelfLife
)

class TropiVaultViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val preferences = UserPreferences(application)
    val repository = TropiVaultRepository(database.tropiVaultDao(), preferences)

    init {
        viewModelScope.launch {
            DatabaseInitializer.populateIfNeeded(database.tropiVaultDao())
        }
    }

    // --- Navigation & Flow ---
    private val _currentScreen = MutableStateFlow(
        if (repository.isOnboardingCompleted) Screen.PUBLIC_MARKETPLACE else Screen.ONBOARDING_START
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    private val _postLoginDestination = MutableStateFlow<Screen?>(null)
    val postLoginDestination: StateFlow<Screen?> = _postLoginDestination.asStateFlow()

    private val _authPromptMessage = MutableStateFlow<String?>(null)
    val authPromptMessage: StateFlow<String?> = _authPromptMessage.asStateFlow()

    fun requireLoginFor(destination: Screen, reason: String = "Please log in first before buying from FarmVault.") {
        _postLoginDestination.value = destination
        _authPromptMessage.value = reason
        navigateTo(Screen.LOGIN)
    }

    fun clearAuthPrompt() {
        _authPromptMessage.value = null
    }

    fun consumePostLoginDestination(): Screen? {
        val dest = _postLoginDestination.value
        _postLoginDestination.value = null
        return dest
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            val prev = screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = prev
            return true
        }
        return false
    }

    fun completeOnboarding() {
        repository.isOnboardingCompleted = true
        navigateTo(Screen.PUBLIC_MARKETPLACE)
    }

    // --- Active User & Session ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val sessionState: StateFlow<com.example.tropivault.model.UserSessionState> = _currentUser.map { entity ->
        if (entity == null) {
            com.example.tropivault.model.UserSessionState.Unauthenticated
        } else {
            val user = com.example.tropivault.model.User.fromEntity(entity)
            if (user.isPending) {
                com.example.tropivault.model.UserSessionState.PendingApproval(
                    user,
                    "Your ${user.roleDisplayName} application is pending verification by FarmVault admin."
                )
            } else {
                com.example.tropivault.model.UserSessionState.Authenticated(user)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.tropivault.model.UserSessionState.Unauthenticated)

    val userRole: StateFlow<com.example.tropivault.model.UserRole?> = sessionState.map { it.activeRole }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isClient: StateFlow<Boolean> = sessionState.map { it.activeRole == com.example.tropivault.model.UserRole.CLIENT }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isFarmer: StateFlow<Boolean> = sessionState.map { it.activeRole == com.example.tropivault.model.UserRole.FARMER }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isRider: StateFlow<Boolean> = sessionState.map { it.activeRole == com.example.tropivault.model.UserRole.RIDER }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAdmin: StateFlow<Boolean> = sessionState.map { it.activeRole == com.example.tropivault.model.UserRole.ADMIN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pendingUsers = repository.pendingUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val approvedRiders = repository.approvedRiders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getActiveUserFlow().collect { user ->
                _currentUser.value = user
            }
        }
    }

    // --- Products & Filters ---
    val allProducts = repository.allProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val approvedProducts = repository.approvedProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filters = MutableStateFlow(FilterState())
    val filters: StateFlow<FilterState> = _filters.asStateFlow()

    fun updateSearchQuery(query: String) {
        _filters.value = _filters.value.copy(searchQuery = query)
    }

    fun selectCategory(category: String) {
        _filters.value = _filters.value.copy(selectedCategory = category)
    }

    fun selectLocation(location: String) {
        _filters.value = _filters.value.copy(selectedLocation = location)
    }

    fun selectSortBy(sort: String) {
        _filters.value = _filters.value.copy(sortBy = sort)
    }

    val filteredProducts = combine(approvedProducts, _filters) { products, filter ->
        var list = products

        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.farmName.lowercase().contains(q) ||
                it.location.lowercase().contains(q)
            }
        }

        if (filter.selectedCategory != "All") {
            list = list.filter { it.category.equals(filter.selectedCategory, ignoreCase = true) }
        }

        if (filter.selectedLocation != "All Locations") {
            list = list.filter { it.location.contains(filter.selectedLocation, ignoreCase = true) }
        }

        when (filter.sortBy) {
            "PriceLow" -> list.sortedBy { it.price }
            "PriceHigh" -> list.sortedByDescending { it.price }
            "ShelfLife" -> list.sortedByDescending { it.shelfLifeDaysRemaining }
            else -> list.sortedWith(compareByDescending<ProductEntity> { it.isFeatured }.thenByDescending { it.id })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Product Detail ---
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    fun selectProduct(product: ProductEntity) {
        _selectedProduct.value = product
        navigateTo(Screen.PRODUCT_DETAIL)
    }

    // --- Cart ---
    val cartItems = repository.cartItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartSubtotal = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount = cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartItemQuantity(cartItem: CartItemEntity, delta: Int) {
        viewModelScope.launch {
            val newQty = cartItem.quantity + delta
            if (newQty <= 0) {
                repository.removeCartItem(cartItem.id)
            } else {
                database.tropiVaultDao().updateCartItem(cartItem.copy(quantity = newQty))
            }
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // --- Orders ---
    val allOrders = repository.allOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unassignedDeliveries = repository.unassignedDeliveries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedOrder = MutableStateFlow<OrderEntity?>(null)
    val selectedOrder: StateFlow<OrderEntity?> = _selectedOrder.asStateFlow()

    fun selectOrder(order: OrderEntity) {
        _selectedOrder.value = order
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        fulfillmentType: String,
        deliveryAddress: String,
        pickupDepot: String,
        paymentMethod: String,
        paymentReference: String,
        deliveryFee: Double,
        onSuccess: (Long) -> Unit
    ) {
        val items = cartItems.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val subtotal = items.sumOf { it.price * it.quantity }
            val orderId = repository.placeOrder(
                user = _currentUser.value,
                customerName = customerName,
                customerPhone = customerPhone,
                fulfillmentType = fulfillmentType,
                deliveryAddress = deliveryAddress,
                pickupDepot = pickupDepot,
                paymentMethod = paymentMethod,
                paymentReference = paymentReference,
                items = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee
            )
            onSuccess(orderId)
        }
    }

    // --- Authentication ---
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    fun clearAuthMessages() {
        _authError.value = null
        _authSuccessMessage.value = null
    }

    fun login(email: String, pass: String, onSuccess: (UserEntity) -> Unit) {
        viewModelScope.launch {
            clearAuthMessages()
            val cleanEmail = email.trim()

            // Guaranteed store admin account
            if (cleanEmail.equals("admin@store.com", ignoreCase = true) && pass == "admin123") {
                var adminUser: UserEntity? = database.tropiVaultDao().getUserByEmail("admin@store.com")
                if (adminUser == null) {
                    val newAdmin = UserEntity(
                        email = "admin@store.com",
                        password = "admin123",
                        fullName = "Store Administrator",
                        role = "ADMIN",
                        phone = "0917-888-STORE",
                        address = "FarmVault Main Control Center, BGC Taguig",
                        status = "APPROVED"
                    )
                    val newId = database.tropiVaultDao().insertUser(newAdmin)
                    adminUser = newAdmin.copy(id = newId)
                }
                repository.activeUserId = adminUser.id
                _currentUser.value = adminUser
                onSuccess(adminUser)
                return@launch
            }

            val user = repository.login(cleanEmail, pass)
            if (user != null) {
                if (user.status == "PENDING") {
                    _authError.value = "Your ${user.role} account is pending approval by FarmVault admin."
                } else if (user.status == "REJECTED") {
                    _authError.value = "Your registration has been rejected. Please contact support."
                } else {
                    _currentUser.value = user
                    onSuccess(user)
                }
            } else {
                _authError.value = "Invalid email or password. Please try again."
            }
        }
    }

    fun updateAddress(newAddress: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(address = newAddress.trim())
            database.tropiVaultDao().updateUser(updated)
            _currentUser.value = updated
        }
    }

    fun editProduct(
        productId: Long,
        name: String,
        price: Double,
        stockKg: Double,
        storageTemp: String,
        shelfLifeDays: Int,
        preservationNotes: String
    ) {
        viewModelScope.launch {
            val existing = database.tropiVaultDao().getAllProducts().first().find { it.id == productId }
            if (existing != null) {
                val updated = existing.copy(
                    name = name,
                    price = price,
                    stockKg = stockKg,
                    storageTemp = storageTemp,
                    shelfLifeDaysRemaining = shelfLifeDays,
                    preservationNotes = preservationNotes
                )
                repository.updateProduct(updated)
            }
        }
    }

    private val _riderPayoutMessage = MutableStateFlow<String?>(null)
    val riderPayoutMessage: StateFlow<String?> = _riderPayoutMessage.asStateFlow()

    fun requestRiderPayout(amount: Double) {
        viewModelScope.launch {
            _riderPayoutMessage.value = "Payout request of ₱%.2f submitted to FarmVault Finance!".format(amount)
            database.tropiVaultDao().insertNotification(
                NotificationEntity(
                    userId = 1,
                    targetRole = "ADMIN",
                    title = "Rider Payout Request",
                    message = "${_currentUser.value?.fullName ?: "Rider"} requested a payout of ₱%.2f.".format(amount),
                    type = "PAYMENT"
                )
            )
        }
    }

    fun clearRiderPayoutMessage() {
        _riderPayoutMessage.value = null
    }

    fun quickLoginAs(role: String) {
        viewModelScope.launch {
            clearAuthMessages()
            val users = allUsers.value
            val target = when (role.uppercase()) {
                "ADMIN" -> users.find { it.role == "ADMIN" }
                "FARMER" -> users.find { it.role == "FARMER" && it.status == "APPROVED" }
                "RIDER" -> users.find { it.role == "RIDER" && it.status == "APPROVED" }
                "CLIENT" -> users.find { it.role == "CLIENT" }
                else -> null
            }
            if (target != null) {
                repository.activeUserId = target.id
                _currentUser.value = target
                when (target.role) {
                    "ADMIN" -> navigateTo(Screen.ADMIN_DASHBOARD)
                    "FARMER" -> navigateTo(Screen.FARMER_DASHBOARD)
                    "RIDER" -> navigateTo(Screen.RIDER_DASHBOARD)
                    else -> navigateTo(Screen.PUBLIC_MARKETPLACE)
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        navigateTo(Screen.PUBLIC_MARKETPLACE)
    }

    fun registerClient(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        address: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            clearAuthMessages()
            val existing = database.tropiVaultDao().getUserByEmail(email)
            if (existing != null) {
                _authError.value = "An account with this email already exists."
                return@launch
            }
            val user = UserEntity(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                role = "CLIENT",
                phone = phone.trim(),
                address = address.trim(),
                status = "APPROVED"
            )
            repository.register(user)
            _currentUser.value = user
            _authSuccessMessage.value = "Account created successfully! Welcome to FarmVault."
            onSuccess()
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "CANCELLED")
        }
    }

    fun confirmOrderDelivered(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "DELIVERED")
        }
    }

    fun registerFarmer(
        fullName: String,
        farmName: String,
        email: String,
        phone: String,
        farmLocation: String,
        password: String,
        autoApprove: Boolean = true,
        onSuccess: (UserEntity) -> Unit
    ) {
        viewModelScope.launch {
            clearAuthMessages()
            val existing = database.tropiVaultDao().getUserByEmail(email)
            if (existing != null) {
                _authError.value = "An account with this email already exists."
                return@launch
            }
            val user = UserEntity(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                role = "FARMER",
                phone = phone.trim(),
                farmName = farmName.trim(),
                farmLocation = farmLocation.trim(),
                status = if (autoApprove) "APPROVED" else "PENDING"
            )
            repository.register(user)
            if (autoApprove) {
                _currentUser.value = user
            }
            _authSuccessMessage.value = if (autoApprove) "Farmer account activated! Welcome to your Farm Vault dashboard." else "Farmer registration submitted! An admin will review and approve your orchard vault credentials."
            onSuccess(user)
        }
    }

    fun registerRider(
        fullName: String,
        email: String,
        phone: String,
        address: String,
        vehicleType: String,
        licenseNumber: String,
        password: String,
        autoApprove: Boolean = true,
        onSuccess: (UserEntity) -> Unit
    ) {
        viewModelScope.launch {
            clearAuthMessages()
            val existing = database.tropiVaultDao().getUserByEmail(email)
            if (existing != null) {
                _authError.value = "An account with this email already exists."
                return@launch
            }
            val user = UserEntity(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                role = "RIDER",
                phone = phone.trim(),
                address = address.trim(),
                vehicleType = vehicleType.trim(),
                licenseNumber = licenseNumber.trim(),
                status = if (autoApprove) "APPROVED" else "PENDING"
            )
            repository.register(user)
            if (autoApprove) {
                _currentUser.value = user
            }
            _authSuccessMessage.value = if (autoApprove) "Rider account activated! Welcome to your Rider delivery dashboard." else "Rider registration submitted! An admin will review your climate-control transport details."
            onSuccess(user)
        }
    }

    fun registerAdmin(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        adminKey: String,
        onSuccess: (UserEntity) -> Unit
    ) {
        viewModelScope.launch {
            clearAuthMessages()
            if (adminKey.trim() != "TROPI2026" && adminKey.trim().uppercase() != "ADMIN") {
                _authError.value = "Invalid Admin Authorization Key. Master key required for Admin registration."
                return@launch
            }
            val existing = database.tropiVaultDao().getUserByEmail(email)
            if (existing != null) {
                _authError.value = "An account with this email already exists."
                return@launch
            }
            val user = UserEntity(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                role = "ADMIN",
                phone = phone.trim(),
                status = "APPROVED"
            )
            repository.register(user)
            _currentUser.value = user
            _authSuccessMessage.value = "Administrator account created and verified! Welcome to FarmVault Control Center."
            onSuccess(user)
        }
    }

    // --- Farmer Actions ---
    fun addProduct(
        name: String,
        category: String,
        description: String,
        price: Double,
        unit: String,
        stockKg: Double,
        storageTemp: String,
        shelfLifeDays: Int,
        preservationNotes: String,
        imageUrl: String = "",
        autoApproved: Boolean = false
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                category = category,
                description = description,
                price = price,
                unit = unit,
                stockKg = stockKg,
                farmId = user.id,
                farmName = if (user.farmName.isNotBlank()) user.farmName else user.fullName,
                location = if (user.farmLocation.isNotBlank()) user.farmLocation else "Direct Farm",
                harvestDate = "Just Harvested",
                preservationNotes = preservationNotes,
                storageTemp = storageTemp,
                shelfLifeDaysRemaining = shelfLifeDays,
                isApproved = autoApproved,
                isFeatured = false,
                rating = 5.0f,
                imageUrl = imageUrl
            )
            repository.addProduct(product)
            database.tropiVaultDao().insertNotification(
                NotificationEntity(
                    userId = 0,
                    targetRole = "ADMIN",
                    title = "New Produce Awaiting Approval",
                    message = "${product.farmName} submitted ${product.name} (${product.stockKg.toInt()} kg). Review in Catalog to publish to Marketplace.",
                    type = "PRODUCT"
                )
            )
        }
    }

    fun updateProductStock(productId: Long, newStock: Double) {
        viewModelScope.launch {
            database.tropiVaultDao().updateProductStock(productId, newStock)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // --- Rider Actions ---
    fun assignRider(orderId: Long, rider: UserEntity) {
        viewModelScope.launch {
            repository.assignRider(orderId, rider)
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun completeDelivery(orderId: Long, codCollected: Boolean, proofNotes: String) {
        viewModelScope.launch {
            repository.completeDelivery(orderId, codCollected, proofNotes)
        }
    }

    // --- Admin Actions ---
    fun approveUser(userId: Long) {
        viewModelScope.launch {
            repository.updateUserStatus(userId, "APPROVED")
        }
    }

    fun rejectUser(userId: Long) {
        viewModelScope.launch {
            repository.updateUserStatus(userId, "REJECTED")
        }
    }

    fun toggleProductApproval(productId: Long, currentApproved: Boolean) {
        viewModelScope.launch {
            repository.toggleProductApproval(productId, !currentApproved)
        }
    }

    fun verifyPayment(orderId: Long) {
        viewModelScope.launch {
            repository.verifyPayment(orderId)
        }
    }

    // --- Notifications ---
    val notifications = _currentUser.flatMapLatest { user ->
        val userId = user?.id ?: 0
        val role = user?.role ?: "CLIENT"
        repository.getNotifications(userId, role)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }
}
