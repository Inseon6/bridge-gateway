/**
 * Name : Bridge Gateway
 * Company : Hansol Inticube
 * Written By : Ju In-Seon
 * JDK : 1.8
 * Application Type : Gateway between ISAC WAS Back-End and Bridge Agent
 */

package com.hansolinticube.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
	final static Logger logger = LogManager.getLogger(Main.class);

	/**
	 * Main 흐름 제어 메소드
	 */
	public static void main(String[] args) {
		logger.info("\n" +
				"    ____       _     __                          \n" +
				"   / __ )_____(_)___/ /___ ____                  \n" +
				"  / __  / ___/ / __  / __ `/ _ \\                \n" +
				" / /_/ / /  / / /_/ / /_/ /  __/                 \n" +
				"/________  /_/\\__,_/\\__, /\\___/               \n" +
				"  / ____/___ _/ /_______/    ______ ___  __      \n" +
				" / / __/ __ `/ __/ _ \\ | /| / / __ `/ / / /     \n" +
				"/ /_/ / /_/ / /_/  __/ |/ |/ / /_/ / /_/ /       \n" +
				"\\____/\\__,_/\\__/\\___/|__/|__/\\__,_/\\__, /  \n" +
				"                                  /____/         \n" +
				":: Written by Hansol Inticube                    \n" +
				":: Hansol Inticube Solution Prod Release v1.0.0");

		/* Spring Boot Web 실행 */
		SpringApplication.run(Main.class, args);
		logger.info("Spring Boot Web 서버가 성공적으로 실행되었습니다.");

		/* Bridge Gateway 이니셜라이저 실행 */
		GatewayInitializer.run();
		logger.info("이벤트 리스너가 성공적으로 실행되었습니다.");
	}

}
