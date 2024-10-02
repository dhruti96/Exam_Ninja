import io.restassured.RestAssured;
import io.restassured.response.Response;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BulkUploadTest {

    private WireMockServer wireMockServer;

    @BeforeClass
    public void setup() {
        // Initialize WireMock on port 9000
        wireMockServer = new WireMockServer(9000);
        wireMockServer.start();

        // Mock the /api/admin/questions/upload endpoint
        //Successfully question upload
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{ \"status\": \"10 Questions uploaded successfully.\", \"message\": \"Questions failed 0\" }")));

        //No file or missing file
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                        .withBody("{ \"error\": \"File not Attached, Please upload a valid CSV file.\", \"status\": \"File Not Found\" }")));

        //Invalid file formate
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(415)
                        .withBody("{ \"error\": \"Invalid file format. Only CSV files are allowed.\", \"status\": \"Can't upload file\" }")));

        //Empty file
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("{ \"error\": \"File content is empty. No records found.\", \"status\": \"Can't upload file\" }")
                ));

        //Missing Mandatory Field
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"Question text is missing\", \"status\": \"Upload failed\" }")
                ));

        //Missing Mandatory/ Question Type
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"Question type must be defined. Allowed values are 'MCQ' or 'True/False'.\", \"status\": \"Upload failed\" }")
                ));

        //Missing Question text
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"Question text is missing\", \"status\": \"Upload failed\" }")
                ));

        //Duplicate Question
        wireMockServer.stubFor(post(urlEqualTo("/api/admin/questions/upload"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"status\": \"0 Questions uploaded successfully.\", \"message\": \"These Questions 5 Already exist\", \"failedQuestions\":  \" Duplicate question found : What is Inheritance?\",\" Duplicate question found : What is polymorphism?\", \" Duplicate question found : True or False: Java is platform-independent.\"}")
                ));

        RestAssured.baseURI = "http://localhost:9000";
    }

    @Test
    public void testBulkUpload_SuccessfulUpload() {

        File successUpload = new File("src/test/resources/SampleSuccess.csv");
        given()
                .multiPart("file", successUpload, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
        // System.out.println("Response: " + response.asString());
    }

    @Test
    public void testBulkUpload_NoFileAttached() {

        Response response = given()
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
//                .statusCode(404)
//                .body("error", equalTo("File not Attached, Please upload a valid CSV file."))
//                .body("status", equalTo("File Not Found"))
                .extract()
                .response();

        // System.out.println("Response: " + response.asString());
    }

    @Test
    public void testBulkUpload_InvalidFileFormat() {

        File invalidFormate = new File("src/test/resources/invalid filetype.xlsx");
        given()
                .multiPart("file", invalidFormate, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
//                .statusCode(415)
//                .body("error", equalTo("Invalid file format. Only CSV files are allowed."))
//                .body("status", equalTo("Can't upload file"))
                .extract()
                .response();

        // System.out.println("Response: " + response.asString());
    }

    // Other scenarios like missing fields, empty file, duplicate questions can be tested similarly.

    @Test
    public void testEmptyFile() {

        File emptyFile = new File("src/test/resources/emptyfile - Copy.csv");
        given()
                .multiPart("file", emptyFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
//                .statusCode(400)
//                .body("error", equalTo("File content is empty. No records found."))
//                .body("status", equalTo("Can't upload file"));
                .extract()
                .response();
    }

    @Test
    public void testMissingMandatoryField() {

        File missingQuestionFile = new File("src/test/resources/Missing Mandatory Fields.csv");
        given()
                .multiPart("file", missingQuestionFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
//                .statusCode(400)
//                .body("error", equalTo("Question text is missing"))
//                .body("status", equalTo("Upload failed"))
                .extract()
                .response();
    }

    @Test
    public void testMissingQuestionType() {

        File missingQTypeFile = new File("src/test/resources/mssingQtype.csv");
        given()
                .multiPart("file", missingQTypeFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
//                .statusCode(400)
//                .body("error", equalTo("Question type must be defined. Allowed values are 'MCQ' or 'True/False'."))
//                .body("status", equalTo("Upload failed"))
                .extract()
                .response();
    }

    @Test
    public void testDuplicateQuestions() {

        File duplicateQuestionsFile = new File("src/test/resources/duplicate Qs.csv");
        given()
                .multiPart("file", duplicateQuestionsFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testMissingQuestion(){

        File missingQuestion = new File("src/test/resources/missingQS.csv");
        given()
                .multiPart("file", missingQuestion, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();
    }

    @AfterClass
    public void teardown() {
        // Stop WireMock
        wireMockServer.stop();
    }
}

