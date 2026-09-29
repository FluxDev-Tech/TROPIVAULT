package com.example.tropivault.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.tropivault.ui.Screen
import com.example.tropivault.ui.TropiVaultViewModel
import com.example.tropivault.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: TropiVaultViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showGoogleSetupDialog by remember { mutableStateOf(false) }

    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authSuccess by viewModel.authSuccessMessage.collectAsStateWithLifecycle()
    val authPrompt by viewModel.authPromptMessage.collectAsStateWithLifecycle()

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(email) }
        var resetSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Password Recovery") },
            text = {
                Column {
                    if (resetSent) {
                        Text("Recovery instructions sent to $resetEmail.", color = FreshGreen)
                    } else {
                        Text("Enter your registered email address to receive password reset instructions.")
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetSent) {
                            showForgotPasswordDialog = false
                        } else {
                            resetSent = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text(if (resetSent) "Close" else "Send Reset Link")
                }
            }
        )
    }

    if (showGoogleSetupDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleSetupDialog = false },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = ForestGreen) },
            title = { Text("Google Sign-In") },
            text = {
                Column {
                    Text(
                        "In production environments, Google Sign-In connects via OAuth 2.0 Web Client ID registered in Google Cloud Console.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "You can register your own real account using the 'Create Account' link below for Client, Farmer, Rider, or Administrator roles.",
                        fontSize = 12.sp,
                        color = DeepGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGoogleSetupDialog = false
                        viewModel.navigateTo(Screen.REGISTER)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Create New Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleSetupDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        containerColor = TropiCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo & Header
            Image(
                painter = painterResource(id = R.drawable.farmvault_farm_logo_1790669532613),
                contentDescription = "FarmVault Logo",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome to FarmVault",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = DeepGreen,
                    fontSize = 26.sp
                )
            )
            Text(
                text = "Good Harvests. Longer Tomorrows.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Messages
            if (authPrompt != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldenYellow.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldenYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = ForestGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = authPrompt!!,
                            color = DeepGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (authError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = authError!!,
                        color = Color(0xFF991B1B),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (authSuccess != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = authSuccess!!,
                        color = Color(0xFF166534),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Input Fields
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ForestGreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ForestGreen) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Remember Me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                    )
                    Text("Remember me", fontSize = 12.sp, color = DeepGreen)
                }

                TextButton(
                    onClick = { showForgotPasswordDialog = true },
                    modifier = Modifier.testTag("forgot_password_button")
                ) {
                    Text("Forgot Password?", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    viewModel.login(email, password) { loggedInUser ->
                        val target = viewModel.consumePostLoginDestination()
                        viewModel.clearAuthPrompt()
                        if (target != null) {
                            viewModel.navigateTo(target)
                        } else {
                            when (loggedInUser.role) {
                                "ADMIN" -> viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                                "FARMER" -> viewModel.navigateTo(Screen.FARMER_DASHBOARD)
                                "RIDER" -> viewModel.navigateTo(Screen.RIDER_DASHBOARD)
                                else -> viewModel.navigateTo(Screen.CLIENT_DASHBOARD)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Log In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google Button
            OutlinedButton(
                onClick = { showGoogleSetupDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("google_login_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepGreen)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = ForestGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Google", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = Color.Gray, fontSize = 13.sp)
                TextButton(
                    onClick = { viewModel.navigateTo(Screen.REGISTER) },
                    modifier = Modifier.testTag("login_to_register_button")
                ) {
                    Text("Create Account", color = FreshGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            TextButton(
                onClick = { viewModel.navigateTo(Screen.PUBLIC_MARKETPLACE) },
                modifier = Modifier.testTag("login_back_to_market_button")
            ) {
                Text("Back to Marketplace", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: TropiVaultViewModel
) {
    var selectedRole by remember { mutableStateOf("CLIENT") } // CLIENT, FARMER, RIDER, ADMIN

    // Common fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Role-specific fields
    var address by remember { mutableStateOf("") }
    var farmName by remember { mutableStateOf("") }
    var farmLocation by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Motorcycle (Cold-Thermal Box)") }
    var licenseNumber by remember { mutableStateOf("") }
    var adminKey by remember { mutableStateOf("TROPI2026") }

    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authSuccess by viewModel.authSuccessMessage.collectAsStateWithLifecycle()

    val vehicleTypes = listOf(
        "Motorcycle (Cold-Thermal Box)",
        "Refrigerated Eco Van",
        "Insulated Cargo Trike"
    )

    Scaffold(
        containerColor = TropiCream,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ForestGreen)
                }
                Text(
                    text = "Create FarmVault Account",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join the Tropical Agri Network",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            )
            Text(
                text = "Select your account type to register",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Selector Chips (Client, Farmer, Rider, Admin)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoleTab("Client / Buyer", selectedRole == "CLIENT", Modifier.weight(1f)) { selectedRole = "CLIENT" }
                    RoleTab("Farmer Partner", selectedRole == "FARMER", Modifier.weight(1f)) { selectedRole = "FARMER" }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoleTab("Rider Partner", selectedRole == "RIDER", Modifier.weight(1f)) { selectedRole = "RIDER" }
                    RoleTab("Administrator", selectedRole == "ADMIN", Modifier.weight(1f)) { selectedRole = "ADMIN" }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (authError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = authError!!, color = Color(0xFF991B1B), fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Form Fields
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(if (selectedRole == "CLIENT") "Full Name or Business Name" else "Full Legal Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            if (selectedRole == "ADMIN") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = adminKey,
                    onValueChange = { adminKey = it },
                    label = { Text("Admin Master Key (e.g. TROPI2026)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            if (selectedRole == "FARMER") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = farmName,
                    onValueChange = { farmName = it },
                    label = { Text("Farm or Orchard Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = farmLocation,
                    onValueChange = { farmLocation = it },
                    label = { Text("Farm Location (Province / Island)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            if (selectedRole == "RIDER") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Home Base / Dispatch Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Select Delivery Vehicle:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepGreen)
                vehicleTypes.forEach { vt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vehicleType = vt }
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = vehicleType == vt, onClick = { vehicleType = vt })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(vt, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("LTO Driver's License Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            if (selectedRole == "CLIENT") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Default Delivery Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Mobile Contact Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (password != confirmPassword) {
                        return@Button
                    }
                    when (selectedRole) {
                        "CLIENT" -> {
                            viewModel.registerClient(fullName, email, phone, password, address) {
                                viewModel.navigateTo(Screen.CLIENT_DASHBOARD)
                            }
                        }
                        "FARMER" -> {
                            viewModel.registerFarmer(fullName, farmName, email, phone, farmLocation, password, autoApprove = true) {
                                viewModel.navigateTo(Screen.FARMER_DASHBOARD)
                            }
                        }
                        "RIDER" -> {
                            viewModel.registerRider(fullName, email, phone, address, vehicleType, licenseNumber, password, autoApprove = true) {
                                viewModel.navigateTo(Screen.RIDER_DASHBOARD)
                            }
                        }
                        "ADMIN" -> {
                            viewModel.registerAdmin(fullName, email, phone, password, adminKey) {
                                viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                            }
                        }
                    }
                },
                enabled = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("register_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
            ) {
                Text(
                    text = when (selectedRole) {
                        "ADMIN" -> "Create Administrator Account"
                        "FARMER" -> "Register Farmer Account"
                        "RIDER" -> "Register Rider Account"
                        else -> "Register Client Account"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already registered?", color = Color.Gray, fontSize = 13.sp)
                TextButton(onClick = { viewModel.navigateTo(Screen.LOGIN) }) {
                    Text("Log In", color = ForestGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RoleTab(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) ForestGreen else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else Color.LightGray),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else DeepGreen
            )
        }
    }
}
