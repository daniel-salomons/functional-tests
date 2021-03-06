package nl.vpro.poms.selenium.poms.wijzigen;

import lombok.extern.slf4j.Slf4j;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import nl.vpro.api.client.utils.Config;
import nl.vpro.poms.config.Webtest;

import static org.junit.runners.MethodSorters.NAME_ASCENDING;

/**
 * @author Michiel Meeuwissen
 */

@Slf4j
@FixMethodOrder(NAME_ASCENDING)
public class PersonsWebTest extends Webtest {

    @BeforeClass
    public static void setUp() {
        loginVPROand3voor12();
    }

    static String firstName = "Pietje";
    static String lastName = "Puk" + System.currentTimeMillis();

    /**
     * Create a clip, forgetting to fill in the genre.
     *
     * Nog in te vullen: Genre should be in the footer of the page
     */
    @Test
    public void test01OpenObject() {

        driver.get(CONFIG.getProperties(Config.Prefix.poms).get("baseUrl") + "/#/edit/" + MID);

    }
    @Test
    public void test02AddPerson() {
        ngWebDriver.waitForAngularRequestsToFinish();


        WebElement element = driver.findElement(By.cssSelector("#media-general-WO_VPRO_025057 > div.media-section-general-left > poms-persons > div > button"));
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();

        element.click();
        driver.findElement(By.cssSelector("#suggestions")).sendKeys(firstName + " " + lastName);
        ngWebDriver.waitForAngularRequestsToFinish();
        driver.findElement(By.cssSelector("div.col-12.personfields  span.new")).click();

        ngWebDriver.waitForAngularRequestsToFinish();

        driver.findElement(By.id("givenName")).sendKeys(firstName);
        driver.findElement(By.id("familyName")).sendKeys(lastName);
        driver.findElement(By.id("role")).sendKeys("Redacteur");
        ngWebDriver.waitForAngularRequestsToFinish();

        driver.findElement(By.cssSelector("#scroll-spy-top > div.modal.fade.modal-person.in > div > div > form > div.footer-container > div > button:nth-child(2)")).click();

        ngWebDriver.waitForAngularRequestsToFinish();


        // TODO add Checks.


    }
}
