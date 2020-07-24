package naillibip.firstapp.weather;

import java.util.List;

import naillibip.firstapp.weather.dao.ICitiesDao;

public class CitiesHistorySource {

    private final ICitiesDao citiesDao;
    private List<CityCard> cityCardList;

    public CitiesHistorySource(ICitiesDao citiesDao) {
        this.citiesDao = citiesDao;
    }

    public List<CityCard> getCityCardList() {
        if (cityCardList == null) {
            loadCities();
        }
        return cityCardList;
    }

    public void deleteAllCityCards() {
        new Thread(() -> {
            citiesDao.deleteAllCityCards();
            cityCardList = citiesDao.getAllCityCards();
        }).start();
    }

    public void loadCities() {
        new Thread(() -> cityCardList = citiesDao.getAllCityCards()).start();
    }

    public void addCity(CityCard cityCard) {
        new Thread(() -> {
            citiesDao.insertCity(cityCard);
            cityCardList = citiesDao.getAllCityCards();
        }).start();

    }

    public void updateCity(CityCard cityCard) {
        new Thread(() -> {
            citiesDao.updateCity(cityCard);
            cityCardList = citiesDao.getAllCityCards();
        }).start();
    }

    public void removeCity(CityCard cityCard) {
        new Thread(() -> {
            citiesDao.deleteCity(cityCard);
            cityCardList = citiesDao.getAllCityCards();
        }).start();
    }
}

