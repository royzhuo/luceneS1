import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring.xml"})
public class testSpring {
    @Autowired
    private DataSource dataSource;



}
