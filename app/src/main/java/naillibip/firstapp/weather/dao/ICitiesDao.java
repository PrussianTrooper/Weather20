package naillibip.firstapp.weather.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import naillibip.firstapp.weather.CityCard;

@Dao
public interface ICitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(CityCard cityCard);

    @Delete
    void deleteCity(CityCard cityCard);

    @Update
    void updateCity(CityCard cityCard);

    @Query("SELECT * FROM citycard ORDER BY number_of_searches DESC LIMIT 10")
    List<CityCard> getAllCityCards();

    @Query("DELETE FROM citycard")
    void deleteAllCityCards();
}
