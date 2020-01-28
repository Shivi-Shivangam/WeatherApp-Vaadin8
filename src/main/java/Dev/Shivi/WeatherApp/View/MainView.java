/**
 * @Project WeatherApp
 * @Class MainView
 * *
 * @author Shivangam_Soni
 * @since 27 Jan 2020 : 4:02 PM
 */
package Dev.Shivi.WeatherApp.View;

import Dev.Shivi.WeatherApp.Controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SpringUI(path = "")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;
    private JSONObject weatherJsonObject;
    private JSONArray weatherJsonArray;
    private VerticalLayout mainLayout;
//Form
    private NativeSelect<String> unitSelect;
    private TextField city;
    private Button showWeather;
//Dash-Title
    HorizontalLayout dashboardMain;
    private Label currentCityTitle, currentTemp;
    private Image iconImage;
//Dash-Description
    HorizontalLayout mainDescriptionLayout;
    VerticalLayout descriptionSectionALayout;
    private Label weatherMain, weatherDescription, weatherRealFeel, weatherMin, weatherMax;//Dash-Description Temp-Description
    VerticalLayout descriptionSectionBLayout;
    private Label pressureLabel, humidityLabel, windSpeedLabel, sunRiseLabel, sunSetLabel;//Dash-Description Pressure-Description

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashboardTitle();
        dashboardDesc();

        showWeather.addClickListener(event -> {
            if(!city.isEmpty()){
                try {
                    updateUI();
                } catch (JSONException e) { e.printStackTrace(); }
            }else{
                Notification.show("Please Enter City!");
            }
        });
    }

    private void setUpLayout(){
        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(mainLayout);
    }
    private void setHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Weather");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        headerLayout.addComponent(title);

        mainLayout.addComponent(headerLayout);
    }
    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Image logo = new Image(null, new ClassResource("/WeatherAppIcon.png"));
        logo.setWidth("125px");
        logo.setHeight("125px");

        logoLayout.addComponent(logo);

        mainLayout.addComponent(logoLayout);
    }
    private void setUpForm() {
        HorizontalLayout formlayout = new HorizontalLayout();
        formlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formlayout.setSpacing(true);
        formlayout.setMargin(true);

        //Create the Temperature Selector Component
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth("50px");
        unitSelect.setEmptySelectionAllowed(false);
        ArrayList<String> items = new ArrayList<>();
        items.add("°C");
        items.add("°F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formlayout.addComponent(unitSelect);

        //Text Field for City
        city = new TextField();
        city.setWidth("80%");
        city.setPlaceholder("City");
        formlayout.addComponent(city);

        //Add Weather Button
        showWeather = new Button();
        showWeather.setIcon(VaadinIcons.SEARCH);
        formlayout.addComponent(showWeather);

        mainLayout.addComponent(formlayout);
    }
    private void dashboardTitle() {
        dashboardMain = new HorizontalLayout();
        dashboardMain.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //Add Current City Label
        currentCityTitle = new Label("Currently in City");
        currentCityTitle.addStyleName(ValoTheme.LABEL_H2);
        currentCityTitle.addStyleName(ValoTheme.LABEL_LIGHT);

        //Add Weather Icon Happens Dynamically in updateUi()
        iconImage = new Image();

        //Add Current Temp Label
        currentTemp = new Label(" °C");
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);
    }
    private void dashboardDesc() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //Section-A: Main, Description, RealFeel, Max & Min Temp
        descriptionSectionALayout = new VerticalLayout();
        descriptionSectionALayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        descriptionSectionALayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        weatherMain = new Label("Main: ");
        weatherDescription = new Label("Description: ");
        weatherRealFeel = new Label("Feels Like: °C");
        weatherRealFeel.addStyleName(ValoTheme.LABEL_SUCCESS);
        weatherMin = new Label("Min: °C");
        weatherMax = new Label("Min: °C");
        descriptionSectionALayout.addComponents(weatherMain, weatherDescription, weatherRealFeel, weatherMin, weatherMax);

        //Section-B: Pressure, Humidity, Wind, SunRise, SunSet Vertical Layout
        descriptionSectionBLayout = new VerticalLayout();
        descriptionSectionBLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        descriptionSectionBLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        pressureLabel = new Label("Pressure: ");
        humidityLabel = new Label("Humidity: ");
        windSpeedLabel = new Label("Wind: ");
        sunRiseLabel = new Label("Sunrise:  AM");
        sunSetLabel = new Label("Sunset:  PM");
        descriptionSectionBLayout.addComponents(pressureLabel, humidityLabel, windSpeedLabel, sunRiseLabel, sunSetLabel);
    }

    private void updateUI() throws JSONException {
        String cityString = city.getValue();
        weatherService.setCityName(cityString);
        //Checking If Country Exists
        if(weatherService.getWeather().getString("cod").equals("404")){
            Notification.show(cityString+", Does Not Exist.", Notification.Type.WARNING_MESSAGE);
        }else{
            String defaultUnit, tempUnit, speedUnit;

            if(unitSelect.getValue().equals("°C")){
                defaultUnit = "metric";
                tempUnit = "°C";
                speedUnit = "m/s";
            }else {
                defaultUnit = "imperial";
                tempUnit = "°F";
                speedUnit = "mph";
            }

            weatherService.setUnit(defaultUnit);

            //Getting All Weather Details
            String iconCode = null, main = null, description = null;
            weatherJsonArray = weatherService.getWeatherArray();
            for(int i=0; i<weatherJsonArray.length(); i++) {
                weatherJsonObject = weatherJsonArray.getJSONObject(i);
                iconCode = weatherJsonObject.getString("icon");
                main = weatherJsonObject.getString("main");
                description = weatherJsonObject.getString("description");
            }

            double temp, feelLike, tempMin, tempMax;
            long pressure, humidity;
            weatherJsonObject = weatherService.getMainObject();
            temp = weatherJsonObject.getDouble("temp");
            feelLike = weatherJsonObject.getDouble("feels_like");
            tempMin = weatherJsonObject.getDouble("temp_min");
            tempMax = weatherJsonObject.getDouble("temp_max");
            pressure = weatherJsonObject.getLong("pressure");
            humidity = weatherJsonObject.getLong("humidity");

            double windSpeed;
            weatherJsonObject = weatherService.getWindObject();
            windSpeed = weatherJsonObject.getDouble("speed");

            String country;
            long sunRise, sunSet;
            weatherJsonObject = weatherService.getSunObject();
            country = weatherJsonObject.getString("country");
            sunRise = weatherJsonObject.getLong("sunrise")*1000;
            sunSet = weatherJsonObject.getLong("sunset")*1000;

//Update Dashboard Title
            //City
            currentCityTitle.setValue("Currently in "+weatherService.getWeather().getString("name")+", "
                    +country);
            //Current Temp
            currentTemp.setValue(temp+tempUnit);
            //Icon Image
            iconImage.setSource(new ExternalResource("http://openweathermap.org/img/wn/"+iconCode+"@2x.png"));
            //Adding Updated UI
            dashboardMain.addComponents(currentCityTitle, iconImage, currentTemp);
            mainLayout.addComponent(dashboardMain);

//Update Dashboard Description
            //Section-A
            weatherMain.setValue(main);
            weatherDescription.setValue("Desc: "+description);
            weatherRealFeel.setValue("Feels Like: "+feelLike+tempUnit);
            weatherMin.setValue("Min: "+tempMin+tempUnit);
            weatherMax.setValue("Min: "+tempMax+tempUnit);

            //Section-B
            pressureLabel.setValue("Pressure: "+pressure+" hPa");
            humidityLabel.setValue("Humidity: "+humidity+"%");
            windSpeedLabel.setValue("Wind: "+windSpeed+speedUnit);
            sunRiseLabel.setValue("SunRise: "+convertTime(sunRise));
            sunSetLabel.setValue("SunSet: "+convertTime(sunSet));

            //Adding Updated UI
            mainDescriptionLayout.addComponents(descriptionSectionALayout, descriptionSectionBLayout);
            mainLayout.addComponent(mainDescriptionLayout);
        }
    }

    private String convertTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");

        return dateFormat.format(time);
    }
}