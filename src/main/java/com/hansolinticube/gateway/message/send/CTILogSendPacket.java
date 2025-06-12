package com.hansolinticube.gateway.message.send;

import java.text.MessageFormat;

public class CTILogSendPacket extends SendPacket {
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
    private String primaryKey;          // 1 byte
    private String primary;             // 25 byte
    private String agentReserved;       // 9 byte

    /* Body */
    private String initDateKey;         // 1 byte
    private String init;                // 20 byte
    private String clearDateKey;        // 1 byte
    private String clear;               // 20 byte
    private String count;               // 2 byte
    private String callId;              // 5*count byte

    public CTILogSendPacket() {}

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

    public String getPrimaryKey() {
        return this.primaryKey;
    }

    public String getPrimary() {
        return this.primary;
    }

    public String getAgentReserved() {
        return this.agentReserved;
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

    public String count() {
        return this.count;
    }

    public String callId() {
        return this.callId;
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

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public void setAgentReserved(String agentReserved) {
        this.agentReserved = agentReserved;
    }

    public void setInitDateKey(String initDateKey) {
        this.initDateKey = initDateKey;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public void setClearDateKey(String clearDateKey) {
        this.clearDateKey = clearDateKey;
    }

    public void setClear(String clear) {
        this.clear = clear;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setCallId(String callId){
        this.callId = callId;
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

    @Override
    public String getSendPacketAsString() {
        if (Integer.parseInt(this.count.trim()) == 0) {
            return MessageFormat.format("{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}{17}{18}",
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
                    this.primaryKey,
                    this.primary,
                    this.agentReserved,
                    this.initDateKey,
                    this.init,
                    this.clearDateKey,
                    this.clear,
                    this.count);
        } else {
            return MessageFormat.format("{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}{17}{18}{19}",
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
                    this.primaryKey,
                    this.primary,
                    this.agentReserved,
                    this.initDateKey,
                    this.init,
                    this.clearDateKey,
                    this.clear,
                    this.count,
                    this.callId);
        }
    }

    public int getSendPacketLength() {
        int sendPacketLength = 0;
        sendPacketLength += this.packetKey == null ? 0 : this.packetKey.length();
        sendPacketLength += this.packetLength == null ? 0 : this.packetLength.length();
        sendPacketLength += this.resultCode == null ? 0 : this.resultCode.length();
        sendPacketLength += this.serviceKey == null ? 0 : this.serviceKey.length();
        sendPacketLength += this.serviceCode == null ? 0 : this.serviceCode.length();
        sendPacketLength += this.timeKey == null ? 0 : this.timeKey.length();
        sendPacketLength += this.time == null ? 0 : this.time.length();
        sendPacketLength += this.centerKey == null ? 0 : this.centerKey.length();
        sendPacketLength += this.center == null ? 0 : this.center.length();
        sendPacketLength += this.serverIdKey == null ? 0 : this.serverIdKey.length();
        sendPacketLength += this.serverId == null ? 0 : this.serverId.length();
        sendPacketLength += this.primaryKey == null ? 0 : this.primaryKey.length();
        sendPacketLength += this.primary == null ? 0 : this.primary.length();
        sendPacketLength += this.agentReserved == null ? 0 : this.agentReserved.length();
        sendPacketLength += this.initDateKey == null ? 0 : this.initDateKey.length();
        sendPacketLength += this.init == null ? 0 : this.init.length();
        sendPacketLength += this.clearDateKey == null ? 0 : this.clearDateKey.length();
        sendPacketLength += this.clear == null ? 0 : this.clear.length();
        sendPacketLength += this.count == null ? 0 : this.count.length();
        sendPacketLength += this.callId == null ? 0 : this.callId.length();

        return sendPacketLength;
    }
}
