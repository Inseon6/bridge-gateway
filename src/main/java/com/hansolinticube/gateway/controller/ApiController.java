package com.hansolinticube.gateway.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.hansolinticube.gateway.GatewayInitializer;

import com.hansolinticube.gateway.service.ControlCTIService;
import com.hansolinticube.gateway.service.ManageStationService;
import com.hansolinticube.gateway.service.ManageVDNService;
import com.hansolinticube.gateway.service.SelectCTILogService;
import com.hansolinticube.gateway.service.ServiceType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {
    final static Logger logger = LogManager.getLogger(ApiController.class);
    final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/resource/extension/modification", method = RequestMethod.POST, produces = "application/json; charset=UTF8")
    public ResponseEntity<String> resourceExtensionModification(HttpServletRequest request, @RequestBody String requestBody) {
        GatewayInitializer gatewayInitializer = GatewayInitializer.getInstance();

        /* 요청 클라이언트 정보 로깅 */
        logger.info(MessageFormat.format("클라이언트로부터 요청을 받았습니다.\n" +
                                                "클라이언트 아이피 : {0}\n" +
                                                "클라이언트 포트 : {1}\n" +
                                                "클라이언트 요청 텍스트 메시지 : {2}", request.getRemoteHost(), request.getRemotePort(), requestBody));

        /*
         * 각 요청 타입에 대한 내용
         * (1) VDN 리소스 변경 이벤트 전달(생성, 삭제, 수정)
         * (2) CTI 서비스 제어
         * (3) 이벤트 로그 조회를 위한 정보 전달
         * 이때 각 요청 타입에 대한 구별은 serviceType 값을 이용한다.
         */

        /* ISAC으로부터 받은 메시지 */
        JsonObject jsonMessageFromISAC = new Gson().fromJson(requestBody, JsonObject.class);
        logger.info(MessageFormat.format("ISAC으로부터 받은 JSON 타입의 요청 메시지\n{0}", gson.toJson(jsonMessageFromISAC)));

        JsonObject jsonIsacData = jsonMessageFromISAC.getAsJsonObject("isacData");

        /* isacData 파라미터 존재 여부 검증 */
        if (jsonIsacData == null) {
            String errorMessage = "요청 메시지에 isacData 파라미터에 대한 값이 존재하지 않습니다.";
            logger.error(errorMessage);
            Map<String, Object> response = new HashMap<>();
            response.put("requestResult", "failure");
            response.put("errorMessage", errorMessage);
            String jsonResponse = new Gson().toJson(response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        }

        JsonElement serviceType = jsonMessageFromISAC.get("serviceType");

        /* serviceType 파라미터 존재 여부 검증 */
        if (serviceType == null) {
            String errorMessage = "arguments 파라미터 값인 JSON 객체 중에서 serviceType 파라미터에 대한 값이 존재하지 않습니다.";
            logger.error(errorMessage);
            Map<String, Object> response = new HashMap<>();
            response.put("requestResult", "failure");
            response.put("errorMessage", errorMessage);
            String jsonResponse = new Gson().toJson(response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        }

        /* 분기 시작 */
        String requestType = serviceType.getAsString();
        ServiceType service;

        switch (requestType) {
            case "ManageVDNService":
                // (1) VDN 리소스 변경 이벤트 전달(생성, 삭제, 수정)
                // 센터별 CTI-01(Primary)에 요청하고 만약에 응답이 없으면 CTI-02(Backup)에 요청
                logger.info("ManageVDNService 서비스가 요청되었습니다.");
                service = new ManageVDNService(jsonMessageFromISAC);
                break;
            case "ControlCTIService":
                // (2) CTI 서비스 제어
                // 제어가 필요한 각 서버에 요청
                logger.info("ControlCTIService 서비스가 요청되었습니다.");
                service = new ControlCTIService(jsonMessageFromISAC);
                break;
            case "SelectCTILogService":
                // (3) CTI 로그 조회를 위한 정보 전달
                // 조회가 필요한 각 서버에 요청
                logger.info("SelectCTILogService 서비스가 요청되었습니다.");
                service = new SelectCTILogService(jsonMessageFromISAC);
                break;
            case "ManageStationService":
                logger.info("ManageStationService 서비스가 요청되었습니다.");
                service = new ManageStationService(jsonMessageFromISAC);
                break;
            default:
                String errorMessage = MessageFormat.format("서비스 분기에 허용되지 않는 serviceType 파라미터 값이 요청되었습니다.({0})", requestType);
                logger.error(errorMessage);
                Map<String, Object> response = new HashMap<>();
                response.put("requestResult", "failure");
                response.put("errorMessage", errorMessage);
                String jsonResponse = new Gson().toJson(response);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse);
        }

        try {
            service.startProcess();
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("requestResult", "failure");
            response.put("errorMessage", e.getMessage());
            String jsonResponse = new Gson().toJson(response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        }

        logger.info("성공적으로 서비스 처리를 완료하였습니다.");
        logger.info(MessageFormat.format("결과 코드 : {0}", service.getResultCode()));

        /* ISAC에게 결과 코드 반환 */
        Map<String, Object> response = new HashMap<>();
        response.put("requestResult", "success");
        response.put("result", service.getResultCode());
        String jsonResponse = new Gson().toJson(response);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }
}
