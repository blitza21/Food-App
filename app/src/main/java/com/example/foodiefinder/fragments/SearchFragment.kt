package com.example.foodiefinder.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefinder.R
import com.example.foodiefinder.activities.AllContentsActivity
import com.example.foodiefinder.activities.AllMealsActivity
import com.example.foodiefinder.adapters.CategoryAdapter
import com.example.foodiefinder.adapters.CountryAdapter
import com.example.foodiefinder.adapters.IngredientAdapter
import com.example.foodiefinder.network.RetrofitHelper
import com.example.foodiefinder.pojo.Category
import com.example.foodiefinder.pojo.Country
import com.example.foodiefinder.pojo.Ingredient
import com.example.foodiefinder.viewmodels.SearchFactory
import com.example.foodiefinder.viewmodels.SearchViewModel


class SearchFragment : Fragment() {
    private lateinit var searchBar : SearchView
    private lateinit var tvViewAllIngredients : TextView
    private lateinit var tvViewAllCategories : TextView
    private lateinit var tvViewAllCountries : TextView
    private lateinit var rvCategories : RecyclerView
    private lateinit var rvCountries : RecyclerView
    private lateinit var rvIngredients : RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var allIngredients : List<Ingredient>
    private lateinit var allCategories : List<Category>
    private lateinit var allCountries : List<Country>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
        prepareRecyclerViews()
        setupViewModel()
        setupObservers()

        handleSearchBar()
        handleViewAllIngredients()
        handleViewAllCategories()
        handleViewAllCountries()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("Search fragment" , "onAttach")
    }

    private fun initUI(view : View){
        searchBar = view.findViewById(R.id.search_bar)
        tvViewAllIngredients = view.findViewById(R.id.tvViewAllIngredients)
        tvViewAllCategories = view.findViewById(R.id.tvViewAllCategories)
        tvViewAllCountries = view.findViewById(R.id.tvViewAllCountries)
        rvCategories = view.findViewById(R.id.rvCategories)
        rvIngredients = view.findViewById(R.id.rvIngredients)
        rvCountries = view.findViewById(R.id.rvCountries)
    }
    private fun prepareRecyclerViews(){
        categoryAdapter = CategoryAdapter(listOf() , R.layout.item_horizontal_small , requireActivity())
        rvCategories.adapter = categoryAdapter
        rvCategories.layoutManager = LinearLayoutManager(requireActivity() , RecyclerView.HORIZONTAL , false)

        ingredientAdapter = IngredientAdapter(listOf() , R.layout.item_horizontal_small , requireActivity())
        rvIngredients.adapter = ingredientAdapter
        rvIngredients.layoutManager = LinearLayoutManager(requireActivity() , RecyclerView.HORIZONTAL , false)

        countryAdapter = CountryAdapter(listOf() , R.layout.item_horizontal_small , requireActivity())
        rvCountries.adapter = countryAdapter
        rvCountries.layoutManager = LinearLayoutManager(requireActivity() , RecyclerView.HORIZONTAL , false)

    }

    private fun setupViewModel(){
        val retrofitService = RetrofitHelper.retrofitService
        val factory = SearchFactory(retrofitService)
        searchViewModel = ViewModelProvider(this , factory).get(SearchViewModel::class.java)
    }

    private fun setupObservers(){
        val ingredientObserver = Observer<List<Ingredient>>{ingredients ->
            allIngredients = ingredients
            ingredientAdapter.ingredients = ingredients.subList(0,10)
            ingredientAdapter.notifyDataSetChanged()
        }
        searchViewModel.ingredients.observe(viewLifecycleOwner , ingredientObserver)

        val categoryObserver = Observer<List<Category>>{categories ->
            allCategories = categories
            categoryAdapter.categories = categories.subList(0,10)
            categoryAdapter.notifyDataSetChanged()
        }
        searchViewModel.categories.observe(viewLifecycleOwner , categoryObserver)

        val countryObserver = Observer<List<Country>>{ countries ->
            allCountries = countries.subList(0,countries.size - 2)  + listOf(countries.last())
            countryAdapter.countries = countries.subList(0,10)
            countryAdapter.notifyDataSetChanged()
        }
        searchViewModel.countries.observe(viewLifecycleOwner , countryObserver)
    }

    private fun handleViewAllIngredients(){
        tvViewAllIngredients.setOnClickListener{
            val intent = Intent(requireActivity() , AllContentsActivity::class.java)
            intent.putExtra("INGREDIENTS" , allIngredients.toTypedArray())
            intent.putExtra("CHOICE" , 1)
            startActivity(intent)
        }
    }

    private fun handleViewAllCategories(){
        tvViewAllCategories.setOnClickListener{
            val intent = Intent(requireActivity() , AllContentsActivity::class.java)
            intent.putExtra("CATEGORIES" , allCategories.toTypedArray())
            intent.putExtra("CHOICE" , 2)
            startActivity(intent)
        }
    }

    private fun handleViewAllCountries(){
        tvViewAllCountries.setOnClickListener{
            val intent = Intent(requireActivity() , AllContentsActivity::class.java)
            intent.putExtra("COUNTRIES" , allCountries.toTypedArray())
            intent.putExtra("CHOICE" , 3)
            startActivity(intent)
        }
    }

    private fun handleSearchBar(){
        searchBar.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(requireActivity() , AllMealsActivity::class.java)
                intent.putExtra("NAME" , query)
                intent.putExtra("FILTER", 'n')
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

}