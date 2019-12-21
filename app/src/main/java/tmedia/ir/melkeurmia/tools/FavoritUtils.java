/*
 * Copyright 2014 KC Ochibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  The "‚‗‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK unicode 201A
 *  and unicode 2017 that are used for separating the items in a list.
 */

package tmedia.ir.melkeurmia.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import tmedia.ir.melkeurmia.model.Favorit;

//import com.google.gson.Gson;


public class FavoritUtils {
    private Context context;
    JsonArray new_favorites = new JsonArray();

    public FavoritUtils(Context appContext) {
        context = appContext;

        checkFileExist();
    }

    public boolean isFavoritExist(Favorit favorit) {
        String data = readFile();
        if(data.equals("")){
            return  false;
        }else{

            Gson gson = new Gson();
            JsonArray favorites = gson.fromJson(data, JsonArray.class);


            for (int i = 0; i < favorites.size(); i++) {
                JsonObject object = (JsonObject) favorites.get(i);
                if (object.get("path").getAsString().equals(favorit.getPath())) {
                    return true;
                }
            }
            return false;
        }
    }


    public void removeFavorit(Favorit favorit) {
        String data = readFile();
        Gson gson = new Gson();
        JsonArray favorites = gson.fromJson(data, JsonArray.class);
        JsonArray temp = new JsonArray();
        for (int i = 0; i < favorites.size(); i++) {
            JsonObject object = (JsonObject) favorites.get(i);
            if (!object.get("path").getAsString().equals(favorit.getPath())) {
                temp.add(object);
            }
        }

       writeFile(gson.toJson(temp));

    }

    private void removeItem(int index, JsonArray favorites) {
        for (int i = 0; i < favorites.size(); i++) {
            if (i != index) {
                new_favorites.add(favorites.get(i).getAsJsonObject());
            }
        }
    }

    public void putFavorit(Favorit favorit) {
        String data = readFile();
        Gson gson = new Gson();
        JsonArray favorites;
        if(data.equals("")){
            favorites = new JsonArray();
        }else{
            favorites = gson.fromJson(data, JsonArray.class);
        }
        JsonObject object = new JsonObject();
        object.addProperty("name", favorit.getName());
        object.addProperty("path", favorit.getPath());
        object.addProperty("id", favorit.getId());
        favorites.add(object);
        writeFile(gson.toJson(favorites));
    }

    public ArrayList<Favorit> getFavorits(){
        ArrayList<Favorit> lists = new ArrayList<>();
        String data = readFile();
        Gson gson = new Gson();
        JsonArray favorites = gson.fromJson(data, JsonArray.class);
        for (int i=0;i<favorites.size();i++){
            JsonObject object = favorites.get(i).getAsJsonObject();
            Favorit favorit = new Favorit(object.get("name").getAsString(), object.get("path").getAsString(), object.get("id").getAsString());
            lists.add(favorit);
        }
        return lists;
    }


    public void writeFile(String data) {
        File file = checkFileExist();
        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFile() {
        File file = checkFileExist();
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }


    private File checkFileExist() {
        File filefind =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DOCUMENTS + "/AN DROID_APP_DATA/favorites.json"
                        );
        if (filefind.exists()) {
            return filefind;
        } else {
            File path =
                    Environment.getExternalStoragePublicDirectory
                            (
                                    //Environment.DIRECTORY_PICTURES
                                    Environment.DIRECTORY_DOCUMENTS + "/ANDROID_APP_DATA"
                            );
            if (!path.exists()) {
                // Make it, if it doesn't exit
                path.mkdirs();
            }
            File file = new File(path, "favorites.json");
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
            return file;
        }
    }
}