package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.components.TropiVaultBottomBar
import com.example.tropivault.ui.components.TropiVaultTopBar
import com.example.tropivault.ui.screens.*
import com.example.tropivault.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TropiVaultTheme {
                TropiVaultApp()
            }
        }
    }
}

@Composable
fun TropiVaultApp(
    viewModel: TropiVaultViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()

    // Intercept back navigation
    BackHandler(enabled = currentScreen != Screen.PUBLIC_MARKETPLACE && currentScreen != Screen.ONBOARDING_START) {
        viewModel.navigateBack()
    }

    val isTopBarVisible = currentScreen != Screen.ONBOARDING_START && currentScreen != Screen.ONBOARDING_HOW_IT_WORKS && currentScreen != Screen.CHECKOUT
    val isBottomBarVisible = currentScreen in listOf(
        Screen.PUBLIC_MARKETPLACE,
        Screen.CART,
        Screen.ORDER_HISTORY,
        Screen.CLIENT_DASHBOARD,
        Screen.ADMIN_DASHBOARD,
        Screen.FARMER_DASHBOARD,
        Screen.RIDER_DASHBOARD,
        Screen.USER_PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isTopBarVisible) {
                TropiVaultTopBar(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    cartCount = cartCount,
                    onNavigateBack = { viewModel.navigateBack() },
                    onNavigateTo = { viewModel.navigateTo(it) },
                    onOpenRoleSwitcher = {
                        if (currentUser == null) {
                            viewModel.navigateTo(Screen.LOGIN)
                        } else {
                            when (currentUser?.role) {
                                "ADMIN" -> viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                                "FARMER" -> viewModel.navigateTo(Screen.FARMER_DASHBOARD)
                                "RIDER" -> viewModel.navigateTo(Screen.RIDER_DASHBOARD)
                                else -> viewModel.navigateTo(Screen.CLIENT_DASHBOARD)
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isBottomBarVisible) {
                TropiVaultBottomBar(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    onNavigateTo = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.ONBOARDING_START -> OnboardingScreen(viewModel = viewModel, initialPage = 0)
                Screen.ONBOARDING_HOW_IT_WORKS -> OnboardingScreen(viewModel = viewModel, initialPage = 1)
                Screen.PUBLIC_MARKETPLACE -> MarketplaceScreen(viewModel = viewModel)
                Screen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                Screen.CART -> CartScreen(viewModel = viewModel)
                Screen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                Screen.LOGIN -> LoginScreen(viewModel = viewModel)
                Screen.REGISTER -> RegisterScreen(viewModel = viewModel)
                Screen.CLIENT_DASHBOARD -> ClientDashboardScreen(viewModel = viewModel)
                Screen.FARMER_DASHBOARD -> FarmerDashboardScreen(viewModel = viewModel)
                Screen.RIDER_DASHBOARD -> RiderDashboardScreen(viewModel = viewModel)
                Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                Screen.ORDER_HISTORY -> OrderHistoryScreen(viewModel = viewModel)
                Screen.USER_PROFILE -> ProfileScreen(
                    viewModel = viewModel,
                    onOpenRoleSwitcher = {
                        if (currentUser == null) {
                            viewModel.navigateTo(Screen.LOGIN)
                        } else {
                            when (currentUser?.role) {
                                "ADMIN" -> viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                                "FARMER" -> viewModel.navigateTo(Screen.FARMER_DASHBOARD)
                                "RIDER" -> viewModel.navigateTo(Screen.RIDER_DASHBOARD)
                                else -> viewModel.navigateTo(Screen.CLIENT_DASHBOARD)
                            }
                        }
                    }
                )
                Screen.NOTIFICATIONS -> OrderHistoryScreen(viewModel = viewModel)
            }
        }
    }
}
