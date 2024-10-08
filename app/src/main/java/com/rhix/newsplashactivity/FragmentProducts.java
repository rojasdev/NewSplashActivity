package com.rhix.newsplashactivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentProducts extends Fragment {
    private ListView listView;
    private DatabaseHelper myDb;
    private SimpleCursorAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        listView = view.findViewById(R.id.listView);
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);

        myDb = new DatabaseHelper(getActivity());

        // Load list when fragment starts
        loadListView();

        // Open input form dialog when FAB is clicked
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getChildFragmentManager();
                AddProductDialog dialog = new AddProductDialog(new AddProductDialog.AddProductListener() {
                    @Override
                    public void onProductAdded() {
                        loadListView();  // Reload list after product is added
                    }
                });
                dialog.show(fragmentManager, "AddProductDialog");
            }
        });

        // Set up SwipeDismissListViewTouchListener
        listView.setOnTouchListener(new SwipeDismissListViewTouchListener(getContext(), listView, myDb, this));

        return view;
    }

    public void loadListView() {
        Cursor cursor = myDb.getAllData();
        if (cursor != null) {
            String[] from = new String[]{"_id", "PRODUCT_NAME", "QUANTITY", "DESCRIPTION"};
            int[] to = new int[]{R.id.tvProductID, R.id.tvProductName, R.id.tvQuantity, R.id.tvDescription};

            // Use a custom CursorAdapter to include the ID
            adapter = new SimpleCursorAdapter(getActivity(), R.layout.product_list_item, cursor, from, to, 0);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
        }
    }
}
