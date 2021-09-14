package com.secondslot.storage.features.addentity.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
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

        if (characterId != -1) binding.addButton.text = getString(R.string.save_button)
    }

    private fun setListeners() {

        binding.nameTextInput.doOnTextChanged { _, _, _, _ ->
            validateName()
        }

        binding.addButton.setOnClickListener {
            if (!isValidate()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.incorrect_input_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (viewModel.characterId == -1) {
                val character = Character(
                    name = binding.nameTextInput.text.toString().trim(),
                    location = binding.locationTextInput.text.toString().trim(),
                    quote = binding.quoteTextInput.text.toString().trim()
                )
                viewModel.onAddButtonClicked(character)
            } else {
                val character = Character(
                    id = viewModel.characterId,
                    name = binding.nameTextInput.text.toString().trim(),
                    location = binding.locationTextInput.text.toString().trim(),
                    quote = binding.quoteTextInput.text.toString().trim()
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

    private fun isValidate(): Boolean = validateName()

    private fun validateName(): Boolean {
        if (binding.nameTextInput.text.toString().trim().isEmpty()) {
            binding.nameTextInputLayout.error = getString(R.string.empty_field_error)
            binding.nameTextInput.requestFocus()
            return false
        } else {
            binding.nameTextInputLayout.error = null
        }
        return true
    }
}
