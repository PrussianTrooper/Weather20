package naillibip.firstapp.weather.event;

import naillibip.firstapp.weather.CityCard;

public class AddCityToListEvent {

    private CityCard cityCard;

    public AddCityToListEvent(CityCard cityCard) {
        this.cityCard = cityCard;
    }

    public CityCard getCityCard() {
        return cityCard;
    }
}
