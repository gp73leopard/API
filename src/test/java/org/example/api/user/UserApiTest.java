package org.example.api.user;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class UserApiTest {

    private int id=15;
    private String username="ragnaros";
    private String firstName="Jonh";
    private String lastName="Smith";
    private String  email="JSmith@gmail.com";
    private  String password="Kjsfa1s82jSD";
    private String phone="1-877-427-5776";
    private int userStatus=0;

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

    // Создаем пользователя
    @Test
    public void createUser() {
        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setUserStatus(userStatus);


        given()
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(200);

        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .extract().body()
                .as(User.class);
    }

    // Авторизация пользователя
    @Test
    public void logInUser() {

        // Логинимся
        given()
                .pathParams("username", username, "password", password)
                .when()
                .get("/user/login?username={username}&password={password}")
                .then()
                .statusCode(200);

        // Выходим из системы
        given()
                .when()
                .get("user/logout")
                .then()
                .statusCode(200);
    }

    // Удалить пользователя
    @Test
    public void deleteUser() {

//        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200);

        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(404);
    }

}
