package com.vkontakte.miracle.memory.storage;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.getJSONStringIfHas;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.response.longpoll.UserLongPollServerResponse;
import com.vkontakte.miracle.response.messages.local.CachedConversationsResponse;
import com.vkontakte.miracle.throwable.users.InvalidCurrentUserException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsersStorage extends Storage {

    private static final String LOG_TAG = "UsersStorage";

    private static final String PUBLIC_CACHES_NAME = "Caches";
    private final File publicCachesDir;

    private static final String USERS_NAME = "users.json";
    private final File usersFile;

    private static final String AUTHORIZED_NAME = "authorized.json";
    private final File authorizedFile;


    private static final String USER_CACHES_NAME = "Caches_%1$s";
    private static final String IMAGES_NAME = "Images";
    private static final String USER_LONG_POLL_SERVER_NAME = "userLongPollServer.json";
    private static final String CONVERSATIONS_NAME = "conversations.json";


    private static final String SONGS_NAME = "songs.json";
    private static final String MP3S_NAME = "MP3s";
    private static final String PLAYLISTS_NAME = "playlists.json";

    private User currentUser;

    private static UsersStorage instance;

    public static UsersStorage get(){
        return instance;
    }

    public UsersStorage(File appFilesDir) {
        super(appFilesDir);
        instance = this;

        publicCachesDir = new File(getRootFilesDir(), PUBLIC_CACHES_NAME);
        usersFile = new File(publicCachesDir, USERS_NAME);
        authorizedFile = new File(publicCachesDir, AUTHORIZED_NAME);

        initializePublicDirectories();

        try {
            String authorizedId = loadCurrentUserId();
            if(authorizedId!=null){
                setCurrentUser(findUserById(authorizedId));
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }

    //-------------------------------------------------------------------------------//

    public void initializePublicDirectories(){
        if(createNewDirectory(publicCachesDir)){
            createNewFile(usersFile);
            createNewFile(authorizedFile);
        }
    }

    //-------------------------------------------------------------------------------//

    @NonNull
    public File getUsersCachesDir(@NonNull String userId){
        return new File(publicCachesDir, String.format(USER_CACHES_NAME, userId));
    }

    @Nullable
    public File getCurrentUsersCachesDir(){
        if(currentUser!=null){
            return getUsersCachesDir(currentUser.getId());
        }
        return null;
    }

    //-------------------------------------------------------------------------------//

    @Nullable
    public User findUserById(String id) throws IOException,JSONException {
        if(id!=null&&!id.isEmpty()) {
            Comparator comparator = supposed -> id.equals(supposed.getString("id"));
            JSONObject jsonObject = findJSONBy(usersFile, comparator);
            if(jsonObject!=null){
                return new User(jsonObject);
            }
        }
        return null;
    }

    public void insertUser(User user) throws IOException,JSONException {
        if(user!=null){
            JSONObject jsonObject = user.toJSONObject();
            appendJSON(usersFile, jsonObject);
        }
    }

    public boolean updateUser(User user) throws IOException,JSONException {
        if(user!=null){
            Comparator comparator = supposed -> user.getId().equals(supposed.getString("id"));
            JSONObject jsonObject = user.toJSONObject();
            return replaceJSONby(usersFile, jsonObject, comparator);
        }
        return false;
    }

    public boolean removeUserById(String id) throws IOException,JSONException {
        if(id!=null&&!id.isEmpty()) {
            Comparator comparator = supposed -> id.equals(supposed.getString("id"));
            return removeJSONby(usersFile, comparator);
        }
        return false;
    }

    @Nullable
    public List<User> loadUsers() throws IOException,JSONException {
        List<JSONObject> jsonObjects = readJSONList(usersFile);
        List<User> users = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            users.add(new User(jsonObject));
        }
        return users;
    }


    //-------------------------------------------------------------------------------//

    public void setCurrentUserId(@Nullable String userId) throws IOException,JSONException {
        saveCurrentUserId(userId);
        if(userId!=null){
            setCurrentUser(findUserById(userId));
        } else {
            setCurrentUser(null);
        }
    }

    private void saveCurrentUserId(@Nullable String userId) throws IOException,JSONException {
        Authorized authorized = loadAuthorized();
        if(authorized!=null) {
            authorized.userId = userId;
        } else {
            authorized = new Authorized(userId);
        }
        writeJSON(authorizedFile, authorized.toJSONObject());
    }

    @Nullable
    private String loadCurrentUserId() throws IOException,JSONException {
        Authorized authorized = loadAuthorized();
        if(authorized!=null){
            return authorized.userId;
        }
        return null;
    }

    private void setCurrentUser(@Nullable User user){
        currentUser = user;

        if(user!=null){
            initializeUserDirectories(user);
        }

        for (OnCurrentUserChangeListener listener:onCurrentUserChangeListeners) {
            listener.onCurrentUserChange(currentUser);
        }
    }

    public boolean updateCurrentUser(@Nullable User user) throws IOException,JSONException {
        if(user!=null && currentUser!=null && currentUser.getId().equals(user.getId())){
            currentUser = user;
            return updateUser(user);
        }
        return false;
    }

    private void initializeUserDirectories(@Nullable User user){
        if(user!=null){
            File userCaches = getUsersCachesDir(user.getId());
            if(createNewDirectory(userCaches)){
                createNewFile(new File(userCaches, USER_LONG_POLL_SERVER_NAME));
                createNewFile(new File(userCaches, CONVERSATIONS_NAME));
                createNewFile(new File(userCaches, SONGS_NAME));
                createNewFile(new File(userCaches, PLAYLISTS_NAME));
                createNewDirectory(new File(userCaches, IMAGES_NAME));
                createNewDirectory(new File(userCaches, MP3S_NAME));
            }
        }
    }

    //-------------------------------------------------------------------------------//

    @Nullable
    private Authorized loadAuthorized() throws IOException,JSONException {
        JSONObject jsonObject = readJSON(authorizedFile);
        if(jsonObject!=null){
            return new Authorized(jsonObject);
        }
        return null;
    }

    private void saveAuthorized(Authorized authorized) throws IOException,JSONException {
        writeJSON(authorizedFile, authorized.toJSONObject());
    }

    private static class Authorized implements JSONable {

        private String userId;

        private Authorized(String userId) {
            this.userId = userId;
        }

        private Authorized(JSONObject jsonObject) throws JSONException {
            this.userId = getJSONStringIfHas(jsonObject, "userId", null);
        }

        @Override
        public JSONObject toJSONObject() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            return jsonObject;
        }
    }

    //-------------------------------------------------------------------------------//

    @Nullable
    public User getCurrentUser(){
        return currentUser;
    }

    @NonNull
    public User requireCurrentUser(){
        if(currentUser!=null) {
            return currentUser;
        } else {
            throw new InvalidCurrentUserException("Current user is null.");
        }
    }

    public boolean authorized(){
        return currentUser!=null;
    }

    //-------------------------------------------------------------------------------//

    private final List<OnCurrentUserChangeListener> onCurrentUserChangeListeners = new ArrayList<>();

    public void addOnCurrentUserChangeListener(OnCurrentUserChangeListener listener, boolean returnValue){
        onCurrentUserChangeListeners.add(listener);
        if(returnValue){
            listener.onCurrentUserChange(currentUser);
        }
    }

    public void addOnCurrentUserChangeListener(OnCurrentUserChangeListener listener){
        addOnCurrentUserChangeListener(listener, false);
    }

    public void removeOnCurrentUserChangeListener(OnCurrentUserChangeListener listener){
        onCurrentUserChangeListeners.remove(listener);
    }

    public interface OnCurrentUserChangeListener{
        void onCurrentUserChange(@Nullable User user);
    }

    //-------------------------------------------------------------------------------//

    public void saveBitmapForCurrentUser(@Nullable Bitmap bitmap, @Nullable String name){
        if(currentUser!=null) {
            saveBitmapForUser(bitmap, name, currentUser.getId());
        }
    }

    public void saveBitmapForUser(@Nullable Bitmap bitmap, @Nullable String name, @Nullable String userId){
        if(name!=null&&bitmap!=null&&userId!=null) {
            File userCaches = getUsersCachesDir(userId);
            File imagesDir = new File(userCaches, IMAGES_NAME);
            writeBitmap(new File(imagesDir, name), bitmap);
        }
    }

    @Nullable
    public Bitmap loadBitmapForCurrentUser(@Nullable String path){
        if(currentUser!=null) {
            return loadBitmapForUser(path, currentUser.getId());
        }
        return null;
    }

    @Nullable
    public Bitmap loadBitmapForUser(@Nullable String path, @Nullable String userId) {
        if(path!=null&&userId!=null) {
            File userCaches = getUsersCachesDir(userId);
            File imagesDir = new File(userCaches, IMAGES_NAME);
            return loadBitmap(new File(imagesDir, path));
        }
        return null;
    }

    //-------------------------------------------------------------------------------//

    public void saveUserLongPollServerCache(UserLongPollServerResponse userLongPollServerResponse, File currentUserCaches) throws IOException,JSONException {
        if(currentUserCaches!=null) {
            File conversationsFile = new File(currentUserCaches, USER_LONG_POLL_SERVER_NAME);
            writeJSON(conversationsFile, userLongPollServerResponse.toJSONObject());
        }
    }

    public void clearUserLongPollServerCache(File currentUserCaches) throws IOException {
        if(currentUserCaches!=null) {
            File conversationsFile = new File(currentUserCaches, USER_LONG_POLL_SERVER_NAME);
            clearJSON(conversationsFile);
        }
    }

    @Nullable
    public UserLongPollServerResponse loadUserLongPollServerCache(File currentUserCaches) throws IOException,JSONException {
        if(currentUserCaches!=null) {
            File conversationsFile = new File(currentUserCaches, USER_LONG_POLL_SERVER_NAME);
            JSONObject jsonObject = readJSON(conversationsFile);
            if(jsonObject!=null) {
                return new UserLongPollServerResponse(jsonObject);
            }
        }
        return null;
    }

    //-------------------------------------------------------------------------------//

    public void saveConversationsCache(CachedConversationsResponse conversationsResponse, File currentUserCaches) throws IOException,JSONException {
        if(currentUserCaches!=null) {
            File conversationsFile = new File(currentUserCaches, CONVERSATIONS_NAME);
            writeJSON(conversationsFile, conversationsResponse.toJSONObject());
        }
    }

    public void clearConversationsCache(File currentUserCaches) throws IOException {
        if(currentUserCaches!=null) {
            File conversationsFile = new File(currentUserCaches, CONVERSATIONS_NAME);
            clearJSON(conversationsFile);
        }
    }

    @Nullable
    public CachedConversationsResponse loadConversationsCache(File currentUserCaches) throws IOException,JSONException {
        if(currentUserCaches!=null) {
            File conversationsFile = new File(currentUserCaches, CONVERSATIONS_NAME);
            JSONObject jsonObject = readJSON(conversationsFile);
            if(jsonObject!=null) {
                return new CachedConversationsResponse(jsonObject);
            }
        }
        return null;
    }

}
