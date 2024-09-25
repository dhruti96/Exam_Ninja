import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TestAdminLoginPage extends BaseSetUp {

    private Logger log = Logger.getLogger(TestRegistrationPage.class.getName());
    private WebDriver driver;
    private Properties p;

    @BeforeClass
    public void loadProperties() throws IOException {
        // Load properties from application.properties file
        p = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/application.properties");
        p.load(fis);
    }

    @BeforeMethod
    public void setUp() {
        driver = getWebDriver();
        // Open the registration page
        driver.get(p.getProperty("url1"));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        log.info("Navigated to registration page: " + p.getProperty("url"));
    }

    @Test
    public void testSuccessfulLogin(){
        // Happy path: Successful login

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys(p.get("pwd_val").toString());
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        //assertTrue("Login was not successful", l_button.isDisplayed());
    }

    @Test
    public void testInvalidPassword(){
        // Unhappy path: Unsuccessful login
        //Enter valid username and invalid password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("drs@12Sol");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

//        WebElement pwdError = driver.findElement(By.xpath("//span[@id='passwordError']"));
//        assertEquals("Please enter valid password", pwdError.getText());
    }

    @Test
    public void testInvalidUsername(){
        // Unhappy path: Unsuccessful login
        //Enter invalid username and valid password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys("xxxxxgmail.com");
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys(p.get("pwd_val").toString());
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement userError = driver.findElement(By.xpath("//span[@id='usernameError']"));
        assertEquals("Please enter a valid email address.", userError.getText());
    }

    @Test
    public void testInvalidUsernameAndPassword(){
        // Unhappy path: Unsuccessful login
        //Enter invalid username and invalid password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("abcdef#12");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();
    }

    @Test
    public void testPasswordWithoutSpecialCharacter(){
        // Unhappy path: Unsuccessful login
        //Enter password without special character

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("Abcdef12");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//span[@id='specialCharError']"));
        assertEquals("Password must contain at least one special character.", pwdError.getText());
    }

    @Test
    public void testPasswordWithoutUpperCase(){
        // Unhappy path: Unsuccessful login
        //Enter password without Uppercase letter

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("abc#def1");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//span[@id='uppercaseError']"));
        assertEquals("Password must contain at least one uppercase letter.", pwdError.getText());
    }

    @Test
    public void testPasswordWithoutNumber(){
        // Unhappy path: Unsuccessful login
        //Enter password without number

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("abc#defgh");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//span[@id='numberError']"));
        assertEquals("Password must contain at least one number.", pwdError.getText());
    }

    @Test
    public void testWeakPassword(){
        // Unhappy path: Unsuccessful login
        //Enter lest than 8 character in password

        WebElement userName = driver.findElement(By.xpath(p.get("userName").toString()));
        userName.sendKeys(p.get("uName").toString());
        WebElement password = driver.findElement(By.xpath(p.get("pwd").toString()));
        password.sendKeys("abc#1");
        WebElement l_button = driver.findElement(By.xpath(p.get("login_btn").toString()));
        l_button.click();

        WebElement pwdError = driver.findElement(By.xpath("//span[@id='passwordError']"));
        assertEquals("Password must contain at least 8 characters.", pwdError.getText());
    }
}