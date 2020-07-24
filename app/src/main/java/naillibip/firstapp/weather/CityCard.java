package naillibip.firstapp.weather;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@Entity(indices = {@Index(value = {"cityName", "number_of_searches"})})
public class CityCard implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "cityName")
    public String cityName;

    @ColumnInfo(name = "number_of_searches")
    public int numberOfSearches;

    @Ignore
    private double temp;



    @Ignore
    private int position;
    @Ignore
    private int humidity;
    @Ignore
    private int pressure;
    @Ignore
    private int wind;

    @Ignore
    private String icon, country, description, updateOn;

    @Ignore
    private double feelsTemp, tempMax, tempMin;

    public double getTempMax() {
        return tempMax;
    }

    void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public double getTempMin() {
        return tempMin;
    }

    void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTemp(){
        return temp;
    }

    public double getFeelsTemp(){
        return feelsTemp;
    }

    public CityCard(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public int getHumidity() {
        return humidity;
    }

    void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    void setTemp(double temp) {
        this.temp = temp;
    }

    void setFeelsTemp(double feelsTemp) {
        this.feelsTemp = feelsTemp;
    }

    public String getIcon(){
        return icon;
    }

    int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getWind() {
        return wind;
    }

    void setWind(double wind) {
        this.wind = (int) wind;
    }

    void setCityName(String cityName) {
        this.cityName = cityName;
    }

    void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCountry() {
        return country;
    }

    void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateOn() {
        return updateOn;
    }

    void setUpdateOn(String updateOn) {
        this.updateOn = updateOn;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CityCard cityCard = (CityCard) object;
        return this.cityName.equalsIgnoreCase(cityCard.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cityName);
    }
}
