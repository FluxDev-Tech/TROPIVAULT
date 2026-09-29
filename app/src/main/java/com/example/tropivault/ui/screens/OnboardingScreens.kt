package com.example.tropivault.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Unified OnboardingScreen featuring a PageView (HorizontalPager)
 * accommodating 'Get Started' (page 0) and 'How It Works' (page 1)
 * with tropical branding, swipe gestures, and synchronized navigation logic.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: TropiVaultViewModel,
    initialPage: Int = 0
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2 })

    // If initialPage changes externally (e.g. navigation back to start), sync pager
    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage) {
            pagerState.scrollToPage(initialPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_page_view")
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> GetStartedPageView(
                    pagerState = pagerState,
                    onGetStarted = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    onLogin = { viewModel.navigateTo(Screen.LOGIN) },
                    onSkip = { viewModel.completeOnboarding() }
                )
                1 -> HowItWorksPageView(
                    pagerState = pagerState,
                    onBack = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onExploreMarketplace = { viewModel.completeOnboarding() },
                    onCreateAccount = { viewModel.navigateTo(Screen.REGISTER) },
                    onSkip = { viewModel.completeOnboarding() }
                )
            }
        }
    }
}

/**
 * Backward compatibility alias if individual screens are requested directly.
 */
@Composable
fun GetStartedScreen(viewModel: TropiVaultViewModel) {
    OnboardingScreen(viewModel = viewModel, initialPage = 0)
}

@Composable
fun HowItWorksScreen(viewModel: TropiVaultViewModel) {
    OnboardingScreen(viewModel = viewModel, initialPage = 1)
}

/**
 * Page 0: 'Get Started' View
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GetStartedPageView(
    pagerState: PagerState,
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkForest)
    ) {
        // Hero Background Image with Gradient Overlay
        Image(
            painter = painterResource(id = R.drawable.tropivault_hero_harvest_1790637412571),
            contentDescription = "Tropical Harvest",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.45f
        )

        // Gradient Dark Tint for High Contrast Readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkForest.copy(alpha = 0.85f),
                            DarkForest.copy(alpha = 0.90f),
                            DarkForest.copy(alpha = 0.96f),
                            DarkForest
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Logo & High-Contrast Brand Badge (Symmetrically Centered & Farm-Themed)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ForestGreen.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldenYellow),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)
                ) {
                    // Perfectly Centered Farm Logo Emblem
                    Surface(
                        shape = CircleShape,
                        color = GoldenYellow,
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, GoldenYellow),
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(108.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.farmvault_farm_logo_1790669532613),
                            contentDescription = "FarmVault Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Perfectly Symmetrical Centered Title
                    Text(
                        text = "FarmVault",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = GoldenYellow,
                            fontSize = 36.sp,
                            letterSpacing = 1.2.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldenYellow
                    ) {
                        Text(
                            text = "FARM FRESH & COLD VAULT",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepGreen,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Connecting Verified Farms Directly to You",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // Bottom Content & Navigation Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ForestGreen.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenYellow.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AcUnit,
                            contentDescription = null,
                            tint = GoldenYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Climate-Controlled Food Vaults",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "FarmVault\nGood Harvests. Longer Tomorrows.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Discover fresh farm produce, support local growers, and extend freshness up to 3x with FarmVault.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // PageView Indicator Dots
                TropicalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = GoldenYellow,
                    inactiveColor = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Primary CTA: Get Started (Advances PageView)
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_get_started_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldenYellow,
                        contentColor = DeepGreen
                    )
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary CTA: Log In
                OutlinedButton(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_login_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip to Marketplace →",
                        color = GoldenYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Page 1: 'How It Works' View
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HowItWorksPageView(
    pagerState: PagerState,
    onBack: () -> Unit,
    onExploreMarketplace: () -> Unit,
    onCreateAccount: () -> Unit,
    onSkip: () -> Unit
) {
    Scaffold(
        containerColor = TropiCream,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("how_it_works_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Get Started",
                        tint = ForestGreen
                    )
                }
                Text(
                    text = "How It Works",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("how_it_works_skip_button")
                ) {
                    Text("Skip", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // PageView Indicator Dots
                    TropicalPagerIndicator(
                        pagerState = pagerState,
                        activeColor = ForestGreen,
                        inactiveColor = ForestGreen.copy(alpha = 0.25f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Button(
                        onClick = onExploreMarketplace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("explore_marketplace_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Explore Marketplace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Storefront, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ready to start trading?",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = onCreateAccount,
                            modifier = Modifier.testTag("how_it_works_register_link")
                        ) {
                            Text(
                                text = "Create Account",
                                color = FreshGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome to FarmVault",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Connecting farmers, clients, and riders with FarmVault climate-controlled storage vaults.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Feature Card 1: Discover Farm Produce
            item {
                FeatureCard(
                    number = "1",
                    title = "Discover Farm Produce",
                    description = "Browse rare and heirloom fruits and crops preserved at peak sweetness direct from verified farms.",
                    icon = Icons.Default.Search,
                    accentColor = GoldenYellow
                )
            }

            // Feature Card 2: Support Local Farmers
            item {
                FeatureCard(
                    number = "2",
                    title = "Support Local Farmers",
                    description = "Guarantee fair farmgate pricing, empower agricultural communities, and make every seasonal harvest count.",
                    icon = Icons.Default.VolunteerActivism,
                    accentColor = FreshGreen
                )
            }

            // Feature Card 3: Order and Pay Easily
            item {
                FeatureCard(
                    number = "3",
                    title = "Order and Pay Easily",
                    description = "Fast, transparent checkout supporting GCash, Philippine Bank Transfer, Cash on Delivery, or Cash on Pickup.",
                    icon = Icons.Default.Payment,
                    accentColor = TropicalOrange
                )
            }

            // Feature Card 4: Delivery or Pickup
            item {
                FeatureCard(
                    number = "4",
                    title = "Delivery or Pickup",
                    description = "Enjoy swift climate-controlled delivery in thermal vault boxes by verified riders or schedule regional depot pickup.",
                    icon = Icons.Default.LocalShipping,
                    accentColor = ForestGreen
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Animated PageView Dots Indicator
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TropicalPagerIndicator(
    pagerState: PagerState,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val isCurrent = pagerState.currentPage == iteration
            val width by animateDpAsState(
                targetValue = if (isCurrent) 28.dp else 8.dp,
                label = "indicator_width"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (isCurrent) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
fun FeatureCard(
    number: String,
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
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
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = accentColor,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = number,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (accentColor == GoldenYellow) DeepGreen else Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}
