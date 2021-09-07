package com.secondslot.storage.features.addentity.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.secondslot.storage.R
import com.secondslot.storage.data.repository.model.Character
import com.secondslot.storage.databinding.FragmentAddEntityBinding
import com.secondslot.storage.features.addentity.vm.AddEntityViewModel
import com.secondslot.storage.features.addentity.vm.AddEntityViewModelFactory

class AddEntityFragment : Fragment() {

    private lateinit var viewModel: AddEntityViewModel
    private var _binding: FragmentAddEntityBinding? = null
    private val binding get() = requireNotNull(_binding)
//    private var characterId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEntityBinding.inflate(inflater, container, false)
        initViews()
        setListeners()
        setObservers()
        return binding.root
    }

    private fun initViews() {

        val args: AddEntityFragmentArgs by navArgs()
        val characterId = args.id

        val viewModelFactory = AddEntityViewModelFactory(characterId)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(AddEntityViewModel::class.java)

        // Depending on existence of args adjust add or edit version of fragment
        if (characterId == -1) {
            requireActivity().actionBar?.title = getString(R.string.add_entity_toolbar_title)
        } else {
            requireActivity().actionBar?.title = getString(R.string.edit_entity_toolbar_title)
            binding.addButton.text = getString(R.string.save_button)
        }
    }

    private fun setListeners() {

        binding.addButton.setOnClickListener {
            if (viewModel.characterId == -1) {
                val character = Character(
                    name = binding.nameTextInput.text.toString(),
                    location = binding.locationTextInput.text.toString(),
                    quote = binding.quoteTextInput.text.toString()
                )
                viewModel.onAddButtonClicked(character)
            } else {
                val character = Character(
                    id = viewModel.characterId,
                    name = binding.nameTextInput.text.toString(),
                    location = binding.locationTextInput.text.toString(),
                    quote = binding.quoteTextInput.text.toString()
                )
                viewModel.onEditButtonClicked(character)
            }
        }
    }

    private fun setObservers() {

        viewModel.getCharacterLiveData.observe(viewLifecycleOwner) { character ->
            binding.run {
                nameTextInput.setText(character.name)
                locationTextInput.setText(character.location)
                quoteTextInput.setText(character.quote)
            }
        }

        viewModel.characterAddedLiveData.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                viewModel.onAddButtonComplete()
                findNavController().popBackStack()
            }
        }
    }
}