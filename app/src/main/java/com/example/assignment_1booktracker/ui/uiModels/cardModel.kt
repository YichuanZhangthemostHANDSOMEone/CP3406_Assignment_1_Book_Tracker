package com.example.assignment_1booktracker.ui.uiModels

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter

//Define a data model for books used for UI display (for converting model.Book to UI presentation)
data class UIBook(
    val id: Int,
    val imageUrl: String,
    val leftText: String,
    val rightText: String
)


@Composable
fun getImagePainter(imageUrl: String): Painter {
    val context = LocalContext.current
    return if (imageUrl.startsWith("android.resource://")) {
        //Parsing URI format: android.resource://[package]/drawable/[resource_name]
        val uri = Uri.parse(imageUrl)
        val segments = uri.pathSegments
        if (segments.size >= 2) {
            val resName = segments[1]
            //Note: Use the current application package name to obtain the resource ID.
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
            if (resId != 0) {
                painterResource(id = resId)
            } else {
                //If the resource id cannot be found, then use Coil as a fallback option.
                rememberAsyncImagePainter(model = imageUrl)
            }
        } else {
            //The format does not meet the expectations. Please use "Coil" directly.
            rememberAsyncImagePainter(model = imageUrl)
        }
    } else {
        rememberAsyncImagePainter(model = imageUrl)
    }
}
