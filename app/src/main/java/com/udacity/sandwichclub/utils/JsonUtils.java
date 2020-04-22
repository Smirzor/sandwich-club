package com.udacity.sandwichclub.utils;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class JsonUtils {

    /* converts JSON array to string array list */
    private static ArrayList<String> JSONArrayToStringArrayList(JSONArray json) {
        ArrayList<String> stringArrayList = new ArrayList<String>();
        if (json.length() > 0){
            for (int i = 0; i < json.length(); i++) {
                try {
                    stringArrayList.add(json.getString(i));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringArrayList;
    }

    public static Sandwich parseSandwichJson(String json) throws JSONException {

        final String SA_NAMELIST = "name";

        /* url string for the image */
        final String SA_IMAGELINK = "image";

        /* main name and alternative name */
        final String SA_MAINNAME = "mainName";
        final String SA_ALSOKNOWN = "alsoKnownAs";

        /* country of origin, ingredients and description */
        final String SA_ORIGIN = "placeOfOrigin";
        final String SA_INGREDIENTS = "ingredients";
        final String SA_DESCRIPTION = "description";

        final String JSON_MESSAGE_CODE = "code";

        /* creating JSON object from the passed string */
        JSONObject sandwichJSON = new JSONObject(json);

        /* implementing to allow easy switch to possible web version of app */
        if (sandwichJSON.has(JSON_MESSAGE_CODE)){
            int errorCode = sandwichJSON.getInt(JSON_MESSAGE_CODE);

            switch(errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* JSON location wasn't found */
                    return null;
                default:
                    return null;
            }
        }

        /* Example JSON block
        {"name":
            {"mainName":"<placeholder>",
             "alsoKnownAs":["<placeholder>",<placeholder>"]},
         "placeOfOrigin":"<placeholder>",
         "description":"<placeholder>",
         "image":"<imagelink>",
         "ingredients":
             ["<placeholder>","<placeholder>","<placeholder>"]
        */

        /* creating a sandwich object to store the JSON array data in */
        Sandwich selectedSandwich = new Sandwich();

        /* parsing JSON data to sandwich props */
        JSONObject name = sandwichJSON.getJSONObject(SA_NAMELIST);

        selectedSandwich.setMainName(name.getString(SA_MAINNAME));
        selectedSandwich.setPlaceOfOrigin(sandwichJSON.getString(SA_ORIGIN));
        selectedSandwich.setAlsoKnownAs(
                JSONArrayToStringArrayList(name.getJSONArray(SA_ALSOKNOWN)));
        selectedSandwich.setIngredients(
                JSONArrayToStringArrayList(sandwichJSON.getJSONArray(SA_INGREDIENTS)));
        selectedSandwich.setDescription(sandwichJSON.getString(SA_DESCRIPTION));
        selectedSandwich.setImage(sandwichJSON.getString(SA_IMAGELINK));

        return selectedSandwich;
    }
}
