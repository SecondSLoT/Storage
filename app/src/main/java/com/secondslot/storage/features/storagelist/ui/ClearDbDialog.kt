package com.secondslot.storage.features.storagelist.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.secondslot.storage.R

class ClearDbDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.clear_db_dialog_title))
            .setMessage(getString(R.string.clear_db_dialog_message))
            .setPositiveButton(getString(R.string.clear_db_dialog_positive_button)) { _, _ ->
                findNavController().previousBackStackEntry?.savedStateHandle?.set("key", "OK")
            }
            .setNegativeButton(getString(R.string.clear_db_dialog_negative_button)) { dialog, _ ->
                dialog.cancel()
            }
            .create()
}
