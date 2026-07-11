package com.example.vidaapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vidaapp.model.*
import com.example.vidaapp.ui.*
import com.example.vidaapp.ui.theme.VidaAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VidaAPPTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val authState by mainViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Reação automática ao estado de login
    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Success -> {
                navController.navigate("app") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is UiState.Error -> {
                Toast.makeText(context, (authState as UiState.Error).message, Toast.LENGTH_SHORT).show()
                mainViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onEnterClick = { navController.navigate("login") },
                onAdminClick = { navController.navigate("login_admin") }
            )
        }
        composable("login") {
            LoginScreen(
                isAdminLogin = false,
                onLoginClick = { cpf, pass -> mainViewModel.login(cpf, pass) },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("login_admin") {
            LoginScreen(
                isAdminLogin = true,
                onLoginClick = { cpf, pass -> mainViewModel.login(cpf, pass) },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegistrationScreen(
                isAdminUser = false,
                onSaveMember = { member ->
                    mainViewModel.saveMember(member) {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable("app") {
            ChurchAppContent(mainViewModel) {
                mainViewModel.logout()
                navController.navigate("welcome") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun ChurchAppContent(mainViewModel: MainViewModel, onLogout: () -> Unit) {
    val membersList by mainViewModel.members.collectAsState()
    val contentsList by mainViewModel.contents.collectAsState()
    val prayersList by mainViewModel.prayers.collectAsState()
    val financialEntries by mainViewModel.financialEntries.collectAsState()
    val eventsList by mainViewModel.events.collectAsState()
    val birthdayList by mainViewModel.birthdayMembers.collectAsState()
    
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedCountry by rememberSaveable { mutableStateOf("Brasil") }
    var selectedRegion by rememberSaveable { mutableStateOf("Brasil - Sede") }

    val loggedUser = mainViewModel.loggedUser
    val isUserAdmin = loggedUser?.isAdmin == true

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                val shouldShow = when (destination) {
                    AppDestinations.HOME, AppDestinations.CONTENT, AppDestinations.PRAYERS, AppDestinations.EVENTS, AppDestinations.BIRTHDAYS -> true
                    AppDestinations.REGISTER, AppDestinations.MEMBERS, AppDestinations.FINANCIAL -> isUserAdmin
                }
                if (shouldShow) {
                    item(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = { 
                            if (destination != AppDestinations.REGISTER) mainViewModel.setEditMember(null)
                            currentDestination = destination 
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                val showSelector = isUserAdmin && (
                    currentDestination == AppDestinations.FINANCIAL || 
                    currentDestination == AppDestinations.MEMBERS ||
                    currentDestination == AppDestinations.EVENTS
                )
                if (showSelector) {
                    CountrySelector(
                        selectedCountry = selectedCountry,
                        onCountryChange = { country ->
                            selectedCountry = country
                            selectedRegion = if (country == "Portugal") "Portugal" else "Brasil - Sede"
                        },
                        selectedRegion = selectedRegion,
                        onRegionChange = { selectedRegion = it }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.HOME -> HomeScreen(user = loggedUser, onLogout = onLogout)
                    AppDestinations.CONTENT -> ContentScreen(
                        isAdmin = isUserAdmin, 
                        contents = contentsList, 
                        onSaveContent = { t, d, ty, u -> mainViewModel.saveContent(t, d, ty, u) }, 
                        onDeleteContent = { mainViewModel.deleteContent(it) },
                        mainViewModel = mainViewModel
                    )
                    AppDestinations.PRAYERS -> PrayersScreen(
                        isAdmin = isUserAdmin, 
                        prayers = prayersList, 
                        onSendPrayer = { mainViewModel.sendPrayerRequest(it) }, 
                        onDeletePrayer = { mainViewModel.deletePrayer(it) }
                    )
                    AppDestinations.EVENTS -> EventsScreen(
                        isAdmin = isUserAdmin, 
                        events = eventsList.filter { it.location == selectedRegion || it.location == "Geral" }, 
                        onSaveEvent = { t, d, dt, ti, loc -> mainViewModel.saveEvent(t, d, dt, ti, if(loc == "Geral") selectedRegion else loc) }, 
                        onDeleteEvent = { mainViewModel.deleteEvent(it) }
                    )
                    AppDestinations.BIRTHDAYS -> BirthdayScreen(
                        members = birthdayList.filter { it.location == selectedRegion }
                    )
                    AppDestinations.REGISTER -> RegistrationScreen(
                        memberToEdit = mainViewModel.memberToEdit, 
                        isAdminUser = isUserAdmin, 
                        onSaveMember = { mainViewModel.saveMember(it) { currentDestination = AppDestinations.MEMBERS } }
                    )
                    AppDestinations.MEMBERS -> MembersListScreen(
                        members = membersList.filter { it.location == selectedRegion }, 
                        onEditMember = { mainViewModel.setEditMember(it); currentDestination = AppDestinations.REGISTER }, 
                        onDeleteMember = { mainViewModel.deleteMember(it) }
                    )
                    AppDestinations.FINANCIAL -> FinancialScreen(
                        entries = financialEntries.filter { it.location == selectedRegion },
                        onAddEntry = { d, a, c, e, loc -> mainViewModel.addFinancialEntry(d, a, c, e, loc) },
                        onDeleteEntry = { mainViewModel.deleteFinancialEntry(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelector(
    selectedCountry: String,
    onCountryChange: (String) -> Unit,
    selectedRegion: String,
    onRegionChange: (String) -> Unit
) {
    var countryExpanded by remember { mutableStateOf(false) }
    var regionExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().padding(8.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)), horizontalArrangement = Arrangement.SpaceBetween) {
        ExposedDropdownMenuBox(expanded = countryExpanded, onExpandedChange = { countryExpanded = !countryExpanded }) {
            OutlinedTextField(value = selectedCountry, onValueChange = {}, readOnly = true, label = { Text("País") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).width(140.dp))
            ExposedDropdownMenu(expanded = countryExpanded, onDismissRequest = { countryExpanded = false }) {
                DropdownMenuItem(text = { Text("Brasil") }, onClick = { onCountryChange("Brasil"); countryExpanded = false })
                DropdownMenuItem(text = { Text("Portugal") }, onClick = { onCountryChange("Portugal"); countryExpanded = false })
            }
        }

        ExposedDropdownMenuBox(expanded = regionExpanded, onExpandedChange = { regionExpanded = !regionExpanded }) {
            OutlinedTextField(value = selectedRegion, onValueChange = {}, readOnly = true, label = { Text("Região") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionExpanded) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).width(190.dp))
            ExposedDropdownMenu(expanded = regionExpanded, onDismissRequest = { regionExpanded = false }) {
                val regions = if (selectedCountry == "Brasil") listOf("Brasil - Sede", "Brasil - Regional") else listOf("Portugal")
                regions.forEach { region ->
                    DropdownMenuItem(text = { Text(region) }, onClick = { onRegionChange(region); regionExpanded = false })
                }
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Início", Icons.Default.Home),
    CONTENT("Conteúdo", Icons.Default.PlayCircle),
    PRAYERS("Oração", Icons.Default.Favorite),
    EVENTS("Agenda", Icons.Default.Event),
    BIRTHDAYS("Níver", Icons.Default.Cake),
    REGISTER("Novo", Icons.Default.Person),
    MEMBERS("Membros", Icons.AutoMirrored.Filled.List),
    FINANCIAL("Financeiro", Icons.Default.AttachMoney),
}
