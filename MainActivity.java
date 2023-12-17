/*
 * Copyright (C) 2019-2023 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.search;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.here.sdk.core.engine.SDKBuildInformation;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText editTextSearch;

    private static final String TAG = MainActivity.class.getSimpleName();

    private PermissionsRequestor permissionsRequestor;
    private MapView mapView;
    private SearchExample searchExample;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // THIS PART OF CODE RUNS APP IN FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
/*
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        Editable editable = autoCompleteTextView.getText();

        // Convert the Editable to a String
        String hintText = editable.toString();

        // Sample data for autocomplete suggestions (replace this with data from your backend)
        String[] suggestions = {hintText};
        String newhint = searchExample.autoSuggestExample(hintText);

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(suggestions));
        arrayList.add(newhint);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                //TODO : MIGHT HAVE TO CONVERT BACK TO STRING
                android.R.layout.simple_dropdown_item_1line, arrayList.toArray(new String[0]));


        // Set the adapter to the AutoCompleteTextView

        autoCompleteTextView.setAdapter(adapter);

        // Set an item click listener for handling clicks on suggestions
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            //showToast("Selected: " + selectedItem);
            // Add your logic for handling the selected item, e.g., initiating a search
            performSearch(selectedItem);
        });

*/
        // Usually, you need to initialize the HERE SDK only once during the lifetime of an application.
        initializeHERESDK();

        setContentView(R.layout.activity_main);

        Log.d("", "HERE SDK version: " + SDKBuildInformation.sdkVersion().versionName);

        // Get a MapView instance from layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        Button buttonSearch = findViewById(R.id.searchButton);
        // Initialize views
        editTextSearch = findViewById(R.id.editTextSearch);


        //AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);




        // Set a click listener for the search button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClicked(v);
            }
        });

        handleAndroidPermissions();
    }
    // Method to be called when the search button is clicked
    public void onSearchClicked(View view) {
        // Get the text as an Editable
        String searchQuery = editTextSearch.getText().toString().trim();

        performSearch(searchQuery);
    }
    private void performSearch(String editTextSearch) {
        // Get the user input from the EditText
        String searchQuery = editTextSearch.trim();

        // Check if the search query is not empty
        if (!searchQuery.isEmpty()) {
            // TODO: Implement your search logic here
            searchExample.onSearchButtonClicked(searchQuery);

            // For now, just display a toast with the search query
            Toast.makeText(this, "Searching for: " + searchQuery, Toast.LENGTH_SHORT).show();
        } else {
            // If the search query is empty, show a message to the user
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        }
    }
    private void initializeHERESDK() {
        // Set your credentials for the HERE SDK.
        //String accessKeyID = "YOUR_ACCESS_KEY_ID";
        //String accessKeySecret = "YOUR_ACCESS_KEY_SECRET";
        String accessKeyID = "fhfaxFuPUdhphpTDhn1ssg";
        String accessKeySecret = "LbRVA_5H9tbXTFR7MVW3Dj5KYj3RZcbB-etsCS2iiZmBsNuMKXiIbkAN4LE0WFF2KgN-DiQ8k3tIuS5cqiaQVw";
        SDKOptions options = new SDKOptions(accessKeyID, accessKeySecret);
        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    searchExample = new SearchExample(MainActivity.this, mapView);
                } else {
                    Log.d(TAG, "onLoadScene failed: " + mapError);
                }
            }
        });
    }

    //public void searchExampleButtonClicked(View view) {
     //   searchExample.onSearchButtonClicked();
    //}

    public void geocodeAnAddressButtonClicked(View view) {
        searchExample.onGeocodeButtonClicked();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void disposeHERESDK() {
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            // For safety reasons, we explicitly set the shared instance to null to avoid situations,
            // where a disposed instance is accidentally reused.
            SDKNativeEngine.setSharedInstance(null);
        }
    }
}
