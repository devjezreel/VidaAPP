package com.example.vidaapp

import android.os.Bundle
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
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vidaapp.ui.*
import com.example.vidaapp.ui.theme.VidaAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VidaAPPTheme {
                ChurchApp()
            }
        }
    }
}

@Composable
fun ChurchApp(mainViewModel: MainViewModel = viewModel()) {
    val membersList by mainViewModel.members.collectAsState()
    val contentsList by mainViewModel.contents.collectAsState()
    val prayersList by mainViewModel.prayers.collectAsState()
    val financialEntries by mainViewModel.financialEntries.collectAsState()
    val eventsList by mainViewModel.events.collectAsState()
    val birthdayList by mainViewModel.birthdayMembers.collectAsState()
    
    var screenState by rememberSaveable { mutableStateOf(ScreenState.WELCOME) }
    var isAdminLoginAttempt by rememberSaveable { mutableStateOf(false) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    
    var selectedCountry by rememberSaveable { mutableStateOf("Brasil") }
    var selectedRegion by rememberSaveable { mutableStateOf("Brasil - Sede") }

    val loggedUser = mainViewModel.loggedUser
    val isUserAdmin = loggedUser?.isAdmin == true

    when (screenState) {
        ScreenState.WELCOME -> {
            WelcomeScreen(
                onEnterClick = { isAdminLoginAttempt = false; screenState = ScreenState.LOGIN },
                onAdminClick = { isAdminLoginAttempt = true; screenState = ScreenState.LOGIN }
            )
        }

        ScreenState.LOGIN -> {
            LoginScreen(
                isAdminLogin = isAdminLoginAttempt,
                onLoginClick = { cpf, password ->
                    mainViewModel.login(cpf, password) { success, _ ->
                        if (success) {
                            screenState = ScreenState.MAIN_APP
                            currentDestination = if (mainViewModel.loggedUser?.isAdmin == true) AppDestinations.MEMBERS else AppDestinations.HOME
                        }
                    }
                },
                onRegisterClick = { screenState = ScreenState.REGISTER },
                errorMessage = mainViewModel.loginError
            )
        }

        ScreenState.REGISTER -> {
            RegistrationScreen(
                isAdminUser = false, 
                onSaveMember = { member ->
                    mainViewModel.saveMember(member.copy(isAdmin = false)) { screenState = ScreenState.LOGIN }
                }
            )
        }

        ScreenState.MAIN_APP -> {
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
                            AppDestinations.HOME -> HomeScreen(user = loggedUser, onLogout = { mainViewModel.logout(); screenState = ScreenState.WELCOME })
                            AppDestinations.CONTENT -> ContentScreen(
                                isAdmin = isUserAdmin, 
                                contents = contentsList, 
                                onSaveContent = { t, d, ty, u -> mainViewModel.saveContent(t, d, ty, u) }, 
                                onDeleteContent = { mainViewModel.deleteContent(it) },
                                mainViewModel = mainViewModel
                            )
                            AppDestinations.PRAYERS -> PrayersScreen(isAdmin = isUserAdmin, prayers = prayersList, onSendPrayer = { mainViewModel.sendPrayerRequest(it) }, onDeletePrayer = { mainViewModel.deletePrayer(it) })
                            
                            AppDestinations.EVENTS -> EventsScreen(
                                isAdmin = isUserAdmin, 
                                events = eventsList.filter { it.location == selectedRegion || it.location == "Geral" }, 
                                onSaveEvent = { t, d, dt, ti, loc -> mainViewModel.saveEvent(t, d, dt, ti, selectedRegion) }, 
                                onDeleteEvent = { mainViewModel.deleteEvent(it) }
                            )
                            
                            AppDestinations.BIRTHDAYS -> BirthdayScreen(
                                members = birthdayList.filter { it.location == selectedRegion }
                            )
                            
                            AppDestinations.REGISTER -> RegistrationScreen(memberToEdit = mainViewModel.memberToEdit, isAdminUser = isUserAdmin, onSaveMember = { mainViewModel.saveMember(it) { currentDestination = AppDestinations.MEMBERS } })
                            
                            AppDestinations.MEMBERS -> MembersListScreen(
                                members = membersList.filter { it.location == selectedRegion }, 
                                onEditMember = { mainViewModel.setEditMember(it); currentDestination = AppDestinations.REGISTER }, 
                                onDeleteMember = { mainViewModel.deleteMember(it) }
                            )
                            
                            AppDestinations.FINANCIAL -> FinancialScreen(
                                entries = financialEntries.filter { it.location == selectedRegion },
                                onAddEntry = { d, a, c, e -> mainViewModel.addFinancialEntry(d, a, c, e, selectedRegion) },
                                onDeleteEntry = { mainViewModel.deleteFinancialEntry(it) }
                            )
                        }
                    }
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

enum class ScreenState { WELCOME, LOGIN, REGISTER, MAIN_APP }

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
