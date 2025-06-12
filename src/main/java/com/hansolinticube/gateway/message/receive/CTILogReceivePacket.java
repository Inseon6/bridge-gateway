package com.hansolinticube.gateway.message.receive;

public class CTILogReceivePacket extends ReceivePacket {
    /* 원본 문자열 */
    private final String stringMessage;

    /* Header */
    private final String packetKey;           // 8 byte
    private final String packetLength;        // 4 byte
    private final String resultCode;          // 5 byte
    private final String serviceKey;          // 8 byte
    private final String serviceCode;         // 3 byte
    private final String timeKey;             // 1 byte
    private final String time;                // 14 byte
    private final String centerKey;           // 1 byte
    private final String center;              // 10 byte
    private final String serverIdKey;         // 1 byte
    private final String serverId;            // 10 byte
    private final String primaryKey;          // 1 byte
    private final String primary;             // 25 byte
    private final String headerReserved;      // 9 byte

    /* Body */
    private final String initDateKey;         // 1 byte
    private final String init;                // 20 byte
    private final String clearDateKey;        // 1 byte
    private final String clear;               // 20 byte
//    private final String count;               // 2 byte
//    private final String callId;              // 5*count byte

    public CTILogReceivePacket(String stringMessage) {
        this.stringMessage = stringMessage;
        this.packetKey = stringMessage.substring(0,8);
        this.packetLength = stringMessage.substring(8,12);
        this.resultCode = stringMessage.substring(12, 17);
        this.serviceKey = stringMessage.substring(17, 25);
        this.serviceCode = stringMessage.substring(25, 28);
        this.timeKey = stringMessage.substring(28, 29);
        this.time = stringMessage.substring(29, 43);
        this.centerKey = stringMessage.substring(43, 44);
        this.center = stringMessage.substring(44, 54);
        this.serverIdKey = stringMessage.substring(54, 55);
        this.serverId = stringMessage.substring(55, 65);
        this.primaryKey = stringMessage.substring(65, 66);
        this.primary = stringMessage.substring(66, 91);
        this.headerReserved = stringMessage.substring(91, 100);
        this.initDateKey = stringMessage.substring(100, 101);
        this.init = stringMessage.substring(101, 121);
        this.clearDateKey = stringMessage.substring(121, 122);
        this.clear = stringMessage.substring(122, 142);
//        this.count = stringMessage.substring(142, 144);
//        String replacedCount = this.count.trim();
//        this.callId = Integer.parseInt(replacedCount) * 5 == 0 ? null : stringMessage.substring(144, 144+(Integer.parseInt(replacedCount)*5));
    }

    @Override
    public String toString() {
        return this.stringMessage;
    }

    public String getAsString() {
        return this.stringMessage;
    }

//    public String getCallId() {
//        return callId;
//    }

    public String getCenter() {
        return center;
    }

    public String getCenterKey() {
        return centerKey;
    }

    public String getClear() {
        return clear;
    }

    public String getPacketKey() {
        return packetKey;
    }

    public String getPacketLength() {
        return packetLength;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getTimeKey() {
        return timeKey;
    }

    public String getTime() {
        return time;
    }

    public String getServerIdKey() {
        return serverIdKey;
    }

    public String getServerId() {
        return serverId;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getPrimary() {
        return primary;
    }

    public String getHeaderReserved() {
        return headerReserved;
    }

    public String getInitDateKey() {
        return initDateKey;
    }

    public String getInit() {
        return init;
    }

    public String getClearDateKey() {
        return clearDateKey;
    }
}
