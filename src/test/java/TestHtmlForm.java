import com.codeborne.selenide.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.*;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class TestHtmlForm {
    private Logger log = LogManager.getLogger();

    @DataProvider
    public static Object[][] googleData() {
        return new Object[][]{{"Мне повезет", "Мне повез"}, {"10 * 10", "100"}, {"6 - 3 * 2", "0"}, {"Тест", "Тест"},};
    }

    @DataProvider
    public static Object[][] registration() {
        return new Object[][]{{"Виктория", "email@email.com", "qwerty1235", "qwerty1235"},};
    }

    @Test(dataProvider = "registration", threadPoolSize = 10, invocationCount = 5, invocationTimeOut = 1000)
    public void testMyHtml(String name, String email, String password, String password_retry) throws InterruptedException {
        open("C:\\Users\\User\\Desktop\\testUi\\src\\main\\resources\\index.html");

        $x("//input[@name='name']").val(name);
        $(By.xpath("//input[@name='email']")).val(email);
        $x("//input[@name='password']").val(password);
        $(By.name("password_retry")).val(password_retry);

        SelenideElement elementRadio = $(By.id("female"));
        elementRadio.click();
        boolean femaleButtonIsSelected = elementRadio.isSelected();
        log.info("Female button is checked " + femaleButtonIsSelected);

        $("select").selectOption(2);
        SelenideElement elementSelect = $(By.id("checkbox"));

        if (elementSelect.isSelected()) {
            log.info("Checkbox already is Toggled On");
        } else {
            log.trace("Checkbox is Toggled Off");
            elementSelect.click();
            log.trace("UPD: Checkbox is Toggled On");
        }

        Thread.sleep(3000);

        $x("//*[@type='submit']").click();

        $("#head").shouldBe(text(name + " " + email));

        Thread.sleep(3000);
    }

//    ============================TEST GOOGLE====================================

    @Test(dataProvider = "googleData")
    public void testGoogleThroughButton(String search, String result) {
        open("http://google.com");
        $x("//input[@title=\"Поиск\"]").val(search);
        $(By.name("btnK")).click();

        searchText(result);
    }

    @Test(dataProvider = "googleData")
    public void testGoogleThroughEnter(String search, String result) {
        open("http://google.com");
        $x("//input[@title=\"Поиск\"]").val(search).pressEnter();
        //.sendKeys(search, Keys.ENTER);

        searchText(result);
    }

    @BeforeClass
    public void setUp() {
        Configuration.browser = "chrome";
    }

    public void searchText(String result) {
        if ($("#cwos").exists()) {
            $("#cwos").shouldBe(text((result)));
        } else if ($x("//h3[./span]").exists()) {
            ElementsCollection elements = $$x("//h3[./span]");
            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).shouldHave(text(result));
            }
        }
    }
}
