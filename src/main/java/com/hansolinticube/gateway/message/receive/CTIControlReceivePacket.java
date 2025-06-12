package com.hansolinticube.gateway.message.receive;

public class CTIControlReceivePacket extends ReceivePacket {
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
    private final String controlKey;          // 1 byte
    private final String control;             // 20 byte

    public CTIControlReceivePacket(String stringMessage) {
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
        this.controlKey = stringMessage.substring(121, 122);
        this.control = stringMessage.substring(122, 142);
    }

    @Override
    public String toString() {
        return this.stringMessage;
    }

    public String getAsString() {
        return this.stringMessage;
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

    public String getCenterKey() {
        return centerKey;
    }

    public String getCenter() {
        return center;
    }

    public String getServerIdKey() {
        return serverIdKey;
    }

    public String getServerId() {
        return serverId;
    }

    public String getExtraKey() {
        return extraKey;
    }

    public String getExtra() {
        return extra;
    }

    public String getHeaderReserved() {
        return headerReserved;
    }

    public String getDbTableKey() {
        return dbTableKey;
    }

    public String getDbChangedTableName() {
        return dbChangedTableName;
    }

    public String getControlKey() {
        return controlKey;
    }

    public String getControl() {
        return control;
    }
}
