package naillibip.firstapp.weather.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import naillibip.firstapp.weather.CityCard;
import naillibip.firstapp.weather.dao.ICitiesDao;

@Database(entities = {CityCard.class}, version = 7)
public abstract class CitiesHistory extends RoomDatabase {
    public abstract ICitiesDao getCitiesDao();
}
