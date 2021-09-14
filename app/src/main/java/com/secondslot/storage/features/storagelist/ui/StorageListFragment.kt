package com.secondslot.storage.features.storagelist.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.secondslot.storage.R
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.databinding.FragmentStorageListBinding
import com.secondslot.storage.features.storagelist.adapter.CharacterAdapter
import com.secondslot.storage.features.storagelist.vm.StorageListViewModel
import com.secondslot.storage.features.storagelist.vm.StorageListViewModelFactory

private const val TAG = "StorageListFragment"

class StorageListFragment : Fragment(), CharacterListener {

    private lateinit var viewModel: StorageListViewModel
    private var _binding: FragmentStorageListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val characterAdapter = CharacterAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageListBinding.inflate(inflater, container, false)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val viewModelFactory = StorageListViewModelFactory(prefs)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(StorageListViewModel::class.java)

        initViews()
        setListeners()
        setObservers()
        return binding.root
    }

    private fun initViews() {
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = characterAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        if (binding.fab.isShown) binding.fab.hide()
                    }

                    if (dy < 0) {
                        if (!binding.fab.isShown) binding.fab.show()
                    }
                }
            })

            setHasFixedSize(true)
        }
    }

    private fun setListeners() {
        binding.fab.setOnClickListener { viewModel.onFabClicked() }
    }

    private fun setObservers() {

        viewModel.charactersLiveData.observe(viewLifecycleOwner) { characters ->
            Log.d(TAG, "CharactersLiveData received. Characters.size = ${characters.size}")
            characters?.let { characterAdapter.submitList(characters) }
        }

        viewModel.openAddEntityLiveData.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                val action = StorageListFragmentDirections.toAddEntityFragment(
                    label = getString(R.string.add_entity_toolbar_title)
                )
                findNavController().navigate(action)
                viewModel.onFabClickedComplete()
            }
        }

        viewModel.clearDbSelectedLiveData.observe(viewLifecycleOwner) { isSelected ->
            if (isSelected) {
                showClearDbConfirmDialog()
                viewModel.onClearDbSelectedComplete()
            }
        }

        viewModel.characterDeletedLiveData.observe(viewLifecycleOwner) { deletedCharacter ->
            if (deletedCharacter != null) showCharacterDeletedSnackbar(deletedCharacter)
        }

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.storageListFragment)

        // Create observer and add it to the NavBackStackEntry's lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains("key")
            ) {
                val result = navBackStackEntry.savedStateHandle.get<String>("key")
                if (result == "OK") {
                    viewModel.onClearDbConfirmed()
                    Log.d("myLogs", "Clear DB confirmed")
                    navBackStackEntry.savedStateHandle.remove<String>("key")
                }
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onPrefsUpdated()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_db -> {
                viewModel.onClearDbSelected()
                true
            }
            R.id.add_fake_data -> {
                viewModel.onAddFakeDataSelected()
                true
            }
            else -> item.onNavDestinationSelected(findNavController()) ||
                    super.onOptionsItemSelected(item)
        }
    }

    private fun showClearDbConfirmDialog() {
        val action = StorageListFragmentDirections.toClearDbDialog()
        findNavController().navigate(action)
    }

    // CharacterListener function edit()
    override fun edit(id: Int) {
        val action = StorageListFragmentDirections.toAddEntityFragment(
            id,
            label = getString(R.string.edit_entity_toolbar_title)
        )
        findNavController().navigate(action)
    }

    // CharacterListener function delete()
    override fun delete(character: Character) {
        viewModel.onDeleteButtonClicked(character)
    }

    private fun showCharacterDeletedSnackbar(character: Character) {
        val message = getString(R.string.snackbar_message, character.name)

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                viewModel.onRestoreCharacter(character)
            }
            .show()

        viewModel.onRestoreCharacterCompleted()
    }
}
