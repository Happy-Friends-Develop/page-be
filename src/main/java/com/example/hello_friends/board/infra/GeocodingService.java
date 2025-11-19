package com.example.hello_friends.board.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeocodingService {
    @Value("${kakao.api.key}")
    private String apiKey;

    private final String API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=";

    public Map<String, Double> getCoordinate(String address) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더에 입장권(API Key) 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오에게 주소 물어보기
        ResponseEntity<String> response = restTemplate.exchange(
                API_URL + address,
                HttpMethod.GET,
                entity,
                String.class
        );

        // JSON 확인하기
        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONArray documents = jsonObject.getJSONArray("documents");

        // 검색 결과가 없으면 0.0, 0.0 반환 (혹은 에러 처리)
        if (documents.isEmpty()) {
            return null;
        }

        // 가장 첫 번째 검색 결과에서 x(경도), y(위도) 꺼내기
        JSONObject document = documents.getJSONObject(0);
        // 경도
        Double longitude = Double.parseDouble(document.getString("x"));
        // 위도
        Double latitude = Double.parseDouble(document.getString("y"));

        Map<String, Double> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }
}
