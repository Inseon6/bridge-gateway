package com.hansolinticube.gateway.service;

import com.google.gson.JsonObject;
import com.hansolinticube.gateway.GatewayInitializer;
import com.hansolinticube.gateway.message.receive.CTIControlReceivePacket;
import com.hansolinticube.gateway.message.receive.CTILogReceivePacket;
import com.hansolinticube.gateway.message.receive.ReceivePacket;
import com.hansolinticube.gateway.message.receive.VDNManageReceivePacket;
import com.hansolinticube.gateway.message.send.SendPacket;
import com.hansolinticube.gateway.netty.NettyClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

/**
 * 추상화 클래스
 * @author  Hansol AICC개발2팀 주인선
 */
public abstract class ServiceType {
    final static Logger logger = LogManager.getLogger(ServiceType.class);
    GatewayInitializer gatewayInitializer = GatewayInitializer.getInstance();

    String serviceType;  // 서비스 타입
    JsonObject jsonMessageFromIsac;  // ISAC으로부터 받은 JSON 메시지
    SendPacket sendPacket;  // CTIBridge에게 전달할 패킷 객체
    ReceivePacket receivePacket;  // CTIBridge로부터 전달 받은 메시지를 내부 자료구조로 역직렬화
    String resultCode;  // CTIBridge로부터 전달받은 결과 코드

    /* CTIBridge에게 전달해야 할 파라미터 값 */
    String dbChangedTableName; // sChangeTBL
    String centerCd; // sCenter
    String serverCd; // sServerId(CTI 서비스 제어)
    String controlType; // sControl
    String serverId; // sServerId(CTI 로그 조회)
    String uniqueCallId; // sPrimary
    String callInitDate; // sInit
    String callClearDate; // sClear
    String countCallList;
    String callIdList; // sCallId

    /* CTIBridge Agent IP 및 Port 정보 */
    String primaryAgentIp;
    int primaryAgentPort;
    int primaryAgentRetry;
    String backupAgentIp;
    int backupAgentPort;
    int backupAgentRetry;

    public ServiceType(JsonObject jsonMessageFromIsac) {
        this.jsonMessageFromIsac = jsonMessageFromIsac;
    }

    /**
     * CTIBridge로부터 전달 받은 결과 코드를 반환한다.
     * @return 결과 코드
     */
    public String getResultCode() {
        return this.resultCode;
    }

    /**
     * CTIBridge가 요구하는 인터페이스 요구 사항을 충족시키기 위해서 사용하는 패딩 값 추가 메소드
     * @param target 패딩 값을 추가할 문자열
     * @param pad 패딩
     * @param digit 패딩 값 추가를 통해 맞춰주어야 할 길이
     * @return 패딩이 적용된 문자열
     */
    public String padString(String target, String pad, int digit) {
        int targetLength = target.length();
        if (targetLength == digit) {
            return target;
        } else if (targetLength < digit) {
            StringBuilder targetBuilder = new StringBuilder(target);
            for (int i = 0; i < digit - targetLength; i++) {
                targetBuilder.append(pad);
            }
            target = targetBuilder.toString();
            return target;
        } else {
            return target;
        }
    }

    /**
     * (저수준 모듈) 1. ISAC으로부터 받은 파라미터값 추출
     */
    public abstract void retrieveDataParameters() throws Exception;

    /**
     * (저수준 모듈) 2. 요청할 서버 IP와 PORT 추출
     */
    public abstract void setAgentIpAndPort();

    /**
     * (저수준 모듈) 3. 142byte 패킷 생성
     */
    public abstract void createPacket();

    /**
     * (저수준 모듈) 4. 연결 및 패킷 전송
     */
    public void notifyEvent() throws Exception {
        /* CTIBridge Primary 서버에 연결 시도 및 데이터 전송 */
        NettyClient nettyClient = new NettyClient(
                this.primaryAgentIp,
                this.primaryAgentPort,
                true,
                142
        );
        try {
            nettyClient.connect();
            logger.info(MessageFormat.format("CTIBridge Primary({0}:{1}) 서버에 연결되었습니다.",
                    this.primaryAgentIp,
                    this.primaryAgentPort));
        } catch (Exception e) {
            /*
             * CONNECT_TIMEOUT 또는 CONNEC_REFUSED 일 때, CTIBridge Backup 서버에 연결 시도
             * VDN 일 때만 C1 또는 C2 Backup 서버로 요청하기
             */
            if ("ManageVDNService".equals(this.serviceType) || "ManageStationService".equals(this.serviceType)) {
                logger.error(MessageFormat.format("CTIBridge Primary({0}:{1}) 서버에 연결 요청을 하였지만 에러가 발생하였습니다.({2})",
                        this.primaryAgentIp,
                        this.primaryAgentPort,
                        e.getMessage()));
                logger.info(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 연결을 다시 시도합니다.",
                        this.backupAgentIp,
                        this.backupAgentPort));
                nettyClient = new NettyClient(
                        this.backupAgentIp,
                        this.backupAgentPort,
                        false,
                        Integer.parseInt(sendPacket.getPacketLength().trim())
                );
                try {
                    nettyClient.connect();
                    logger.info(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 연결되었습니다.",
                            this.backupAgentIp,
                            this.backupAgentPort));
                } catch (Exception e2) {
                    throw new Exception(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 연결 요청을 하였지만 에러가 발생하였습니다. 요청 처리를 중단합니다.({2})",
                            this.backupAgentIp,
                            this.backupAgentPort,
                            e2.getMessage()));
                }
            } else {
                throw new Exception(e.getMessage());
            }
