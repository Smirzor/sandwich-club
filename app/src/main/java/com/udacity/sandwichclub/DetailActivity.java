package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import org.json.JSONException;

import java.util.List;
// TODO loading marker
// TODO gradle clean
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    // declaring all TextViews
    private TextView dOriginTextView;
    private TextView dAlsoKnownTextView;
    private TextView dIngredientsTextView;
    private TextView dDescriptionTextView;
    private ImageView ingredientsIv;

    // clears all the placeholder strings used for preview purpose in the designer
    private void removePlaceHolderStrings(){
        dOriginTextView.setText("");
        dAlsoKnownTextView.setText("");
        dIngredientsTextView.setText("");
        dDescriptionTextView.setText("");
    }

    // finds all needed content Ids
    private void getViewIds(){
        ingredientsIv = (ImageView) findViewById(R.id.image_iv);

        dOriginTextView = (TextView) findViewById(R.id.origin_tv);
        dAlsoKnownTextView = (TextView) findViewById(R.id.also_known_tv);
        dIngredientsTextView = (TextView) findViewById(R.id.ingredients_tv);
        dDescriptionTextView = (TextView) findViewById(R.id.description_tv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getViewIds();
        removePlaceHolderStrings();

        populateUI();
    }

    /* close activity with toast message*/
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    /* fill UI using an AsyncTask */
    private void populateUI() {
        new FetchSandwichDataTask().execute(getIntent());
    }

    /* create one string from string list */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String textListToTextViewString (List<String> texts) {
        String text = "";
        if (texts.size() > 0){
            text = String.join(", ", texts);
        }
        return text;
    }

    /* change empty strings to "n/a" */
    private String checkIfEmpty(String text){
        if (text == null || text.isEmpty()){
            return "n/a";
        }
        return text;
    }

    /* populate all views with sandwich data */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillViews(Sandwich sandwich){
        setTitle(checkIfEmpty(sandwich.getMainName()));

        dOriginTextView.setText(checkIfEmpty(sandwich.getPlaceOfOrigin()));
        dAlsoKnownTextView.append(checkIfEmpty(
                textListToTextViewString(sandwich.getAlsoKnownAs())));
        dIngredientsTextView.setText(checkIfEmpty(
                textListToTextViewString(sandwich.getIngredients())));
        dDescriptionTextView.setText(checkIfEmpty(sandwich.getDescription()));

        ingredientsIv.setTooltipText(sandwich.getImage());
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(ingredientsIv);
    }

    /* async task where JSON data is loaded */
    public class FetchSandwichDataTask extends AsyncTask<Intent, Void, Sandwich> {
        @Override
        protected Sandwich doInBackground(Intent... intents) {
            intents[0] = getIntent();
            Intent intent = intents[0];
            Sandwich sandwich = null;

            if (intent == null) {
                closeOnError();
                return null;
            }
            else {
                int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
                if (position == DEFAULT_POSITION) {
                    // EXTRA_POSITION not found in intent
                    closeOnError();
                    return null;
                }

                // get the correct sandwich JSON entry
                String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
                String json = sandwiches[position];

                // try to retrieve data from the JSON string
                try{
                    sandwich = JsonUtils.parseSandwichJson(json);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                if (sandwich == null) {
                    // Sandwich data unavailable
                    closeOnError();
                    return null;
                }
            }
            return sandwich;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Sandwich sandwich) {
            if (sandwich != null) {
                // if the sandwich object isn't empty populate the views
                fillViews(sandwich);
            }
            else{
                closeOnError();
            }
        }
    }
}
