package hideonbush3.springboot.happypet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
	@PropertySource("classpath:application.properties")
})
public class HappypetApplication {

	public static void main(String[] args) {
		SpringApplication.run(HappypetApplication.class, args);
	}

}
