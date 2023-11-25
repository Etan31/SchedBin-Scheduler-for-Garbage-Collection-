package com.example.a1project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

// DeleteDialogFragment.java
public class DeleteDialogFragment extends DialogFragment {

    private DeleteDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DeleteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog, null);

        builder.setView(view);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle delete button click
                RadioGroup radioGroup = view.findViewById(R.id.radio_group);
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                if (selectedRadioButtonId != -1) {
                    RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);

                    boolean deleteThisEvent = selectedRadioButton.getId() == R.id.radio_this_event;
                    boolean deleteThisAndFollowingEvents = selectedRadioButton.getId() == R.id.radio_this_and_following_events;

                    listener.onDeleteConfirmed(deleteThisEvent, deleteThisAndFollowingEvents);
                } else {
                    // No radio button selected
                    // Handle accordingly (show an error, etc.)
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancel button click
                listener.onDeleteCancelled();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Handle cancel when clicking outside the dialog or pressing back
                listener.onDeleteCancelled();
            }
        });

        return builder.create();
    }


    public interface DeleteDialogListener {
        void onDeleteConfirmed(boolean deleteThisEvent, boolean deleteThisAndFollowingEvents);
        void onDeleteCancelled();
    }
}
