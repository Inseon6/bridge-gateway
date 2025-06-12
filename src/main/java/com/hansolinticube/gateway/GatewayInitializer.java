package com.hansolinticube.gateway;

import com.google.gson.JsonObject;
import com.hansolinticube.gateway.utility.IniLoader;
import com.hansolinticube.gateway.utility.JsonLoader;
import com.hansolinticube.gateway.netty.NettyClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

import static java.lang.System.exit;

/**
 * 싱글톤 패턴
 * 목적 : 해당 클래스는 이벤트 리스너 초기화 과정을 담당 및 필요한 설정 데이터를 내부 자료구조에 보존한다.
 */
public class GatewayInitializer {
    final static Logger logger = LogManager.getLogger(GatewayInitializer.class);
    public static GatewayInitializer gatewayInitializer = new GatewayInitializer();

    private final String ctiBridgeIpPrimary;
    private final int ctiBridgePortPrimary;
    private final String ctiBridgeIpBackup;
    private final int ctiBridgePortBackup;

    private JsonObject serverJson;

    private GatewayInitializer() {
        /* system.ini 파일 로드 */
        Ini systemIni = null;
        try {
            systemIni = IniLoader.getIni("etc/system.ini");
            logger.info("system.ini 파일을 정상적으로 로드하였습니다.");
        } catch (IOException e) {
            logger.error(MessageFormat.format("system.ini 파일을 로드하는 도중 예외가 발생하였습니다.({0})", e.getMessage()));
            exit(1);
        }

        this.ctiBridgeIpPrimary = systemIni.get("ctibridge_primary", "ip");
        this.ctiBridgePortPrimary = Integer.parseInt(systemIni.get("ctibridge_primary", "port"));
        this.ctiBridgeIpBackup = systemIni.get("ctibridge_backup", "ip");
        this.ctiBridgePortBackup = Integer.parseInt(systemIni.get("ctibridge_backup", "port"));

        /* server.json 파일 로드 */
        try {
            this.serverJson = JsonLoader.getJsonObject("etc/server.json");
            logger.info("server.json 파일을 정상적으로 로드하였습니다.");
        } catch (FileNotFoundException e) {
            logger.error(MessageFormat.format("server.json 파일을 로드하는 도중 예외가 발생하였습니다.({0})", e.getMessage()));
            exit(1);
        }
    }

    public static GatewayInitializer getInstance() {
        return gatewayInitializer;
    }

    public JsonObject getServerJson() {
        return this.serverJson;
    }

    /**
     * 리스너 초기화 과정 수행
     */
    public void startInitProcess() {
        /* CTIBridge Primary 연동 테스트 */
        NettyClient nettyClient = new NettyClient(
                this.ctiBridgeIpPrimary,
                this.ctiBridgePortPrimary,
                true,
                1
        );

        logger.info("CTIBridge 서버와의 연동 테스트를 시작합니다.");

        try {
            nettyClient.connect();
            logger.info("CTIBridge Primary 서버와의 연동 테스트가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            nettyClient.close();
            logger.error("CTIBridge Primary 서버에 접속하는 도중 예외가 발생하였습니다. CTIBridge Backup 서버로 접속을 시도합니다.");
            /* CTIBridge Backup 연동 테스트 */
             nettyClient = new NettyClient(
                    this.ctiBridgeIpBackup,
                    this.ctiBridgePortBackup,
                     false,
                     1
            );
            try {
                nettyClient.connect();
                logger.info("CTIBridge Backup 서버와의 연동 테스트가 성공적으로 완료되었습니다.");
            } catch (Exception ex) {
                nettyClient.close();
                logger.error(MessageFormat.format("CTIBridge 백업 서버에 접속하는 도중 에러가 발생하였습니다. CTIBridge Primary 및 Backup 서버 상태를 확인하세요.({0})", e.getMessage()));
            }
        }
        nettyClient.close();
    }

    static public void run() {
        gatewayInitializer.startInitProcess();
    }
}
