package com.hansolinticube.gateway.service;

import com.google.gson.JsonObject;
import com.hansolinticube.gateway.message.send.StationManageSendPacket;
import com.hansolinticube.gateway.time.CurrentDateTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManageStationService extends ServiceType {
    final static Logger logger = LogManager.getLogger(ManageStationService.class);

    public ManageStationService(JsonObject jsonMessageFromIsac) {
        super(jsonMessageFromIsac);
        this.serviceType = "ManageStationService";
    }

    @Override
    public void retrieveDataParameters() throws Exception {
        ArrayList<String> nullList = new ArrayList<>();
        String queryType = null;

        try {
            queryType = retrieveQueryType();
        } catch (NullPointerException e) {
            nullList.add("queryType");
        }

        if (queryType == null) {
            throw new Exception("쿼리 타입이 null 입니다.");
        }

        try {
            this.dbChangedTableName = retrieveDbChangedTableName();
        } catch (NullPointerException e) {
            nullList.add("dbChangedTableName");
        }

        try {
            this.centerCd = retireveCenterCd();
        } catch (NullPointerException e) {
            nullList.add("centerCd");
        }

        logger.info(MessageFormat.format("내선번호 " + queryType +" 요청에 대해서 필요한 파라미터 값을 추출하였습니다.\n" +
            "dbChangedTableName : {0}\n" +
            "centerCd : {1}", this.dbChangedTableName, this.centerCd));

        if (!nullList.isEmpty()) {
            throw new NullPointerException(MessageFormat.format("다음 파라미터들이 존재하지 않아서 NullPointerException이 발생하였습니다.({0})", nullList.toString()));
        }
    }

    @Override
    public void setAgentIpAndPort() {
        JsonObject serverJson = gatewayInitializer.getServerJson();

        this.primaryAgentIp = serverJson.getAsJsonObject(this.centerCd).getAsJsonObject("CTI").getAsJsonObject("01").get("IP").getAsString();
        this.primaryAgentPort = serverJson.getAsJsonObject(this.centerCd).getAsJsonObject("CTI").getAsJsonObject("01").get("PORT").getAsInt();
        this.primaryAgentRetry = serverJson.getAsJsonObject(this.centerCd).getAsJsonObject("CTI").getAsJsonObject("01").get("RETRY").getAsInt();
        this.backupAgentIp = serverJson.getAsJsonObject(this.centerCd).getAsJsonObject("CTI").getAsJsonObject("02").get("IP").getAsString();
        this.backupAgentPort = serverJson.getAsJsonObject(this.centerCd).getAsJsonObject("CTI").getAsJsonObject("02").get("PORT").getAsInt();
        this.backupAgentRetry = serverJson.getAsJsonObject(this.centerCd).getAsJsonObject("CTI").getAsJsonObject("02").get("RETRY").getAsInt();
    }

    @Override
    public void createPacket() {
        StationManageSendPacket stationManageSendPacket = new StationManageSendPacket();

        stationManageSendPacket.setPacketKey(padString("DBM_SERV", " ", 8));
        stationManageSendPacket.setPacketLength(padString("0142", " ", 4));
        stationManageSendPacket.setResultCode(padString("", " ", 5));
        stationManageSendPacket.setServiceKey(padString("D_CNGSTA", " ", 8));
        stationManageSendPacket.setServiceCode(padString("803", " ", 3));
        stationManageSendPacket.setTimeKey(padString("m", " ", 1));
        stationManageSendPacket.setTime(padString(CurrentDateTime.getCurrentDateTime(), " ", 14));
        stationManageSendPacket.setCenterKey(padString("C", " ", 1));
        stationManageSendPacket.setCenter(padString(this.centerCd, " ", 10));
        stationManageSendPacket.setServerIdKey(padString("I", " ", 1));
        stationManageSendPacket.setServerId(padString("", " ", 10));
        stationManageSendPacket.setExtraKey(padString("e", " ", 1));
        stationManageSendPacket.setExtra(padString("", " ", 10));
        stationManageSendPacket.setAgentReserved(padString("", " ", 24));
        stationManageSendPacket.setDbTableKey(padString("T", " ", 1));
        stationManageSendPacket.setDbChangedTableName(padString(this.dbChangedTableName, " ", 20));
        stationManageSendPacket.setReserved(padString("", " ", 21));

        this.sendPacket = stationManageSendPacket;
    }

    private String retrieveQueryType() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("queries").get("type").getAsString();
    }

    private String retrieveDbChangedTableName() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("dbChangedTableName").getAsString();
    }

    private String retireveCenterCd() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("centerCd").getAsString();
    }

}
