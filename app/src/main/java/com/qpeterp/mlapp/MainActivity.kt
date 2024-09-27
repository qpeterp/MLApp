package com.qpeterp.mlapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qpeterp.mlapp.ui.action.ActionScreen
import com.qpeterp.mlapp.ui.action.ActionViewModel
import com.qpeterp.mlapp.ui.action.ActionViewModelFactory
import com.qpeterp.mlapp.ui.etc.EtcScreen
import com.qpeterp.mlapp.ui.home.HomeScreen
import com.qpeterp.mlapp.ui.theme.MLAppTheme

class MainActivity : ComponentActivity() {
    // 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 허용된 경우 카메라를 사용할 수 있음
        } else {
            // 권한이 거부된 경우 다이얼로그 표시
            showDialog(this)
        }
    }
    private lateinit var actionViewModel: ActionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionViewModel = ViewModelProvider(this, ActionViewModelFactory()).get(ActionViewModel::class.java)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            MLAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MyBottomNavigation(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            indicatorColor = Color.Yellow,
                            navController = navController
                        )
                    },
                    containerColor = Color.Black
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        NavigationHost(
                            navController = navController
                        )
                    }
                }
            }
        }
        checkPermissions()
    }

    // 권한 체크
    private fun checkPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
    }

    // 권한 요청
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 이미 권한이 부여된 경우
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // 권한 요청 이유 설명
                showDialog(this)
            }

            else -> {
                // 권한 요청
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("권한 요청")
            setMessage("앱을 사용하기 위해 카메라 권한이 필요합니다.")
            setPositiveButton("확인") { _, _ -> requestPermissionLauncher.launch(Manifest.permission.CAMERA) }
        }.create().show()
    }

    @Composable
    private fun MyBottomNavigation(
        modifier: Modifier = Modifier,
        containerColor: Color,
        contentColor: Color,
        indicatorColor: Color,
        navController: NavHostController
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val items = listOf(
            BottomNavItem.Action,
            BottomNavItem.Home,
            BottomNavItem.Etc
        )

        AnimatedVisibility(
            visible = items.map { it.route }.contains(currentRoute)
        ) {
            NavigationBar(
                modifier = modifier,
                contentColor = contentColor,
                containerColor = containerColor
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        label = {
                            Text(
                                text = item.label,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                )
                            )
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label
                            )
                        },
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let {
                                    popUpTo(it) { saveState = true }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = indicatorColor,
                            selectedTextColor = indicatorColor,
                            unselectedIconColor = contentColor,
                            unselectedTextColor = contentColor,
                            indicatorColor = containerColor // 선택된 항목의 배경색
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun NavigationHost(navController: NavController) {
        NavHost(navController as NavHostController,
            startDestination = BottomNavItem.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            composable(BottomNavItem.Action.route) { ActionScreen() }
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Etc.route) { EtcScreen() }
        }
    }

    sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
        data object Action : BottomNavItem(
            "action",
            Icons.Outlined.Add,
            "운동횟수"
        )

        data object Home : BottomNavItem(
            "home",
            Icons.Outlined.Home,
            "홈"
        )

        data object Etc : BottomNavItem(
            "etc",
            Icons.Outlined.Menu,
            "그 외"
        )
    }
}