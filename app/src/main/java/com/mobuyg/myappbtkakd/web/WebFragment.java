package com.mobuyg.myappbtkakd.web;


import com.mobuyg.myappbtkakd.R;
import com.mobuyg.myappbtkakd.MainActivity;
import com.mobuyg.myappbtkakd.ErrorDialogFragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import im.delight.android.webview.AdvancedWebView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class WebFragment extends Fragment implements MainActivity.Listener, AdvancedWebView.Listener {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1000;
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATIOAN = 2000;
    private static final int REQUEST_PERMISSION_CAMERA_AND_AUDIO = 3000;

    private static final String KEY_CURRENT_URL = "current-url";

    private static final String ARG_WEB_ADDRESS = "web-address";
    private static final String ARG_URL_HANDLING = "url-handling";
    private static final String ARG_HIDE_ACTION_BAR = "hide-action-bar";
    private static final String ARG_GEOLOCATION_ENABLED = "geolocation-enabled";
    private static final String ARG_NOTIFY_PARAM = "notify-param";
    private static final String ARG_DISABLE_SWIPE_REFRESH = "disable-swipe-refresh";

    

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mCustomView;
    private MyWebChromeClient mWebChromeClient;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private String webAddress;
    private String urlHandling;
    private boolean hideActionBar;
    private boolean geolocationEnabled;
    private String notifyParam;
    private boolean disableSwipeRefresh;

    private AdvancedWebView webView;
    private IPermissionCallback writeStorageCallback;
    private PermissionRequest permissionRequest;
    private GeolocationPermissions.Callback geolocationPermissionCallback;
    private String geolocationOrigin;
    private String[] urlKeys = {};

    public WebFragment() {
        // Required empty public constructor
    }

    public static WebFragment newInstance(String webAddress, String urlHandling,
                                          boolean hideActionBar, boolean geolocationEnabled,
                                          boolean disableSwipeRefresh) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEB_ADDRESS, webAddress);
        args.putString(ARG_URL_HANDLING, urlHandling);
        args.putBoolean(ARG_HIDE_ACTION_BAR, hideActionBar);
        args.putBoolean(ARG_GEOLOCATION_ENABLED, geolocationEnabled);
        args.putBoolean(ARG_DISABLE_SWIPE_REFRESH, disableSwipeRefresh);
        fragment.setArguments(args);
        return fragment;
    }


    public static WebFragment newInstance(String webAddress, String urlHandling,
                                          boolean hideActionBar, boolean geolocationEnabled,
                                          boolean disableSwipeRefresh, String notifyParam) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEB_ADDRESS, webAddress);
        args.putString(ARG_URL_HANDLING, urlHandling);
        args.putBoolean(ARG_HIDE_ACTION_BAR, hideActionBar);
        args.putBoolean(ARG_GEOLOCATION_ENABLED, geolocationEnabled);
        args.putBoolean(ARG_DISABLE_SWIPE_REFRESH, disableSwipeRefresh);
        args.putString(ARG_NOTIFY_PARAM, notifyParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            webAddress = getArguments().getString(ARG_WEB_ADDRESS);
            hideActionBar = getArguments().getBoolean(ARG_HIDE_ACTION_BAR);
            geolocationEnabled = getArguments().getBoolean(ARG_GEOLOCATION_ENABLED);

            urlHandling = getArguments().getString(ARG_URL_HANDLING);
            if (urlHandling != null && !urlHandling.isEmpty())
                urlKeys = urlHandling.split(",");

            disableSwipeRefresh = getArguments().getBoolean(ARG_DISABLE_SWIPE_REFRESH);
            notifyParam = getArguments().getString(ARG_NOTIFY_PARAM);
        }

        

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(!disableSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String url = webView.getUrl();
            if (url != null && !url.isEmpty()) webView.loadUrl(url); else webView.loadUrl(webAddress);
        });


        customViewContainer = view.findViewById(R.id.customViewContainer);

        webView = view.findViewById(R.id.webView);
        webView.setListener(getActivity(), this);
        webView.setGeolocationEnabled(!geolocationEnabled);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return urlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return urlLoading(view, request.getUrl().toString());
            }

            private boolean urlLoading(WebView view, String url) {
                for(String key : urlKeys)
                    if (url.contains(key)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                new AlertDialog.Builder(getContext())
                        .setMessage(getString(R.string.ssl_not_verified))
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> handler.cancel())
                        .setPositiveButton(getString(R.string.yes), (dialog, which)-> handler.proceed())
                        .create().show();
            }
        });

        mWebChromeClient = new MyWebChromeClient();
        webView.setWebChromeClient(mWebChromeClient);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else if (notifyParam != null)
            webView.loadUrl(notifyParam);
        else
            webView.loadUrl(webAddress);

        return view;
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onBackPressed() {
        if (inCustomView()) {
            hideCustomView();
            return true;
        }

        if ((mCustomView == null)  && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return false;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (inCustomView()) hideCustomView();

        if (hideActionBar) ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        webView.evaluateJavascript("if(window.localStream){window.localStream.stop();}", null);
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();

        webView.onResume();
        if (hideActionBar) ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        //webView.onPause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        webView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)
                    if (writeStorageCallback != null)
                        writeStorageCallback.onPermissionGranted();
                break;
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATIOAN:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)
                    if (geolocationPermissionCallback != null)
                        geolocationPermissionCallback.invoke(geolocationOrigin, true, true);
                break;
            case REQUEST_PERMISSION_CAMERA_AND_AUDIO:
                if (grantResults.length > 1 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED)
                    permissionRequest.grant(permissionRequest.getResources());
                else if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)
                    permissionRequest.grant(permissionRequest.getResources());
                else
                    permissionRequest.deny();
                break;
            default: super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        writeStorageCallback = () -> AdvancedWebView.handleDownload(getActivity(), url, suggestedFilename);

        if (ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            writeStorageCallback.onPermissionGranted();
        } else if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(getView(), R.string.write_storage_permission_rationale, BaseTransientBottomBar.LENGTH_LONG)
                    .setAction(android.R.string.ok, (v) -> requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE))
                    .show();
        } else {
            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        swipeRefreshLayout.setRefreshing(true);

        
    }

    @Override
    public void onPageFinished(String url) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        swipeRefreshLayout.setRefreshing(false);
        webView.loadUrl("about:blank");
        
        ErrorDialogFragment errorFragment = ErrorDialogFragment.newInstance(description, () -> webView.loadUrl(failingUrl));
        ErrorDialogFragment.show(getChildFragmentManager(), errorFragment);
    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    interface IPermissionCallback {
        void onPermissionGranted();
    }

    class MyWebChromeClient extends WebChromeClient {
        private View mVideoProgressView;

        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            if (super.getDefaultVideoPoster() == null) {
                return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage);
            } else {
                return super.getDefaultVideoPoster();
            }
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            swipeRefreshLayout.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {

            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            geolocationPermissionCallback = callback;
            geolocationOrigin = origin;

            if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
                callback.invoke(origin, true, true);
            } else if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                Snackbar.make(getView(), R.string.fine_location_permission_rationale, BaseTransientBottomBar.LENGTH_LONG)
                        .setAction(android.R.string.ok, (v) -> requestPermissions(new String[] {ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATIOAN))
                        .show();
            } else {
                requestPermissions(new String[] {ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATIOAN);
            }
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            permissionRequest = request;

            if (permissionRequest.getOrigin().toString().startsWith(webAddress)) {
                ArrayList<String> list = new ArrayList<>();

                if (ContextCompat.checkSelfPermission(getActivity(), RECORD_AUDIO) != PERMISSION_GRANTED)
                    list.add(RECORD_AUDIO);

                if (ContextCompat.checkSelfPermission(getActivity(), CAMERA) != PERMISSION_GRANTED)
                    list.add(CAMERA);

                if (list.size() == 0) {
                    request.grant(request.getResources());
                    return;
                }

                requestPermissions(list.toArray(new String[list.size()]), REQUEST_PERMISSION_CAMERA_AND_AUDIO);
            } else
                request.deny();
        }

    }

}
