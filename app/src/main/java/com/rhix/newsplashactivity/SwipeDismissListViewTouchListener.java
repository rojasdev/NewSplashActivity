package com.rhix.newsplashactivity;

import android.content.Context;
import android.database.Cursor;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeDismissListViewTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private final ListView listView;
    private final DatabaseHelper databaseHelper;
    private final FragmentProducts fragment;

    public SwipeDismissListViewTouchListener(Context context, ListView listView, DatabaseHelper databaseHelper, FragmentProducts fragment) {
        this.listView = listView;
        this.databaseHelper = databaseHelper;
        this.fragment = fragment;
        gestureDetector = new GestureDetector(context, new SwipeGestureDetector());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) { // Swipe left
                    handleSwipeLeft(e1);
                } else if (diffX > 0) { // Swipe right
                    handleSwipeRight(e1);
                }
                return true;
            }
            return false;
        }

        private void handleSwipeLeft(MotionEvent e1) {
            int position = getPositionForTouch(e1);
            if (position != -1) {
                View view = listView.getChildAt(position - listView.getFirstVisiblePosition());
                if (view != null) {
                    final Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                    if (cursor != null) {
                        // Show the delete button
                        Button btnConfirmDelete = view.findViewById(R.id.btnConfirmDelete);
                        btnConfirmDelete.setVisibility(View.VISIBLE);
                        btnConfirmDelete.setOnClickListener(v -> {
                            // Retrieve the product ID from tvProductID
                            TextView tvProductID = view.findViewById(R.id.tvProductID);
                            long id = Long.parseLong(tvProductID.getText().toString());

                            // Remove item from the database
                            databaseHelper.deleteData(id);
                            // Reload list to reflect changes
                            fragment.loadListView();
                            Toast.makeText(fragment.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        }

        private void handleSwipeRight(MotionEvent e1) {
            int position = getPositionForTouch(e1);
            if (position != -1) {
                View view = listView.getChildAt(position - listView.getFirstVisiblePosition());
                if (view != null) {
                    // Hide the delete button
                    Button btnConfirmDelete = view.findViewById(R.id.btnConfirmDelete);
                    btnConfirmDelete.setVisibility(View.GONE);
                }
            }
        }

        private int getPositionForTouch(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int position = listView.pointToPosition(x, y);
            return position;
        }
    }
}
