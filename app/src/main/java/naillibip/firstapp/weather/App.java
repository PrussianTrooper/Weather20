package naillibip.firstapp.weather;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.room.Room;
import naillibip.firstapp.weather.dao.ICitiesDao;
import naillibip.firstapp.weather.database.CitiesHistory;

public class App extends Application {

    private static final String KEY_DARK_MODE = "useDarkMode";

    private static App instance;
    private CitiesHistory db;
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(
                getApplicationContext(),
                CitiesHistory.class,
                "cities_history_search_database")
                .fallbackToDestructiveMigration()
                .build();

    }

    public static boolean getDarkModeStatus() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(instance.getApplicationContext());
        return pref.getBoolean(KEY_DARK_MODE, false);
    }

    public static void setDarkMode(boolean useDarkMode) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(instance.getApplicationContext());
        pref.edit().putBoolean(KEY_DARK_MODE, useDarkMode).apply();
    }

    public ICitiesDao getCitiesDao() {
        return db.getCitiesDao();
    }
}
