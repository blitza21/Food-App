package com.example.foodapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.db.MealDao
import com.example.foodapp.pojo.Meal
import com.example.foodapp.network.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MealViewModel(private val retrofitService: RetrofitService, private val mealDao: MealDao) :
    ViewModel() {
    private val _randomMeal: MutableLiveData<Meal> = MutableLiveData()
    val randomMeal: LiveData<Meal> = _randomMeal

    private val _meal: MutableLiveData<Meal> = MutableLiveData()
    val meal: LiveData<Meal> = _meal

    private val _msg: MutableLiveData<String> = MutableLiveData()
    val msg: LiveData<String> = _msg

    private val _isFav: MutableLiveData<Boolean> = MutableLiveData()
    val isFav: LiveData<Boolean> = _isFav

    fun getRandomMeal() {
        viewModelScope.launch(Dispatchers.IO) {
            val randomMealResponse = retrofitService.getRandomMeal().body()
            val myMeal = randomMealResponse?.meals?.get(0)
            withContext(Dispatchers.Main) {
                if (myMeal != null)
                    _randomMeal.postValue(myMeal!!)
            }
        }
    }

    fun getMealByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mealResponse = retrofitService.filterByName(name).body()
            val myMeal = mealResponse?.meals?.get(0)
            withContext(Dispatchers.Main) {
                if (myMeal != null) {
                    _meal.postValue(myMeal!!)
                }
            }
        }
    }

    fun handleFav(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mealDao.addFav(meal)
            withContext(Dispatchers.Main) {
                if (result > 0) {
                    _msg.postValue("Added to favourites")
                } else {
                    withContext(Dispatchers.IO) {
                        mealDao.deleteFav(meal)
                    }
                    _msg.postValue("Removed from favourites")
                }
                isInFav(meal.id)
            }
        }
    }

    fun isInFav(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mealDao.isFav(id)
            withContext(Dispatchers.Main) {
                _isFav.postValue(result)
            }
        }
    }
}

class MealFactory(private val retrofitService: RetrofitService, private val mealDao: MealDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            return MealViewModel(retrofitService, mealDao) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}