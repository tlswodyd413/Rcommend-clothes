package jymw.project.recommendclothes.main.weather;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ForecastTimeTask extends AsyncTask<Double, Double, JSONObject> {

    @Override
    protected JSONObject doInBackground(Double... params) {
        JSONObject weatherInfo = new JSONObject();
        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastTimeData"); // 초단기 예보
        try {
            String SERVICE_KEY = "mtQuFeWKvIlBbqEj9kZjt%2FTJJ2P3cU123qZfpS4ziovi3ugRh47VafKqmC5dPZyIngaBtP2mR9se%2F6r96S7AJA%3D%3D";
            urlBuilder.append("?").append("ServiceKey").append("=").append(SERVICE_KEY);
            urlBuilder.append("&").append("ServiceKey").append("=").append(SERVICE_KEY);
            urlBuilder.append("&").append("base_date").append("=").append(getBaseDate());
            urlBuilder.append("&").append("base_time").append("=").append(getBaseTime());
            urlBuilder.append("&").append("nx").append("=").append(String.valueOf((Math.round(params[0])))); /* 예보 지점의 X 좌표값 */
            urlBuilder.append("&").append("ny").append("=").append(String.valueOf(Math.round(params[1]))); /* 예보 지점의 Y 좌표값 */
            urlBuilder.append("&").append("numOfRows").append("=").append("62"); /* 한 페이지 결과 수 */
            urlBuilder.append("&").append("pageNo").append("=").append("1"); /* 페이지 번호 */
            urlBuilder.append("&").append("_type").append("=").append("json"); // xml 또는 json
            HttpURLConnection connection = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            BufferedReader br;
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            connection.disconnect();
            Log.e("ForecastTimeTask1", sb.toString());
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) ((org.json.simple.JSONObject) ((org.json.simple.JSONObject) new JSONParser()
                    .parse(sb.toString())).get("response")).get("body")).get("items");
            JSONArray jsonArray = (JSONArray) jsonObject.get("item");
            for (int i = 0; i < jsonArray.size(); i++) {
                org.json.simple.JSONObject object = (org.json.simple.JSONObject) jsonArray.get(i);
                String category = String.valueOf(object.get("category"));
                String fcstValue = String.valueOf(object.get("fcstValue"));
                if (object.get("fcstTime").toString().equals(getNextTime())) {
                    if (category.equals("T1H") || category.equals("SKY") || category.equals("REH") || category.equals("PTY") || category.equals("WSD")) {
                        weatherInfo.put(category, getFcstContent(category, fcstValue));
                    }
                }
            }
        } catch (IOException | ParseException | JSONException | NullPointerException e) {
            System.out.println(e.getLocalizedMessage());
        }
        Log.e("ForecastTimeTask2", String.valueOf(weatherInfo));
        return weatherInfo;
    }

    /**
     * 오늘 날짜를 구하는 함수
     *
     * @return yyyyMMdd 형식의 문자열
     */
    private String getBaseDate() {
        return new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date());
    }

    /**
     * 기준 시간을 구하는 함수
     *
     * @return 매 시 30분을 나타내는 문자열
     */
    private String getBaseTime() {
        String hour = new SimpleDateFormat("HH", Locale.KOREA).format(new Date());
        String minute = new SimpleDateFormat("mm", Locale.KOREA).format(new Date());
        if (Integer.parseInt(minute) <= 30) {
            hour = String.valueOf(Integer.parseInt(hour) - 1);
            if (Integer.parseInt(hour) <= 9) {
                hour = "0" + hour;
            }
        }
        return hour.concat("30");
    }

    /**
     * 다음 예보 시간을 구하는 함수
     *
     * @return 매 시 정각을 나타내는 문자열
     */
    private String getNextTime() {
        String hour = new SimpleDateFormat("HH", Locale.KOREA).format(new Date());
        String minute = new SimpleDateFormat("mm", Locale.KOREA).format(new Date());
        if (Integer.parseInt(minute) > 30) {
            hour = String.valueOf(Integer.parseInt(hour) + 1);
            if (Integer.parseInt(hour) <= 9) {
                hour = "0" + hour;
            } else if (Integer.parseInt(hour) == 24) {
                hour = "00";
            }
        }
        return hour.concat("00");
    }

    /**
     * 예보 요소와 값에 따라 내용을 구하는 함수
     *
     * @param category  예보 요소
     * @param fcstValue 예보 값
     * @return 예보 내용
     */
    private String getFcstContent(String category, String fcstValue) {
        switch (category) {
            case "SKY": // 날씨
                switch (fcstValue) {
                    case "1":
                        return "맑음";
                    case "2":
                        return "구름조금";
                    case "3":
                        return "구름많음";
                    case "4":
                        return "흐림";
                }
                break;
            case "PTY": // 강수 형태
                switch (fcstValue) {
                    case "0":
                        return "없음";
                    case "1":
                        return "비";
                    case "2":
                        return "비/눈";
                    case "3":
                        return "눈";
                }
                break;
            default:
                break;
        }
        return fcstValue;
    }
}