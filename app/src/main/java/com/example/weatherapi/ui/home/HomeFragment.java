package com.example.weatherapi.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weatherapi.R;
import com.example.weatherapi.databinding.FragmentHomeBinding;
import com.example.weatherapi.models.Clouds;
import com.example.weatherapi.models.Main;
import com.example.weatherapi.models.Model;
import com.example.weatherapi.models.Sys;
import com.example.weatherapi.models.Wind;
import com.example.weatherapi.remote_data.RetrofitBuilder;
import com.example.weatherapi.remote_data.WeatherApi;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    LottieAnimationView rain_lotty;
    LottieAnimationView snow_lotty;
    private FragmentHomeBinding binding;
    Integer temperature;
    Integer tempMaximal;
    Integer tempMinimal;
    String currentTime=java.text.DateFormat.getDateTimeInstance().format(new Date());
    int humidity_c;
    final String apiKey= WeatherApi.url;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.rainLotty.setAnimation(R.raw.rainy);
        binding.snowLotty.setAnimation(R.raw.snow);
        binding.localtime.setText(currentTime);

        Call<Model> call= RetrofitBuilder.getInstance().getCurrentWeather("Bishkek",apiKey);
        call.enqueue(new Callback<Model>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.isSuccessful() && response.body()!=null){
                    Main main_model=response.body().getModel();
                    Wind wind_model=response.body().getWind_model();
                    Clouds clouds_model=response.body().getClouds_model();
                    Sys sys_model=response.body().getSys_model();

                    Double temp=main_model.getTemp();
                    Double tempMax=main_model.getTempMax();
                    Double tempMin=main_model.getTempMin();

                    temperature=makeFromFaringate(temp);
                    tempMaximal=makeFromFaringate(tempMax);
                    tempMinimal=makeFromFaringate(tempMin);

                    binding.tempC.setText(String.valueOf(temperature)+" °C");
                    if(temperature<=14){
                        binding.sun.setVisibility(View.INVISIBLE);
                        setNoHotWeather();
                    }
                    binding.maxMinTemp.setText(String.valueOf(tempMaximal)+" °C↑ \n"+ String.valueOf(tempMinimal)+" °C↓");
                    binding.cityName.setText("Bishkek");
                    
                    binding.humidity.setText(main_model.getHumidity()+" %");
                    humidity_c=main_model.getHumidity();
                    if(humidity_c>=55){
                        rainy_possible();
                    }
                    binding.pressure.setText(main_model.getPressure()+" \nmBar");
                    
                    binding.wind.setText(wind_model.getSpeed()+" m/s");
                    binding.cloud.setText(clouds_model.getAll()+" %");

                    binding.sunrise.setText(String.valueOf(getCurrDateTime(sys_model.getSunrise())));
                    binding.sunset.setText(String.valueOf(getCurrDateTime(sys_model.getSunset())));

                    binding.timezone.setText(String.valueOf(response.body().getTimezone()));
                    setCondition();

                    if (response.body().getTimezone()<=6500 && response.body().getTimezone()>=-27508){
                        setNight();
                    }else {
                        setDay();
                    }

                }

            }

            @Override
            public void onFailure(Call<Model> call,@NonNull Throwable throwable) {
                Toast.makeText(requireActivity(), "No forecast data"+ throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.slideUpBottomSheet.setOnClickListener(v -> {


            if (binding.bottomSheet.getVisibility() == View.GONE) {
                binding.bottomSheet.setVisibility(View.VISIBLE);
            } else {
                binding.bottomSheet.setVisibility(View.GONE);
            }

            binding.rainLotty.setVisibility(View.INVISIBLE);

            binding.blueSky.setVisibility(View.VISIBLE);
            binding.sun.setVisibility(View.INVISIBLE);
            binding.badWeatherSky.setVisibility(View.INVISIBLE);
            binding.inputCity.setText("");
            binding.condition.setText("...");
            binding.isRainOrNot.setVisibility(View.INVISIBLE);
        });

        binding.search.setOnClickListener(v1 -> {
            if (!binding.inputCity.getText().toString().isEmpty()) {
                Call<Model> call = RetrofitBuilder.getInstance().getCurrentWeather(binding.inputCity.getText().toString(), apiKey);
                call.enqueue(new Callback<Model>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<Model> call, @NonNull Response<Model> response) {
                        Main main_model = response.body().getModel();
                        Wind wind_model = response.body().getWind_model();

                        Clouds clouds_model = response.body().getClouds_model();

                        Sys sys_model = response.body().getSys_model();

                        Double temp = main_model.getTemp();
                        Double tempMax = main_model.getTempMax();
                        Double tempMin = main_model.getTempMin();

                        temperature = makeFromFaringate(temp);
                        tempMaximal = makeFromFaringate(tempMax);
                        tempMinimal= makeFromFaringate(tempMin);

                        binding.tempC.setText(temperature + " °C");
                        binding.maxMinTemp.setText(String.valueOf(tempMaximal) + " °C↑  \n" + String.valueOf(tempMinimal) + " °C↓");

                        binding.cityName.setText(binding.inputCity.getText().toString());

                        binding.humidity.setText(main_model.getHumidity() + " %");
                        binding.pressure.setText(main_model.getPressure() + " \nmBar");

                        binding.cloud.setText(clouds_model.getAll() + " \nmBar");
                        binding.wind.setText(wind_model.getSpeed() + " m/s");

                        binding.sunrise.setText(getCurrDateTime(sys_model.getSunrise()));
                        binding.sunset.setText(getCurrDateTime(sys_model.getSunset()));

                        binding.timezone.setText(String.valueOf(response.body().getTimezone()));
                        setCondition();
                        if (response.body().getTimezone() <= 6500 && response.body().getTimezone() >= -27508) {
                            setNight();
                        } else {
                            setDay();
                        }
                    }


                    @Override
                    public void onFailure(Call<Model> call, Throwable t) {
                        Toast.makeText(requireActivity(), "No WeatherForeCast data"+ t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        binding.tempC.setText(t.getLocalizedMessage());
                    }
                });
                binding.bottomSheet.setVisibility(View.GONE);
            }else {
                Toast.makeText(requireActivity(), "Input the name of the city!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getCurrDateTime(long sunset) {
        String new_m=java.text.DateFormat.getDateTimeInstance().format(new Date(sunset));
        return new_m;
    }

    private void setCondition() {
        if(temperature>20 && temperature <=50){
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.condition.setText("Hot");
            dryWeather();
        }
        if(temperature<=20 && temperature>14){
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.condition.setText("Warm");
            dryWeather();
        }else {
            if(temperature>=12 && temperature <=14){
                setNoHotWeather();
                binding.condition.setText("Cloudy");
//                binding.sun.setVisibility(View.INVISIBLE);
                rainy_monitoring();
            }else {
                if(temperature>=10 && temperature<12){
                    setNoHotWeather();
                    binding.condition.setText("cold");
//                    binding.sun.setVisibility(View.INVISIBLE);

                    rainy_monitoring();

                }else {
                    if(temperature<10){
                        setNoHotWeather();
//                        binding.sun.setVisibility(View.GONE);
                        binding.condition.setText("very cold");
                        snow_monitoring();

                    }
                }
            }
        }
    }


    public void rainy_possible(){
        binding.isRainOrNot.setVisibility(View.VISIBLE);
        binding.isRainOrNot.setText("rain is \npossible ");
        binding.rainLotty.setVisibility(View.VISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
        binding.snowLotty.setVisibility(View.INVISIBLE);

    }
    public void snow_possible(){
        binding.isRainOrNot.setVisibility(View.VISIBLE);
        binding.snowLotty.setVisibility(View.VISIBLE);
        binding.isRainOrNot.setText("Snow is \npossible ");
        binding.rainLotty.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }


    public  void dryWeather(){
        binding.rainLotty.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.VISIBLE);
//        binding.sun.setVisibility(View.INVISIBLE);
        binding.snowLotty.setVisibility(View.INVISIBLE);

    }
    public void rainy_monitoring(){
        if (humidity_c<=55){
            binding.rainLotty.setVisibility(View.VISIBLE);
//            binding.sun.setVisibility(View.INVISIBLE);
            binding.isRainOrNot.setText("");
            binding.snowLotty.setVisibility(View.INVISIBLE);

            dryWeather();
        }else {
            rainy_possible();
        }
    }

    public void snow_monitoring(){
        if (temperature<=5){
            binding.snowLotty.setVisibility(View.VISIBLE);
//            binding.sun.setVisibility(View.INVISIBLE);
            binding.isRainOrNot.setText("");
            snow_possible();
        }else {
            snow_possible();
        }
    }
    public void setNoHotWeather(){
        binding.blueSky.setVisibility(View.INVISIBLE);
//        binding.sun.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);

    }
    public void setDay(){
        binding.nightSky.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.VISIBLE);
//        binding.sun.setVisibility(View.VISIBLE);

    }

    public void setNight(){
        binding.sun.setVisibility(View.INVISIBLE);
        binding.nightSky.setVisibility(View.VISIBLE);
        binding.blueSky.setVisibility(View.INVISIBLE);
    }

    private Integer makeFromFaringate(Double temp) {
        Integer gr=(int)(temp-273.15);
        return gr;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}