package com.rhix.newsplashactivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentFood extends DialogFragment implements DialogListener {
    private static final float SWIPE_THRESHOLD = 100f;
    private static final float SWIPE_VELOCITY_THRESHOLD = 100f;
    private LinearLayout slideView;
    private GestureDetector gestureDetector;
    private FrameLayout slideViewContainer;
    private GridViewAdapter adapter;
    private GridView gridView;
    private View view;
    private static final String JSON_URL = "https://devlab.helioho.st/api/api.php";

    // Add an interface to handle item click events from the adapter
    public interface OnFoodItemClickListener {
        void onFoodItemClicked(FoodModel food);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food, container, false);

        // Initialize the GridView and adapter
        gridView = view.findViewById(R.id.gridView);
        adapter = new GridViewAdapter(requireActivity());
        gridView.setAdapter(adapter);

        // Initialize the slide view container and set its visibility to GONE initially
        slideViewContainer = view.findViewById(R.id.slideViewContainer);
        slideViewContainer.setVisibility(View.GONE); // Set visibility to GONE initially

        // Initialize the gesture detector with a custom gesture listener
        gestureDetector = new GestureDetector(requireContext(), new MyGestureListener());

        // Set an item click listener for the GridView adapter
        adapter.setOnItemClickListener(new GridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FoodModel food) {
                // Show a toast message when an item is clicked
                Toast.makeText(requireContext(), "Item Selected: " + food.getFoodName(), Toast.LENGTH_SHORT).show();

                // Trigger the slide view when an item is clicked and pass the selected item data
                showSlideView(food);
            }
        });

        // Initialize the slideView and start connecting to the service to fetch data
        slideView = view.findViewById(R.id.slideView);
        connectToService();

        // Set touch listener for the slide view container to detect gestures
        slideViewContainer.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true; // Return true to indicate that the event has been handled
        });

        return view;
    }

    // Fetch data from the API
    private void connectToService() {
        new FetchDataTask(adapter).execute(JSON_URL);
    }

    // Handle the dialog dismissal (if needed)
    @Override
    public void onDialogDismissed() {
        // Handle any cleanup when the dialog is dismissed
    }

    // Show the slide view with a slide-up animation, and pass the selected FoodModel
    private void showSlideView(FoodModel food) {
        // Populate slideView with the selected food item details (if needed)
        TextView slideViewTitle = slideView.findViewById(R.id.slideViewTitle);
        slideViewTitle.setText(food.getFoodName()); // Example: setting the title
        // Show the semi-transparent background
        View transparentBackground = view.findViewById(R.id.transparentBackground);
        transparentBackground.setVisibility(View.VISIBLE);

        // Prevent touch events on the transparent background
        transparentBackground.setOnTouchListener((v, event) -> true); // Consume all touch events

        // Get the height of the screen to set the slide view's height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Slide-up animation
        TranslateAnimation slideUp = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.5f
        );
        slideUp.setDuration(100);
        slideUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideViewContainer.startAnimation(slideUp);

        // Set the slide view's height to half of the screen height
        slideView.getLayoutParams().height = screenHeight / 2;
        slideView.requestLayout();

        // Make the slide view container visible
        slideViewContainer.setVisibility(View.VISIBLE);

        // Request focus on the slide view to ensure it is focused
        slideViewContainer.requestFocus();
    }

    // Hide the slide view with a slide-down animation
    private void hideSlideView() {
        // Hide the semi-transparent background
        View transparentBackground = view.findViewById(R.id.transparentBackground);
        transparentBackground.setVisibility(View.GONE);

        // Allow touch events on the transparent background again
        transparentBackground.setOnTouchListener(null); // Remove touch listener

        // Slide-down animation
        TranslateAnimation slideDown = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.5f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f
        );
        slideDown.setDuration(100);
        slideDown.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start animation
        slideViewContainer.startAnimation(slideDown);

        // Set visibility to GONE after the animation completes
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                slideViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    // Custom gesture listener to detect swipe gestures
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Detect swipe gesture (up or down)
            if (e1.getY() - e2.getY() > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                // Swipe up detected, show the slide view
                showSlideView(null); // Or use previously clicked item if required
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                // Swipe down detected, hide the slide view
                hideSlideView();
                return true;
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true; // Must return true to allow for fling detection
        }
    }

    // Handle touch events to detect gestures
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
