package tmedia.ir.melkeurmia.model;

import java.util.List;

/**
 * Created by tmedia on 7/9/2018.
 */

public class ProviceModel extends CityModel {

    private List<CityModel> cities;


    public List<CityModel> getCities() {
        return cities;
    }

    public void setCities(List<CityModel> cities) {
        this.cities = cities;
    }
}
