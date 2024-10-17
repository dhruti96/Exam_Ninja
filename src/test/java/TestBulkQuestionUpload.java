import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class TestBulkQuestionUpload {

    @BeforeClass
    public static void setup() {
        // Base URI
        RestAssured.baseURI = "http://localhost:9000"; // Update with your actual base URL
    }

    @Test
    public void testBulkUpload_SuccessfulUpload() {
        File successUpload = new File("src/test/resources/SampleSuccess.csv");
        Response response = given()
                .multiPart("file", successUpload, "text/csv")
                .multiPart("examName", "Java")
                .header("Content-Type", "multipart/form-data")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting response status code and message
        Assert.assertEquals(response.getStatusCode(), 200);
        String status = response.jsonPath().getString("status");
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(status, "10 Questions uploaded successfully.");
        Assert.assertEquals(message, "Questions failed 0");
    }

    @Test
    public void testBulkUpload_NoFileAttached() {
        Response response = given()
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for missing file
        Assert.assertEquals(response.getStatusCode(), 404);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "File not Attached, Please upload a valid CSV file.");
        Assert.assertEquals(status, "File Not Found");
    }

    @Test
    public void testBulkUpload_InvalidFileFormat() {
        File invalidFormat = new File("src/test/resources/invalid filetype.xlsx");
        Response response = given()
                .multiPart("file", invalidFormat, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for invalid file format
        Assert.assertEquals(response.getStatusCode(), 415);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "Invalid file format. Only CSV files are allowed.");
        Assert.assertEquals(status, "Can't upload file");
    }

    @Test
    public void testEmptyFile() {
        File emptyFile = new File("src/test/resources/emptyfile - Copy.csv");
        Response response = given()
                .multiPart("file", emptyFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for empty file
        Assert.assertEquals(response.getStatusCode(), 400);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "File content is empty. No records found.");
        Assert.assertEquals(status, "Can't upload file");
    }

    @Test
    public void testMissingMandatoryField() {
        File missingQuestionFile = new File("src/test/resources/Missing Mandatory Fields.csv");
        Response response = given()
                .multiPart("file", missingQuestionFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for missing mandatory field
        Assert.assertEquals(response.getStatusCode(), 400);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "Question text is missing");
        Assert.assertEquals(status, "Upload failed");
    }

    @Test
    public void testMissingQuestionType() {
        File missingQTypeFile = new File("src/test/resources/mssingQtype.csv");
        Response response = given()
                .multiPart("file", missingQTypeFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for missing question type
        Assert.assertEquals(response.getStatusCode(), 400);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "Question type must be defined. Allowed values are 'MCQ' or 'True/False'.");
        Assert.assertEquals(status, "Upload failed");
    }

    @Test
    public void testDuplicateQuestions() {
        File duplicateQuestionsFile = new File("src/test/resources/duplicate Qs.csv");
        Response response = given()
                .multiPart("file", duplicateQuestionsFile, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for duplicate questions
        Assert.assertEquals(response.getStatusCode(), 409);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "Duplicate questions found");
        Assert.assertEquals(status, "Upload failed");
    }

    @Test
    public void testMissingQuestion() {
        File missingQuestion = new File("src/test/resources/missingQS.csv");
        Response response = given()
                .multiPart("file", missingQuestion, "text/csv")
                .multiPart("examName", "Java")
                .when()
                .post("/api/admin/questions/upload")
                .then()
                .extract()
                .response();

        // Asserting status code and error message for missing question
        Assert.assertEquals(response.getStatusCode(), 400);
        String error = response.jsonPath().getString("error");
        String status = response.jsonPath().getString("status");
        Assert.assertEquals(error, "A question is missing in the file");
        Assert.assertEquals(status, "Upload failed");
    }
}

