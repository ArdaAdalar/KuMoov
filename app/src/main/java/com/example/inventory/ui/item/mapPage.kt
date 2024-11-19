package com.example.inventory.ui.item

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

object MapDestination : NavigationDestination {
    override val route = "map"
    override val titleRes = R.string.map_page_title
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapPage(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            // Replace with your desired top app bar content
        }
    ) {
        MapView(modifier = Modifier.fillMaxSize())

        IconButton(
            onClick = { navController.navigateUp() }, // Navigate back to the previous destination
            modifier = Modifier
                .padding(16.dp)

        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(R.string.app_name)
            )
        }
    }
}

@Composable
fun MapView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            onResume()
            getMapAsync { googleMap ->
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.204958, 29.073858), 15f))
                googleMap.addMarker(MarkerOptions().position(LatLng(41.204958, 29.073858)).title("Istanbul"))
            }
            MapsInitializer.initialize(context)
        }
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}
@Preview
@Composable
fun MapPagePreview() {
    MapPage(navController = rememberNavController())
}

