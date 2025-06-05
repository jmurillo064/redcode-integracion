package ec.redcode.net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEurekaServer
@SpringBootApplication
public class DockerSpringNetflixEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerSpringNetflixEurekaServerApplication.class, args);
    }

}
