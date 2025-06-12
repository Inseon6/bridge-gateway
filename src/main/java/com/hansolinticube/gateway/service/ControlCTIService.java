package com.hansolinticube.gateway.service;

import com.google.gson.JsonObject;
import com.hansolinticube.gateway.message.send.CTIControlSendPacket;
import com.hansolinticube.gateway.time.CurrentDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ControlCTIService extends ServiceType {
    final static Logger logger = LogManager.getLogger(ControlCTIService.class);

    public ControlCTIService(JsonObject jsonMessageFromIsac) {
        super(jsonMessageFromIsac);
        this.serviceType = "ControlCTIService";
    }

    @Override
    public void retrieveDataParameters() throws NullPointerException {
        ArrayList<String> nullList = new ArrayList<>();

        try {
            this.serverCd = retrieveServerCd();
        } catch (NullPointerException e) {
            nullList.add("serverCd");
        }

        try {
            this.dbChangedTableName = retrieveDbChangedTableName();
        } catch (NullPointerException e) {
            nullList.add("dbChangedTableName");
        }

        try {
            this.controlType = retrieveControlType();
        } catch (NullPointerException e) {
            nullList.add("controlType");
        }

        if (!nullList.isEmpty()) {
            throw new NullPointerException(MessageFormat.format("다음 파라미터들이 존재하지 않아서 NullPointerException이 발생하였습니다.({0})", nullList.toString()));
        }

        logger.info(MessageFormat.format("CTI 제어 요청에 대해서 파라미터 값을 추출하였습니다.\n" +
                "serverIp : {0}\n" +
                "dbChangedTableName : {1}\n" +
                "controlType : {2}", this.serverCd, this.dbChangedTableName, this.controlType));
    }

    @Override
    public void setAgentIpAndPort() {
        /*
         * 예를 들면, serverCd에 SC-CTI-01이 담겨서 온다. 이것을
         * '-'를 기준으로 분리하면 배열에 담기어 ['SC', 'CTI', '01']로 반환된다.
         * 다만, ControlCTIService, SelectCTILogService는 Primary에만 요청을 하고 끝난다.
         */

        JsonObject serverJson = gatewayInitializer.getServerJson();

        String[] serverCdSplit = this.serverCd.split("-");
        String center = serverCdSplit[0];
        String cti = serverCdSplit[1];
        String number = serverCdSplit[2];

        this.primaryAgentIp = serverJson.getAsJsonObject(center).getAsJsonObject(cti).getAsJsonObject(number).get("IP").getAsString();
        this.primaryAgentPort = serverJson.getAsJsonObject(center).getAsJsonObject(cti).getAsJsonObject(number).get("PORT").getAsInt();
        this.primaryAgentRetry = serverJson.getAsJsonObject(center).getAsJsonObject(cti).getAsJsonObject(number).get("RETRY").getAsInt();
    }

    @Override
    public void createPacket() {
        CTIControlSendPacket ctiServiceSend = new CTIControlSendPacket();

        ctiServiceSend.setPacketKey(this.padString("DBM_SERV", " ", 8));
        ctiServiceSend.setPacketLength(this.padString("0142", " ", 4));
        ctiServiceSend.setResultCode(this.padString("", " ", 5));
        ctiServiceSend.setServiceKey(this.padString("D_CNGSER", " ", 8));
        ctiServiceSend.setServiceCode(this.padString("802", " ", 3));
        ctiServiceSend.setTimeKey(this.padString("m", " ", 1));
        ctiServiceSend.setTime(padString(CurrentDateTime.getCurrentDateTime(), " ", 14));
        ctiServiceSend.setCenterKey(padString("C", " ", 1));
        ctiServiceSend.setCenter(padString("", " ", 10));
        ctiServiceSend.setServerIdKey(padString("I", " ", 1));
        ctiServiceSend.setServerId(padString(this.serverCd, " ", 10));
        ctiServiceSend.setExtraKey(padString("e", " ", 1));
        ctiServiceSend.setExtra(padString("", " ", 10));
        ctiServiceSend.setAgentReserved(padString("", " ", 24));
        ctiServiceSend.setDbTableKey(padString("T", " ", 1));
        ctiServiceSend.setDbChangedTableName(padString(this.dbChangedTableName, " ", 20));
        ctiServiceSend.setControlKey(padString("C", " ", 1));
        ctiServiceSend.setControl(padString(this.controlType, " ", 20));

        this.sendPacket = ctiServiceSend;
    }

    private String retrieveServerCd() throws NullPointerException {
            return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("serverCd").getAsString();
    }

    private String retrieveDbChangedTableName() throws NullPointerException {
            return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("dbChangedTableName").getAsString();
    }

    private String retrieveControlType() throws NullPointerException {
            return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("controlType").getAsString();
    }

}
