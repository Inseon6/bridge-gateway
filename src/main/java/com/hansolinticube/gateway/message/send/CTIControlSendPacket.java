package com.hansolinticube.gateway.message.send;

import java.text.MessageFormat;

public class CTIControlSendPacket extends SendPacket {
    /* Header */
    private String packetKey;           // 8 byte
    private String packetLength;        // 4 byte
    private String resultCode;          // 5 byte
    private String serviceKey;          // 8 byte
    private String serviceCode;         // 3 byte
    private String timeKey;             // 1 byte
    private String time;                // 14 byte
    private String centerKey;           // 1 byte
    private String center;              // 10 byte
    private String serverIdKey;         // 1 byte
    private String serverId;            // 10 byte
    private String extraKey;            // 1 byte
    private String extra;               // 10 byte
    private String agentReserved;       // 24 byte

    /* Body */
    private String dbTableKey;          // 1 byte
    private String dbChangedTableName;  // 20 byte
    private String controlKey;          // 1 byte
    private String control;             // 20 byte

    public CTIControlSendPacket() {}

    public String getPacketKey() {
        return this.packetKey;
    }

    public String getPacketLength() {
        return this.packetLength;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public String getServiceKey() {
        return this.serviceKey;
    }

    public String getServiceCode() {
        return this.serviceCode;
    }

    public String getTimeKey() {
        return this.timeKey;
    }

    public String getCenterKey() {
        return this.centerKey;
    }

    public String getServerIdKey() {
        return this.serverIdKey;
    }

    public String getExtraKey() {
        return this.extraKey;
    }

    public String getExtra() {
        return this.extra;
    }

    public String getAgentReserved() {
        return this.agentReserved;
    }

    public String getDbTableKey() {
        return this.dbTableKey;
    }

    public String getTime() {
        return this.time;
    }

    public String getCenter() {
        return this.center;
    }

    public String getServerId() {
        return this.serverId;
    }

    public String getDbChangedTableName() {
        return this.dbChangedTableName;
    }

    public void setPacketKey(String packetKey) {
        this.packetKey = packetKey;
    }

    public void setPacketLength(String packetLength) {
        this.packetLength = packetLength;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public void setCenterKey(String centerKey) {
        this.centerKey = centerKey;
    }

    public void setServerIdKey(String serverIdKey) {
        this.serverIdKey = serverIdKey;
    }

    public void setExtraKey(String extraKey) {
        this.extraKey = extraKey;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void setAgentReserved(String agentReserved) {
        this.agentReserved = agentReserved;
    }

    public void setDbTableKey(String dbTableKey) {
        this.dbTableKey = dbTableKey;
    }

    public void setControlKey(String controlKey) {
        this.controlKey = controlKey;
    }

    public void setControl(String control) {
        this.control = control;
    }

    /**
     * 현재 시각
     * @param time YYYYMMDDHHMMSS
     */
    public void setTime(String time) {
        this.time = time;
    }

    public void setCenter(String center) {
        /* padding 값 추가하는 로직을 구현 */
        this.center = center;
    }

    public void setServerId(String serverId) {
        /* padding 값 추가하는 로직을 구현 */
        this.serverId = serverId;
    }

    public void setDbChangedTableName(String tableName) {
        /* padding 값 추가하는 로직을 구현 */
        this.dbChangedTableName = tableName;
    }

    @Override
    public String getSendPacketAsString() {
        return MessageFormat.format("{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}{17}",
                this.packetKey,
                this.packetLength,
                this.resultCode,
                this.serviceKey,
                this.serviceCode,
                this.timeKey,
                this.time,
                this.centerKey,
                this.center,
                this.serverIdKey,
                this.serverId,
                this.extraKey,
                this.extra,
                this.agentReserved,
                this.dbTableKey,
                this.dbChangedTableName,
                this.controlKey,
                this.control);
    }
}