//
        }
        
        /* 패킷 전송 */
        nettyClient.sendData(sendPacket.getSendPacketAsString());

        logger.info("CTIBridge 서버에 패킷을 전송하였습니다.");
        logger.info(MessageFormat.format("전송한 패킷 : {0}", sendPacket.getSendPacketAsString()));
        logger.info("CTIBridge 서버로부터 응답 데이터를 수신할 때까지 대기합니다.");

        long mnTimeout = 20000; // 응답을 받을 때까지 대기할 시간: 20초
        long startTime = System.currentTimeMillis();

        /* 응답을 받을 때까지 대기 */
        while (nettyClient.getSessionStatus()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            /* 응답 대기시간이 20초 이상이 되었고, 서비스타입이 ManageVDNService 일 때만 CTIBridge Backup 서버로 요청*/
            if ((elapsedTime >= mnTimeout && "ManageVDNService".equals(this.serviceType)) || (elapsedTime >= mnTimeout && "ManageStationService".equals(this.serviceType))) {
                nettyClient.close();

                /* CTIBridge Primary는 요청했지만 CONNECT_TIMEOUT 또는 CONNECT_REFUSED이고, Backup 서버 모두 요청했지만 READ_TIMEOUT */
                if (!nettyClient.isPrimary()) {
                    String errorMessage = MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 데이터를 전송하고 20초를 기다렸지만 초과하여 요청 처리를 중단합니다.",
                            this.backupAgentIp,
                            this.backupAgentPort);
                    throw new Exception(errorMessage);
                }

                /* CTIBridge Primary가 READ_TIMEOUT 시에, CTIBridge Backup 서버에 연결 시도 */
                logger.info(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 연결을 시도합니다.",
                        this.backupAgentIp,
                        this.backupAgentPort));
                nettyClient = new NettyClient(
                    this.backupAgentIp,
                    this.backupAgentPort,
                        false,
                        Integer.parseInt(sendPacket.getPacketLength().trim())
                );
                try {
                    nettyClient.connect();
                    logger.info(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 연결되었습니다.",
                            this.backupAgentIp,
                            this.backupAgentPort));
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }

                /* CTIBridge Backup 서버에 패킷 전송 */
                nettyClient.sendData(sendPacket.getSendPacketAsString());
                logger.info(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버에 패킷을 전송하였습니다.\n" +
                        "전송한 패킷 : {2}",
                        this.backupAgentIp,
                        this.backupAgentPort,
                        sendPacket.getSendPacketAsString()));
                logger.info(MessageFormat.format("CTIBridge Backup({0}:{1}) 서버로부터 응답 데이터를 수신할 때까지 대기합니다.",
                        this.backupAgentIp,
                        this.backupAgentPort));
                long startTimeVDN = System.currentTimeMillis();
                while (nettyClient.getSessionStatus()) {
                    long elapsedTimeVDN = System.currentTimeMillis() - startTimeVDN;
                    if ( elapsedTimeVDN >= mnTimeout ) {
                        nettyClient.close();
                        throw new Exception(MessageFormat.format("CTIBridge Primary({0}:{1}) 서버와, CTIBridge Backup({2}:{3}) 서버에 요청했지만 " +
                                                                        "모두 응답 시간 20초를 초과하여 연결을 종료합니다.",
                                this.primaryAgentIp,
                                this.primaryAgentPort,
                                this.backupAgentIp,
                                this.backupAgentPort));
                    }
                }
            } else if (elapsedTime >= mnTimeout) {
                nettyClient.close();
                throw new Exception ("CTIBridge Agent에게 요청했지만 응답 시간 20초를 초과하여 연결을 종료합니다.");
            }
        }

        /* NettyClient의 dataFromCTIBridge가 null이라면 예외 처리 */
        if (nettyClient.getDataFromCTIBridge() == null) {
            String errorMessage = "CTIBridge로부터 받은 데이터가 null 이거나 비정상적으로 연결이 종료되어 데이터를 수신하지 못 하였습니다. 서버 관리자에게 문의하세요.";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }

        /* CTIBridge 서버로부터 수신한 데이터를 내부 자료구조로 역직렬화 */
        String messageFromCTIBridge = nettyClient.getDataFromCTIBridge();
        String serviceKey = messageFromCTIBridge.substring(17, 25);
        switch (serviceKey) {
            case "D_SH_LOG":
                receivePacket = new CTILogReceivePacket(messageFromCTIBridge);
                break;
            case "D_CNGSER":
                receivePacket = new CTIControlReceivePacket(messageFromCTIBridge);
                break;
            case "D_CNGVDN":
            default:
                receivePacket = new VDNManageReceivePacket(messageFromCTIBridge);
                break;
        }
        logger.info("CTIBridge로부터 패킷을 전송받았습니다.");
        logger.info(receivePacket.toString());
        this.resultCode = receivePacket.getResultCode();
    }

    /**
     * (고수준 모듈) 흐름 제어 메소드
     */
    public void startProcess() throws Exception {
        /* ISAC로부터 받은 데이터로부터 필요한 파라미터 값을 추출 */
        retrieveDataParameters();

        /* Agent Primary IP and Port */
        setAgentIpAndPort();

        /* CTIBridge Agent에게 보낼 패킷을 생성 */
        createPacket();

        /* CTIBridge Agent에게 이벤트 전달 */
        try {
            notifyEvent();
        } catch (Exception e) {
            throw new Exception(MessageFormat.format("CTIBridge Agent에게 이벤트 전달하는 도중 에러가 발생하였습니다.({0})", e.getMessage()));
        }
    }
}
