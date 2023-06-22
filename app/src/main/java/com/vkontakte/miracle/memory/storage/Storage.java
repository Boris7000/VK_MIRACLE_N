package com.vkontakte.miracle.memory.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static final String LOG_TAG = "Storage";

    private final File rootFilesDir;

    public Storage(File rootFilesDir) {
        this.rootFilesDir = rootFilesDir;
    }

    public File getRootFilesDir() {
        return rootFilesDir;
    }

    //------------------------------------------------------------//

    public static boolean createNewDirectory(@Nullable File file){
        if(file!=null) {
            if (!file.exists()) {
                if (file.mkdir()) {
                    Log.d(LOG_TAG, "Created directory " + file.getAbsolutePath());
                    return true;
                } else {
                    Log.d(LOG_TAG, "Unable to create directory " + file.getAbsolutePath());
                }
            } else {
                Log.d(LOG_TAG, "Unable to create an already existing directory " + file.getAbsolutePath());
                return true;
            }
        } else {
            Log.d(LOG_TAG, "Unable to create null directory.");
        }
        return false;
    }

    public static boolean removeDirectory(@Nullable File file){
        if(file!=null) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] contents = file.listFiles();
                    if (contents != null) {
                        for (File f : contents) {
                            removeDirectory(f);
                        }
                    }
                    if (file.delete()) {
                        Log.d(LOG_TAG, "Deleted directory " + file.getAbsolutePath());
                        return true;
                    } else {
                        Log.d(LOG_TAG, "Unable to delete directory " + file.getAbsolutePath());
                    }
                } else {
                    Log.d(LOG_TAG, "Unable to delete file as directory " + file.getAbsolutePath());
                }
            } else {
                Log.d(LOG_TAG, "Unable to delete non-existent directory " + file.getAbsolutePath());
                return true;
            }
        } else {
            Log.d(LOG_TAG, "Unable to delete null directory.");
        }
        return false;
    }

    //------------------------------------------------------------//

    public static boolean checkFile(File file){
        return file!=null&&file.exists()&&file.isFile();
    }

    public static boolean createNewFile(File file){
        if(!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Log.d(LOG_TAG, "Created file "+file.getAbsolutePath());
                    return true;
                } else {
                    Log.d(LOG_TAG, "Unable to create file "+file.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Unable to create file "+file.getAbsolutePath());
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "Unable to create an already existing file "+file.getAbsolutePath());
            return true;
        }
        return false;
    }

    public static boolean deleteFile(File file){
        if(file.exists()) {
            if(file.isFile()) {
                if (file.delete()) {
                    Log.d(LOG_TAG, "Deleted file " + file.getAbsolutePath());
                    return true;
                } else {
                    Log.d(LOG_TAG, "Unable to delete file " + file.getAbsolutePath());
                }
            } else {
                Log.d(LOG_TAG, "Unable to delete directory as file" + file.getAbsolutePath());
            }
        } else {
            Log.d(LOG_TAG, "Unable to delete non-existent file " + file.getAbsolutePath());
            return true;
        }
        return false;
    }

    //------------------------------------------------------------//

    public static boolean writeObject(File file, Object object){
        if(checkFile(file)) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(object);
                objectOutputStream.close();
                fileOutputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Nullable
    public static Object readObject(File file){
        if(file!=null&&file.exists()&&file.isFile()) {
            if (file.length() > 0) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    Object object = objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    return object;
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //------------------------------------------------------------//

    public static boolean writeBitmap(File file, Bitmap bitmap){
        if(checkFile(file)||createNewFile(file)){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Bitmap loadBitmap(File file){
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static boolean removeBitmap(File file){
        if (file.exists()) {
            if (file.delete()) {
                Log.d(LOG_TAG, "Deleted file "+file.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    //------------------------------------------------------------//

    public static void writeJSONList(File file, List<JSONObject> array) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (JSONObject jsonObject:array){
            writer.write(jsonObject.toString());
            writer.newLine();
        }
        writer.close();
    }

    public static JSONObject readJSON(File file) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        if(line!=null){
            return new JSONObject(line);
        }
        return null;
    }

    public static void writeJSON(File file, JSONObject jsonObject) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(jsonObject.toString());
        writer.close();
    }

    public static void clearJSON(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.close();
    }

    public static List<JSONObject> readJSONList(File file) throws IOException, JSONException {
        BufferedReader reader  = new BufferedReader(new FileReader(file));
        List<JSONObject> array = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            array.add(new JSONObject(line));
        }
        reader.close();
        return array;
    }

    public static void appendJSON(File file, JSONObject jsonObject) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        writer.write(jsonObject.toString());
        writer.newLine();
        writer.close();
    }

    @Nullable
    public static JSONObject readJSONAt(File file, int index) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            count++;
            if (count == index) {
                return new JSONObject(line);
            }
        }
        return null;
    }

    @Nullable
    public static JSONObject findJSONBy(File file, Comparator comparator) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(line);
            if (comparator.compare(jsonObject)) {
                return jsonObject;
            }
        }
        return null;
    }

    public static boolean replaceJSONby(File file, JSONObject jsonObject, Comparator comparator) throws IOException, JSONException {
        if(file!=null) {
            File tempFile = new File(file.getParentFile(), "replaceJSONby.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                JSONObject oldJsonObject = new JSONObject(currentLine);
                if (comparator.compare(oldJsonObject)) {
                    currentLine = jsonObject.toString();
                }
                writer.write(currentLine + System.getProperty("line.separator"));
            }

            writer.close();
            reader.close();
            return tempFile.renameTo(file);
        }
        return false;
    }

    public static boolean removeJSONby(File file, Comparator comparator) throws IOException, JSONException {
        if(file!=null) {
            File tempFile = new File(file.getParentFile(), "removeJSONby.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(currentLine);
                if (comparator.compare(jsonObject)) {
                    continue;
                }
                writer.write(currentLine + System.getProperty("line.separator"));
            }

            writer.close();
            reader.close();
            return tempFile.renameTo(file);
        }
        return false;
    }

    public interface Comparator{
        boolean compare(JSONObject supposed) throws JSONException;
    }

    public static boolean removeLineAt(File file, int index) throws IOException {
        if(file!=null&&index>=0) {
            File tempFile = new File(file.getParentFile(), "removeLineTempFile.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            int lineNumber = 0;
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (lineNumber++ == index) {
                    continue;
                }
                writer.write(currentLine + System.getProperty("line.separator"));
            }

            writer.close();
            reader.close();
            return tempFile.renameTo(file);
        }
        return false;
    }

    //------------------------------------------------------------//

    public static class ArrayListReader<T>{
        @Nullable
        public ArrayList<T> read(@Nullable File file, AnonymousConverter<T> converter){
            if(file!=null&&file.exists()&&file.isFile()) {
                if(file.length()>0) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        ArrayList<?> objects = (ArrayList<?>) objectInputStream.readObject();
                        objectInputStream.close();
                        fileInputStream.close();
                        if (objects == null) {
                            objects = new ArrayList<>();
                        }
                        ArrayList<T> items = new ArrayList<>();
                        for (Object object : objects) {
                            if (object != null) {
                                items.add(converter.convert(object));
                            }
                        }
                        return items;

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new ArrayList<>();
                }
            }
            return null;
        }
    }

    public interface AnonymousConverter<T>{
        T convert(Object object);
    }

    //------------------------------------------------------------//

}
