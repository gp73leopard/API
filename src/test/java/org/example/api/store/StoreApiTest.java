package org.example.api.store;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.example.model.Order;
import java.io.IOException;
import java.util.Map;



import static io.restassured.RestAssured.given;

public class StoreApiTest {

    private int id = 1;
    private int petId = 1;
    private int quantity = 1;

    @BeforeClass
    public void prepare() throws IOException{
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void orderSave(){
        Order order = new Order();

        order.setId(id);
        order.setPetId(petId);
        order.setQuantity(quantity);

        // Делаем заказ
        given()
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200);

        // Находим заказ
        Order actual =
                given()
                        .pathParam("orderId", id)
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);

        Assert.assertEquals(actual.getId(), order.getId());

    }

    @Test
    public void orderDelete() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        // Удаляем заказ
        given()
                .pathParam("orderId", id)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);

        // Пытаемся найти заказ после удаления
        given()
                .pathParam("orderId", id)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }

    @Test
    public void inventoryInfo(){

        // Сохраняем тело ответа для эндпоинта /store/inventory в виде Map.class
        Map<String, Integer> inventory;

        inventory =
                given()
                        .when()
                        .get("/store/inventory")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Map.class);

        // С помощью Assert провалидируем поле sold
        Assert.assertTrue(inventory.containsKey("sold"), "Inventory не содержит статус sold" );

    }

}
