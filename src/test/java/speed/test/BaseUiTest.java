package speed.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

public class BaseUiTest {

    protected WebDriver driver;

    // baza url testa
    protected static final String BASE_URL = "https://unnd.lab2.pepeshka.ru/";

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(BASE_URL);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected Sidebar login() {
        // TODO: dobavit vhod v prilozhenie
        LoginPage lp = new LoginPage(driver);
        lp.loginAsExpertAes();
        return new Sidebar(driver);
    }
}
