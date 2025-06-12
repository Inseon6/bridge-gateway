package com.hansolinticube.gateway.message.receive;

public class VDNManageReceivePacket extends ReceivePacket {
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
    private final String extraKey;            // 1 byte
    private final String extra;               // 10 byte
    private final String headerReserved;      // 24 byte

    /* Body */
    private final String dbTableKey;          // 1 byte
    private final String dbChangedTableName;  // 20 byte
    private final String bodyReserved;        // 21 byte

    /**
     * 생성자
     * @param stringMessage CTIBridge로부터 얻은 String 메시지
     */
    public VDNManageReceivePacket(String stringMessage) {
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
        this.extraKey = stringMessage.substring(65, 66);
        this.extra = stringMessage.substring(66, 76);
        this.headerReserved = stringMessage.substring(76, 100);
        this.dbTableKey = stringMessage.substring(100, 101);
        this.dbChangedTableName = stringMessage.substring(101, 121);
        this.bodyReserved = stringMessage.substring(121,142);
    }

    @Override
    public String toString() {
        return this.stringMessage;
    }

    public String getPacketKey() {
        return this.packetKey;
    }

    public String getPacketLength() {
        return this.packetLength;
    }

    @Override
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

    public String getTime() {
        return this.time;
    }

    public String getCenterKey() {
        return this.centerKey;
    }

    public String getCenter() {
        return this.center;
    }

    public String getServerIdKey() {
        return this.serverIdKey;
    }

    public String getServerId() {
        return this.serverId;
    }

    public String getExtraKey() {
        return this.extraKey;
    }

    public String getExtra() {
        return this.extra;
    }

    public String getHeaderReserved() {
        return this.headerReserved;
    }

    public String getDbTableKey() {
        return this.dbTableKey;
    }

    public String getDbChangedTableName() {
        return this.dbChangedTableName;
    }

    public String getBodyReserved() {
        return bodyReserved;
    }
}
