package com.rhix.newsplashactivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddProductDialog extends DialogFragment {

    private EditText editTextProductName, editTextQuantity, editTextDescription;
    private DatabaseHelper myDb;
    private final AddProductListener listener;

    public interface AddProductListener {
        void onProductAdded();
    }

    public AddProductDialog(AddProductListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_layout, null);

        editTextProductName = view.findViewById(R.id.etProductName);
        editTextQuantity = view.findViewById(R.id.etQuantity);
        editTextDescription = view.findViewById(R.id.etDescription);

        myDb = new DatabaseHelper(getActivity());

        // Build dialog with "Cancel" and "Add Product" buttons styled similarly
        builder.setView(view)
                .setTitle("Add Product")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();  // Cancel the dialog
                    }
                })
                .setPositiveButton("Add Product", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Add product logic
                        String productName = editTextProductName.getText().toString();
                        String quantity = editTextQuantity.getText().toString();
                        String description = editTextDescription.getText().toString();

                        if (productName.isEmpty() || quantity.isEmpty() || description.isEmpty()) {
                            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isInserted = myDb.insertData(productName, quantity, description);
                            if (isInserted) {
                                Toast.makeText(getActivity(), "Product added", Toast.LENGTH_SHORT).show();
                                listener.onProductAdded();  // Notify listener (FragmentProducts) that a product was added
                            } else {
                                Toast.makeText(getActivity(), "Error adding product", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        AlertDialog dialog = builder.create();

        // Set the dialog width to fill the entire screen width
        dialog.setOnShowListener(dialogInterface -> {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
        });

        return dialog;
    }
}