package com.halodoc.testWeatherApi;

import haloDoc.qa.pojos.response.weatherForcast.ConsolidatedWeather;
import haloDoc.qa.pojos.response.weatherForcast.WeatherForcastResponse;
import haloDoc.qa.pojos.response.woeID.WoeIdResponse;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.List;

import static io.restassured.RestAssured.given;

public class weatherApi {
    public static Logger logger = LoggerFactory.getLogger(weatherApi.class);
    public static RequestSpecification requestSpecification = given();
//    @BeforeSuite
//    @Parameters({"authType","username","password"})
//    public void beforeSuite(@Optional String authType, @Optional String username, @Optional String password) {
//        if(authType!=null){
//            if(authType.equalsIgnoreCase("PREEMPTIVE")){
//                requestSpecification.auth().preemptive().basic(username,password);
//                logger.info("Preemptive basic auth");
//            } else if(authType.equalsIgnoreCase("NON_PREEMPTIVE")){
//                requestSpecification.auth().basic(username,password);
//                logger.info("Non preemptive basic auth");
//            }
//        }else{
//            logger.info("No authentication was set");
//        }
//    }

    @BeforeTest
    @Parameters({"baseURI"})
    public void beforeTest(@Optional String baseURI) {
        if (baseURI != null) {
            requestSpecification.baseUri(baseURI);
        }
    }

    @BeforeMethod
    @Parameters({"basePath"})
    public void beforeMethod(@Optional String basePath) {
        if (basePath != null) {
            requestSpecification.basePath(basePath);
        }
    }

    public static final String QUERY = "query";

    @Test
    @Parameters({"city", "baseURI", "predictability"})
    public void getWeatherForecast(String city, String baseUri, Integer predictability) {
        /**
         * Getting WoeID from City
         */
        logger.info("City to search is : ", city);
        logger.info("sending request");
        Response response = requestSpecification.queryParam("query", city).get();
        logger.info(response.asString());
        WoeIdResponse[] woeIdResponses = response.as(WoeIdResponse[].class, ObjectMapperType.GSON);
        int woeID = woeIdResponses[0].getWoeid();
        logger.info("woeID for city : {} is {}", city, woeID);

        /**
         * Getting Weather Forecast from woeID.
         */
        requestSpecification = given();
        requestSpecification.baseUri(baseUri);
        requestSpecification.basePath("/" + woeID);
        Response response1 = requestSpecification.get();
        logger.info(response1.asString());
        WeatherForcastResponse weatherForcastResponse = response1.as(WeatherForcastResponse.class, ObjectMapperType.GSON);
        List<ConsolidatedWeather> consolidatedWeatherList = weatherForcastResponse.getConsolidatedWeather();

        /**
         * This Will check all objects in consolidated_weather json array and if predictability provided by user is >=
         * to current object it will print.
         */
        int temp = 0;
        for (int i = 0; i < consolidatedWeatherList.size(); i++) {
            float minTemp = consolidatedWeatherList.get(i).getMinTemp();
            float maxTemp = consolidatedWeatherList.get(i).getMaxTemp();
            int predict = consolidatedWeatherList.get(i).getPredictability();
            if (predict >= predictability) {
                temp++;
                System.out.println("Min Temp: " + minTemp + " , Max Temp: " + maxTemp + ", Predictability:" + predictability);
            }
        }
        if(temp==0){
            System.out.println("No weather ID is having predictability equal to more than: "+predictability);
        }
    }
}
