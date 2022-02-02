package com.mobuyg.myappbtkakd;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ErrorDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "message";
    private static final String TAG_ERROR_FRAGMENT = "error-fragment";

    private String message;
    private Listener listener;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    public static ErrorDialogFragment newInstance(String message, Listener listener) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.listener = listener;

        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        ImageView imageView = view.findViewById(R.id.imageView);
        Button btClose = view.findViewById(R.id.btClose);
        Button btRetry = view.findViewById(R.id.btRetry);

        if (isNetworkAvailable(getContext()))
            txtDescription.setText(message);
        else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.nointernet));
            txtTitle.setText(R.string.title_no_internet);
            txtDescription.setText(R.string.check_your_connection);
        }

        btClose.setOnClickListener((v) -> dismiss());

        btRetry.setOnClickListener((v) -> {
            dismiss();
            if (listener != null) listener.onRetryClicked();
        });


    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static public void show(FragmentManager manager, DialogFragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag(TAG_ERROR_FRAGMENT);
        if (prev != null) transaction.remove(prev);
        transaction.addToBackStack(null);

        fragment.show(transaction, TAG_ERROR_FRAGMENT);
    }

    public interface Listener {
        void onRetryClicked();
    }
}
