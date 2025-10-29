package ecnu.edu.iotbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("ecnu.edu.iotbackend.mapper")
public class IotBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotBackendApplication.class, args);
    }

}
