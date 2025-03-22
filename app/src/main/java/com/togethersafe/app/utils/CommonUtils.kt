package com.togethersafe.app.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@Composable
fun getActivity() = LocalActivity.current as ComponentActivity

@Composable
inline fun <reified VM: ViewModel> getViewModel() = hiltViewModel<VM>(getActivity())
