package com.example.tropivault.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun populateIfNeeded(dao: TropiVaultDao) = withContext(Dispatchers.IO) {
        val existingUsers = dao.getUserByEmail("admin@tropivault.com")
        if (existingUsers != null) return@withContext

        // 1. Seed Users
        val users = listOf(
            UserEntity(
                email = "admin@tropivault.com",
                password = "password123",
                fullName = "Maria Santos (TropiVault Admin)",
                role = "ADMIN",
                phone = "0917-888-0001",
                address = "TropiVault HQ, BGC Innovation Hub, Taguig",
                status = "APPROVED"
            ),
            UserEntity(
                email = "farmer.ramon@guimarasfarms.com",
                password = "password123",
                fullName = "Ramon Valderrama",
                role = "FARMER",
                phone = "0917-555-1234",
                address = "Barangay San Miguel, Jordan",
                farmName = "Guimaras Heritage Orchards",
                farmLocation = "Jordan, Guimaras Island",
                status = "APPROVED"
            ),
            UserEntity(
                email = "farmer.elena@davaobloom.com",
                password = "password123",
                fullName = "Elena Dizon",
                role = "FARMER",
                phone = "0918-222-7890",
                address = "Purok 4, Calinan",
                farmName = "Davao Bio-Valley Farms",
                farmLocation = "Calinan, Davao City",
                status = "APPROVED"
            ),
            UserEntity(
                email = "farmer.pending@mindanaoagri.com",
                password = "password123",
                fullName = "Arnel Macaraeg",
                role = "FARMER",
                phone = "0920-333-4455",
                address = "Sitio Kisolon, Sumilao",
                farmName = "Bukidnon Agro-Valley Highlands",
                farmLocation = "Sumilao, Bukidnon",
                status = "PENDING"
            ),
            UserEntity(
                email = "rider.jun@tropivault.com",
                password = "password123",
                fullName = "Jun Morales",
                role = "RIDER",
                phone = "0919-444-9876",
                address = "Pasig City, Metro Manila",
                vehicleType = "Motorcycle (Insulated Vault Box)",
                licenseNumber = "N02-18-994321",
                status = "APPROVED"
            ),
            UserEntity(
                email = "rider.pending@tropivault.com",
                password = "password123",
                fullName = "Bryan Bautista",
                role = "RIDER",
                phone = "0927-111-5566",
                address = "Mandaluyong City",
                vehicleType = "Eco Cargo Van (Refrigerated)",
                licenseNumber = "N01-20-441234",
                status = "PENDING"
            ),
            UserEntity(
                email = "client.sofia@freshbites.com",
                password = "password123",
                fullName = "Sofia Dela Cruz",
                role = "CLIENT",
                phone = "0917-999-2345",
                address = "Unit 14C, Bellagio Tower 2, BGC, Taguig City",
                status = "APPROVED"
            )
        )
        dao.insertUsers(users)

        // 2. Seed Products
        val products = listOf(
            ProductEntity(
                name = "Guimaras Super Sweet Carabao Mangoes",
                category = "Mangoes",
                description = "World-famous GI-certified Guimaras sweet mangoes. Selected at peak sweetness (Brix 18-20°). Stored in nitrogen-rich controlled atmosphere vault.",
                price = 260.0,
                unit = "per kg (approx 3-4 fruits)",
                stockKg = 450.0,
                farmId = 2,
                farmName = "Guimaras Heritage Orchards",
                location = "Jordan, Guimaras Island",
                harvestDate = "Harvested 24 hrs ago",
                preservationNotes = "Nitrogen-Flush Cold Vault 12°C, 92% RH (No Chemical Preservatives)",
                storageTemp = "12°C",
                shelfLifeDaysRemaining = 18,
                preservationGrade = "Vault Grade AAA",
                isApproved = true,
                isFeatured = true,
                rating = 4.95f
            ),
            ProductEntity(
                name = "Bukidnon High-Altitude Lacatan Bananas",
                category = "Bananas & Plantains",
                description = "Nutrient-dense volcanic soil bananas with deep golden aromatic flesh. Ethylene-controlled preservation slows over-ripening without losing vitamins.",
                price = 125.0,
                unit = "per kg bundle",
                stockKg = 600.0,
                farmId = 3,
                farmName = "Bukidnon Agro-Valley Highlands",
                location = "Bukidnon Highlands",
                harvestDate = "Harvested 36 hrs ago",
                preservationNotes = "Ethylene-Scrubbed Chamber at 14°C",
                storageTemp = "14°C",
                shelfLifeDaysRemaining = 14,
                preservationGrade = "Vault Grade A+",
                isApproved = true,
                isFeatured = true,
                rating = 4.85f
            ),
            ProductEntity(
                name = "Davao Golden Queen Pineapple",
                category = "Citrus & Melons",
                description = "Crisp, non-acidic golden pineapple with edible core. Harvested ripe and quickly hydro-chilled with clean solar mountain water.",
                price = 180.0,
                unit = "per piece (1.6-1.8kg)",
                stockKg = 320.0,
                farmId = 3,
                farmName = "Davao Bio-Valley Farms",
                location = "Calinan, Davao City",
                harvestDate = "Harvested 2 days ago",
                preservationNotes = "Solar Hydro-Chill at 10°C with Bio-Shield",
                storageTemp = "10°C",
                shelfLifeDaysRemaining = 22,
                preservationGrade = "Vault Grade A+",
                isApproved = true,
                isFeatured = true,
                rating = 4.90f
            ),
            ProductEntity(
                name = "Freeze-Dried Carabao Mango Crisps",
                category = "Preserved & Dehydrated",
                description = "100% pure ripe mango crisps prepared with sub-zero cryo-freeze drying. Keeps 98% of natural aroma, vitamins, and tropical crunch. No added sugar.",
                price = 210.0,
                unit = "per 120g resealable vault pouch",
                stockKg = 500.0,
                farmId = 2,
                farmName = "Guimaras Heritage Orchards",
                location = "Jordan, Guimaras Island",
                harvestDate = "Batch TV-2026-F08",
                preservationNotes = "Cryo-Lyophilization (Water activity < 0.2aw)",
                storageTemp = "Ambient Dry (< 25°C)",
                shelfLifeDaysRemaining = 180,
                preservationGrade = "Long-Life Vault Tier 1",
                isApproved = true,
                isFeatured = true,
                rating = 5.0f
            ),
            ProductEntity(
                name = "Batangas Purple Mangosteen",
                category = "Exotic & Rare",
                description = "Known as the Queen of Tropical Fruits. Plump, snow-white segments with sweet and tangy floral undertones. Preserved in low-oxygen hypobaric storage.",
                price = 350.0,
                unit = "per kg",
                stockKg = 150.0,
                farmId = 2,
                farmName = "Guimaras Heritage Orchards",
                location = "Lipa, Batangas",
                harvestDate = "Harvested yesterday",
                preservationNotes = "Hypobaric Micro-Climate Vault at 11°C",
                storageTemp = "11°C",
                shelfLifeDaysRemaining = 15,
                preservationGrade = "Vault Grade AAA",
                isApproved = true,
                isFeatured = false,
                rating = 4.92f
            ),
            ProductEntity(
                name = "Davao Pink Magallanes Pomelo",
                category = "Citrus & Melons",
                description = "Juicy, seedless pink segments dripping with sweet tropical citrus nectar. Wax-free breathable membrane locks in natural hydration.",
                price = 280.0,
                unit = "per box (2 large fruits)",
                stockKg = 280.0,
                farmId = 3,
                farmName = "Davao Bio-Valley Farms",
                location = "Calinan, Davao City",
                harvestDate = "Harvested 3 days ago",
                preservationNotes = "Chilled Micro-Perforated Biofilm Storage 13°C",
                storageTemp = "13°C",
                shelfLifeDaysRemaining = 28,
                preservationGrade = "Vault Grade A+",
                isApproved = true,
                isFeatured = true,
                rating = 4.88f
            ),
            ProductEntity(
                name = "Mindanao Red Pitahaya (Dragon Fruit)",
                category = "Exotic & Rare",
                description = "Vivid crimson flesh loaded with betalains and antioxidants. Organically cultivated under volcanic sun and ozone-purified for crisp freshness.",
                price = 230.0,
                unit = "per kg",
                stockKg = 190.0,
                farmId = 3,
                farmName = "Davao Bio-Valley Farms",
                location = "Davao del Sur",
                harvestDate = "Harvested this morning",
                preservationNotes = "Ozone Sanitized & Chill Vault 8°C",
                storageTemp = "8°C",
                shelfLifeDaysRemaining = 16,
                preservationGrade = "Vault Grade A",
                isApproved = true,
                isFeatured = false,
                rating = 4.78f
            ),
            ProductEntity(
                name = "Solar-Dehydrated Jackfruit Chews",
                category = "Preserved & Dehydrated",
                description = "Chewy golden strips of aromatic langka dried slowly inside hygienic solar thermal dehydrator domes. Naturally caramelized and rich in potassium.",
                price = 185.0,
                unit = "per 150g pouch",
                stockKg = 340.0,
                farmId = 3,
                farmName = "Davao Bio-Valley Farms",
                location = "Calinan, Davao City",
                harvestDate = "Batch TV-2026-S12",
                preservationNotes = "Solar Tunnel Dehydration at 48°C",
                storageTemp = "Ambient Dry",
                shelfLifeDaysRemaining = 120,
                preservationGrade = "Vault Grade A",
                isApproved = true,
                isFeatured = false,
                rating = 4.82f
            ),
            ProductEntity(
                name = "Tropical Immunity Vault Box (5kg Crate)",
                category = "Farm Bundles",
                description = "Curated multi-fruit basket: 2kg Guimaras Mangoes, 1kg Davao Pomelo, 1kg Lacatan Bananas, and 1 Pouch of Freeze-Dried Crisps in an insulated wooden vault box.",
                price = 920.0,
                unit = "per 5kg wooden vault crate",
                stockKg = 75.0,
                farmId = 2,
                farmName = "Guimaras Heritage Orchards",
                location = "Direct from Orchard Hubs",
                harvestDate = "Assembled daily",
                preservationNotes = "Insulated Thermal Crate with Eco-Ice packs",
                storageTemp = "12°C",
                shelfLifeDaysRemaining = 15,
                preservationGrade = "Curator's Choice Tier",
                isApproved = true,
                isFeatured = true,
                rating = 4.98f
            )
        )
        dao.insertProducts(products)

        // 3. Seed Initial Demo Order
        val sampleOrderId = dao.insertOrder(
            OrderEntity(
                orderNumber = "TV-2026-9041",
                userId = 7, // Sofia Dela Cruz
                customerName = "Sofia Dela Cruz",
                customerPhone = "0917-999-2345",
                fulfillmentType = "DELIVERY",
                deliveryAddress = "Unit 14C, Bellagio Tower 2, BGC, Taguig City",
                pickupDepot = "",
                paymentMethod = "GCASH",
                paymentReference = "GC-8902148119",
                paymentStatus = "VERIFIED",
                orderStatus = "READY_FOR_DISPATCH",
                subtotal = 730.0,
                deliveryFee = 75.0,
                totalAmount = 805.0,
                riderId = 5, // Jun Morales
                riderName = "Jun Morales (Motorcycle Vault)",
                codCollected = false,
                createdAt = System.currentTimeMillis() - 7200000L
            )
        )
        dao.insertOrderItems(
            listOf(
                OrderItemEntity(
                    orderId = sampleOrderId,
                    productId = 1,
                    productName = "Guimaras Super Sweet Carabao Mangoes",
                    price = 260.0,
                    quantity = 2,
                    unit = "per kg",
                    farmName = "Guimaras Heritage Orchards"
                ),
                OrderItemEntity(
                    orderId = sampleOrderId,
                    productId = 4,
                    productName = "Freeze-Dried Carabao Mango Crisps",
                    price = 210.0,
                    quantity = 1,
                    unit = "per 120g pouch",
                    farmName = "Guimaras Heritage Orchards"
                )
            )
        )

        // Second Order pending assignment for Admin & Rider testing
        val pendingOrderId = dao.insertOrder(
            OrderEntity(
                orderNumber = "TV-2026-9088",
                userId = 7,
                customerName = "Sofia Dela Cruz",
                customerPhone = "0917-999-2345",
                fulfillmentType = "DELIVERY",
                deliveryAddress = "Unit 14C, Bellagio Tower 2, BGC, Taguig City",
                pickupDepot = "",
                paymentMethod = "COD",
                paymentReference = "COD-PENDING",
                paymentStatus = "PENDING_VERIFICATION",
                orderStatus = "PROCESSING",
                subtotal = 360.0,
                deliveryFee = 75.0,
                totalAmount = 435.0,
                riderId = null,
                riderName = null,
                codCollected = false,
                createdAt = System.currentTimeMillis() - 1800000L
            )
        )
        dao.insertOrderItems(
            listOf(
                OrderItemEntity(
                    orderId = pendingOrderId,
                    productId = 3,
                    productName = "Davao Golden Queen Pineapple",
                    price = 180.0,
                    quantity = 2,
                    unit = "per piece",
                    farmName = "Davao Bio-Valley Farms"
                )
            )
        )

        // 4. Seed Notifications
        dao.insertNotification(
            NotificationEntity(
                userId = 0,
                targetRole = "ALL",
                title = "Welcome to TropiVault!",
                message = "Connecting farmers, clients, and riders with climate-smart food preservation.",
                type = "GENERAL"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = 0,
                targetRole = "ADMIN",
                title = "Pending Partner Registrations",
                message = "1 Farmer (Bukidnon Agro-Valley) and 1 Rider (Bryan Bautista) await approval.",
                type = "APPROVAL"
            )
        )
    }
}
