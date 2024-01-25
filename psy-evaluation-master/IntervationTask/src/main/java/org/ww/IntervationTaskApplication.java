package org.ww;

/**
 * @Author lqk
 * @Date 2022/2/10 22:01
 * @Version 1.0
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class IntervationTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntervationTaskApplication.class, args);
    }

}
