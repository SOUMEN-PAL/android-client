package com.mifos.feature.center.center_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetCenterDetailsUseCase
import com.mifos.core.objects.group.CenterInfo
import com.mifos.feature.center.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CenterDetailsViewModel @Inject constructor(
    private val getCenterDetailsUseCase: GetCenterDetailsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val centerId = savedStateHandle.getStateFlow(key = Constants.CENTER_ID, initialValue = 0)

    private val _centerDetailsUiState =
        MutableStateFlow<CenterDetailsUiState>(CenterDetailsUiState.Loading)
    val centerDetailsUiState = _centerDetailsUiState.asStateFlow()

    fun loadClientDetails(centerId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getCenterDetailsUseCase(centerId, false).collect { result ->
            when (result) {
                is Resource.Error -> _centerDetailsUiState.value =
                    CenterDetailsUiState.Error(R.string.feature_center_error_loading_centers)

                is Resource.Loading -> _centerDetailsUiState.value = CenterDetailsUiState.Loading

                is Resource.Success -> {
                    result.data?.let {
                        _centerDetailsUiState.value = CenterDetailsUiState.CenterDetails(
                            it.first,
                            if (it.second.isNotEmpty()) it.second[0] else CenterInfo()
                        )
                    }
                }
            }
        }
    }
}