package com.hansolinticube.gateway.service;

import com.google.gson.JsonObject;
import com.hansolinticube.gateway.message.send.VDNManageSendPacket;
import com.hansolinticube.gateway.time.CurrentDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ManageVDNService extends ServiceType {
    final static Logger logger = LogManager.getLogger(ManageVDNService.class);

    public ManageVDNService(JsonObject jsonMessageFromIsac) {
        super(jsonMessageFromIsac);
        this.serviceType = "ManageVDNService";
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
            throw new Exception("쿼리 타입이 D 또는 U가 아닌 null 입니다.");
        }

        switch (queryType) {
            case "D":
                logger.info("데이터 삭제(DELETE) 요청입니다.");
                try {
                    this.dbChangedTableName = retrieveDbChangedTableNameDelete();
                } catch (NullPointerException e) {
                    nullList.add("dbChangedTableName");
                }

                try {
                    this.centerCd = retireveCenterCdDelete();
                } catch (NullPointerException e) {
                    nullList.add("centerCd");
                }
                logger.info(MessageFormat.format("데이터 삭제(DELETE) 요청에 대해서 필요한 파라미터 값을 추출하였습니다.\n" +
                        "dbChangedTableName : {0}\n" +
                        "centerCd : {1}", this.dbChangedTableName, this.centerCd));
                break;
            case "U":
                logger.info("데이터 수정(INSERT, UPDATE) 요청입니다.");
                try {
                    this.dbChangedTableName = retireveDbChangedTableNameUpdate();
                } catch (NullPointerException e) {
                    nullList.add("dbChangedTableName");
                }
                try {
                    this.centerCd = retrieveCenterCdUpdate();
                } catch (NullPointerException e) {
                    nullList.add("centerCd");
                }
                logger.info(MessageFormat.format("데이터 수정(INSERT, UPDATE) 요청에 대해서 필요한 파라미터 값을 추출하였습니다.\n" +
                        "dbChangedTableName : {0}\n" +
                        "centerCd : {1}", this.dbChangedTableName, this.centerCd));
                break;
            default:
                throw new Exception(MessageFormat.format("쿼리 타입이 D 또는 U가 아닙니다.(쿼리 타입 : {0})", queryType));
        }
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
        VDNManageSendPacket vdnServiceSend = new VDNManageSendPacket();

        vdnServiceSend.setPacketKey(this.padString("DBM_SERV", " ", 8));
        vdnServiceSend.setPacketLength(this.padString("0142", " ", 4));
        vdnServiceSend.setResultCode(this.padString("", " ", 5));
        vdnServiceSend.setServiceKey(this.padString("D_CNGVDN", " ", 8));
        vdnServiceSend.setServiceCode(this.padString("801", " ", 3));
        vdnServiceSend.setTimeKey(this.padString("m", " ", 1));
        vdnServiceSend.setTime(padString(CurrentDateTime.getCurrentDateTime(), " ", 14));
        vdnServiceSend.setCenterKey(padString("C", " ", 1));
        vdnServiceSend.setCenter(padString(this.centerCd, " ", 10));
        vdnServiceSend.setServerIdKey(padString("I", " ", 1));
        vdnServiceSend.setServerId(padString("", " ", 10));
        vdnServiceSend.setExtraKey(padString("e", " ", 1));
        vdnServiceSend.setExtra(padString("", " ", 10));
        vdnServiceSend.setAgentReserved(padString("", " ", 24));
        vdnServiceSend.setDbTableKey(padString("T", " ", 1));
        vdnServiceSend.setDbChangedTableName(padString(this.dbChangedTableName, " ", 20));
        vdnServiceSend.setReserved(padString("", " ", 21));

        this.sendPacket = vdnServiceSend;
    }

    private String retrieveQueryType() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("queries").get("type").getAsString();
    }

    private String retrieveDbChangedTableNameDelete() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonArray("datasets").asList().get(0).getAsJsonObject().get("dbChangedTableName").getAsString();
    }

    private String retireveCenterCdDelete() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonArray("datasets").asList().get(0).getAsJsonObject().get("centerCd").getAsString();
    }

    private String retireveDbChangedTableNameUpdate() throws  NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("dbChangedTableName").getAsString();
    }

    private String retrieveCenterCdUpdate() throws NullPointerException {
        return this.jsonMessageFromIsac.getAsJsonObject("isacData").getAsJsonObject("arguments").get("centerCd").getAsString();
    }
}
