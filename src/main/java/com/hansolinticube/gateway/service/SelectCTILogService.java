package com.hansolinticube.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hansolinticube.gateway.message.send.CTILogSendPacket;
import com.hansolinticube.gateway.time.CurrentDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SelectCTILogService extends ServiceType {
    final static Logger logger = LogManager.getLogger(SelectCTILogService.class);

    public SelectCTILogService(JsonObject jsonMessageFromIsac) {
        super(jsonMessageFromIsac);
        this.serviceType = "SelectCTILogService";
    }

    @Override
    public void retrieveDataParameters() throws NullPointerException {
        ArrayList<String> nullList = new ArrayList<>();
        try {
            this.serverId = retrieveServerId();
        } catch (NullPointerException e) {
            nullList.add("serverId");
        }

        try {
            this.uniqueCallId = retrieveUniqueCallId();
        } catch (NullPointerException e) {
            nullList.add("uniqueCallId");
        }

        try {
            this.callInitDate = retrieveCallInitDate();
        } catch (NullPointerException e) {
            nullList.add("callInitDate");
        }

        try {
            this.callClearDate = retrieveCallClearDate();
        } catch (NullPointerException e) {
            nullList.add("callClearDate");
        }

        try {
            this.callIdList = retrieveCallId();
            this.countCallList = retrieveCountCallId();
        } catch (NullPointerException | JsonProcessingException e) {
            nullList.add("callIdList");
        }

        if (!nullList.isEmpty()) {
            throw new NullPointerException(MessageFormat.format("다음 파라미터들이 존재하지 않아서 NullPointerException이 발생하였습니다.({0})", nullList.toString()));
        }

        logger.info(MessageFormat.format("CTI 로그 조회 요청에 대해서 파라미터 값을 추출하였습니다.\n" +
                "serverId : {0}\n" +
                "uniqueCallId : {1}\n" +
                "callInitDate : {2}\n" +
                "callClearDate : {3}\n" +
                "callIdList : {4}\n" +
                "countCallList: {5}",
                this.serverId,
                this.uniqueCallId,
                this.callInitDate,
                this.callClearDate,
                this.callIdList,
                this.countCallList));
    }

    @Override
    public void setAgentIpAndPort() {
        /*
         * 예를 들면, serverCd에 SC-CTI-01이 담겨서 온다. 이것을
         * '-'를 기준으로 분리하면 배열에 담기어 ['SC', 'CTI', '01']로 반환된다.
         * 다만, ControlCTIService, SelectCTILogService는 Primary에만 요청을 하고 끝난다.
         */

        JsonObject serverJson = gatewayInitializer.getServerJson();

        String[] serverIdSplit = this.serverId.split("-");
        String center = serverIdSplit[0];
        String cti = serverIdSplit[1];
        String number = serverIdSplit[2];

        this.primaryAgentIp = serverJson.getAsJsonObject(center).getAsJsonObject(cti).getAsJsonObject(number).get("IP").getAsString();
        this.primaryAgentPort = serverJson.getAsJsonObject(center).getAsJsonObject(cti).getAsJsonObject(number).get("PORT").getAsInt();
        this.primaryAgentRetry = serverJson.getAsJsonObject(center).getAsJsonObject(cti).getAsJsonObject(number).get("RETRY").getAsInt();
    }

    @Override
    public void createPacket() {
        CTILogSendPacket CTILogSendPacket = new CTILogSendPacket();

        CTILogSendPacket.setPacketKey(this.padString("DBM_SERV", " ", 8));
        CTILogSendPacket.setResultCode(this.padString("", " ", 5));
        CTILogSendPacket.setServiceKey(this.padString("D_SH_LOG", " ", 8));
        CTILogSendPacket.setServiceCode(this.padString("811", " ", 3));
        CTILogSendPacket.setTimeKey(this.padString("m", " ", 1));
        CTILogSendPacket.setTime(padString(CurrentDateTime.getCurrentDateTime(), " ", 14));
        CTILogSendPacket.setCenterKey(padString("C", " ", 1));
        CTILogSendPacket.setCenter(padString("", " ", 10));
        CTILogSendPacket.setServerIdKey(padString("I", " ", 1));
        CTILogSendPacket.setServerId(padString(this.serverId, " ", 10));
        CTILogSendPacket.setPrimaryKey(padString("P", " ", 1));
        CTILogSendPacket.setPrimary(padString(this.uniqueCallId, " ", 25));
        CTILogSendPacket.setAgentReserved(padString("", " ", 9));
        CTILogSendPacket.setInitDateKey(padString("i", " ", 1));
        CTILogSendPacket.setInit(padString(this.callInitDate, " ", 20));
        CTILogSendPacket.setClearDateKey(padString("c", " ", 1));
        CTILogSendPacket.setClear(padString(this.callClearDate, " ", 20));
        CTILogSendPacket.setCount(padString(this.countCallList, " ", 2));
        CTILogSendPacket.setCallId(this.callIdList);

        // 구성된 패킷 길이에 packetLength 문자열 길이인 4를 더해서 최종 패킷 길이를 산출
        CTILogSendPacket.setPacketLength(this.padString(String.valueOf(CTILogSendPacket.getSendPacketLength() + 4), " ", 4));

        this.sendPacket = CTILogSendPacket;
    }

    private String retrieveServerId() {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("serverId").getAsString();
    }

    private String retrieveUniqueCallId() {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("uniqueCallId").getAsString();
    }

    private String retrieveCallInitDate() {
        String inputDate = this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("callInitDate").getAsString();

        // 입력 문자열의 날짜 형식
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        // 출력 문자열의 날짜 형식
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");


        Date date = null;
        try {
            date = inputDateFormat.parse(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return outputDateFormat.format(date);
    }

    private String retrieveCallClearDate() {
        String inputDate = this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("callClearDate").getAsString();

        // 입력 문자열의 날짜 형식
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        // 출력 문자열의 날짜 형식
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        Date date = null;
        try {
            date = inputDateFormat.parse(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return outputDateFormat.format(date);
    }

    private String retrieveCountCallId() throws JsonProcessingException {
        JsonArray jsonArray = this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").getAsJsonObject("callIdList").get("list").getAsJsonArray();

        // 중복 제거를 위해 Set에 값 추가
        Set<String> uniqueValues = new HashSet<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            uniqueValues.add(jsonObject.get("callId").getAsString());
        }

        // 중복 제거된 값들의 카운트
        return String.valueOf(uniqueValues.size());
    }

    private String retrieveCallId() throws JsonProcessingException {
        JsonArray jsonArray = this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").getAsJsonObject("callIdList").get("list").getAsJsonArray();

        // 중복 제거를 위해 Set에 값 추가
        Set<String> uniqueValues = new HashSet<>();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            uniqueValues.add(jsonObject.get("callId").getAsString());
        }

        // 중복 제거된 값을 하나의 문자열로 합치기
        StringBuilder result = new StringBuilder();
        for (String value : uniqueValues) {
            result.append(value);
        }

        return String.valueOf(result);
    }
}
