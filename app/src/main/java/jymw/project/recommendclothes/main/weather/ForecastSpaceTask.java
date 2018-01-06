package jymw.project.recommendclothes.main.weather;

import android.os.AsyncTask;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ForecastSpaceTask extends AsyncTask<Double, Double, JSONObject> {
    private int type;

    public ForecastSpaceTask(int type) {
        this.type = type;
    }

    @Override
    protected JSONObject doInBackground(Double... params) {
        JSONObject weatherInfo = new JSONObject();
        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); // 동네 예보
        try {
            String SERVICE_KEY = "mtQuFeWKvIlBbqEj9kZjt%2FTJJ2P3cU123qZfpS4ziovi3ugRh47VafKqmC5dPZyIngaBtP2mR9se%2F6r96S7AJA%3D%3D";
            // String SERVICE_KEY = "9hM28CjOEj%2BYFTpS6eki0kdLExLx%2Bw6a9ZBC4AyllUsl%2BAQAvv%2Bg1jj5ZiXI%2B%2BMiYfSeEXxjY031JlMw7s7S%2Bg%3D%3D";
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

            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) ((org.json.simple.JSONObject) ((org.json.simple.JSONObject) new JSONParser()
                    .parse(sb.toString())).get("response")).get("body")).get("items");
            JSONArray jsonArray = (JSONArray) jsonObject.get("item");
            for (int i = 0; i < jsonArray.size(); i++) {
                org.json.simple.JSONObject object = (org.json.simple.JSONObject) jsonArray.get(i);
                String category = String.valueOf(object.get("category"));
                String fcstTime = String.valueOf(object.get("fcstTime"));
                String fcstValue = String.valueOf(object.get("fcstValue"));
                if (type == 21) {
                    if (category.equals("TMN") || category.equals("TMX")) {
                        weatherInfo.put(category, fcstValue);
                    }
                } else {
                    if (category.equals("POP") || category.equals("SKY") || category.equals("T3H")) {
                        JSONObject temp = new JSONObject();
                        temp.put(fcstTime, getFcstContent(category, fcstValue));
                        weatherInfo.put(category, temp);
                    }
                }
            }
        } catch (ParseException | IOException | JSONException | NullPointerException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return weatherInfo;
    }

    /**
     * 오늘 날짜를 구하는 함수
     *
     * @return yyyyMMdd 형식의 문자열
     */
    private String getBaseDate() {
        int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.KOREA).format(new Date()));
        if (hour == 0 || hour == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            return new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(calendar.getTime());
        } else {
            return new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date());
        }
    }

    /**
     * 기준 시간을 반환하는 함수
     *
     * @return "0200"
     */
    private String getBaseTime() {
        int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.KOREA).format(new Date()));
        if (hour == 0 || hour == 1) {
            return "2300";
        } else {
            return "0200";
        }
    }

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
            default:
                break;
        }
        return fcstValue;
    }
}