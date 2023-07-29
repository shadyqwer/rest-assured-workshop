package exercises;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@WireMockTest(httpPort = 9876)
public class RestAssuredExercises6Test {

    /*******************************************************
     * Create a new payload for a GraphQL query using a
     * HashMap and the specified query (with hardcoded ID)
     *
     * POST this object to https://rickandmortyapi.com/graphql
     *
     * Assert that the name of the fruit is 'Rick Sanchez'
     *
     * Use "data.character.name" as the GPath
     * expression to extract the required value from the response
     ******************************************************/

    @Test
    public void getFruitData_checkFruitName_shouldBeManzana() {

        String queryString = """
                {
                    character(id: 1){
                        id
                        name
                        status
                        species
                        gender
                      }
                }
                """;

        HashMap<String, Object> graphqlQuery = new HashMap<>();
        graphqlQuery.put("query", queryString);

        given().
            contentType(ContentType.JSON).
            body(graphqlQuery).
        when().
            post("https://rickandmortyapi.com/graphql").
        then().
            assertThat().
            statusCode(200).
        and().
            body("data.character.name", equalTo("Rick Sanchez"));
    }

    /*******************************************************
     * Transform this Test into a ParameterizedTest, using
     * a CsvSource data source with three test data rows:
     * -------------------------------------------
     *       id  |              name |     species
     * -------------------------------------------
     *         1 |       Morty Smith |       Human
     *        14 |       Alien Morty |       Alien
     *        83 |  Cronenberg Morty |  Cronenberg
     *
     * Parameterize the test
     *
     * Create a new GraphQL query from the given query string
     * Pass in the fruit id as a variable value
     *
     * POST this object to https://rickandmortyapi.com/graphql
     *
     * Assert that the HTTP response status code is 200
     *
     * Assert that the name of the fruit is equal to the value in the data source
     * Use "data.character.name" as the GPath
     * expression to extract the required value from the response
     *
     * Also, assert that the tree name is equal to the value in the data source
     * Use "data.character.species" as the GPath
     * expression to extract the required value from the response
     ******************************************************/

    @ParameterizedTest
    @CsvSource({
            "2, Morty Smith, Human",
            "14, Alien Morty, Alien",
            "83, Cronenberg Morty, Cronenberg"
    })
    public void getFruitDataById_checkFruitNameAndTreeName(int characterId, String expectedName, String expectedSpecies) {

        String queryString = """
                query GetCharacter($id: ID!)
                {
                    character(id:$id){
                        id
                        name
                        status
                        species
                        gender
                      }
                }
                """;

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("id", characterId);

        HashMap<String, Object> graphqlQuery = new HashMap<>();
        graphqlQuery.put("query", queryString);
        graphqlQuery.put("variables", variables);

        given().
            contentType(ContentType.JSON).
            body(graphqlQuery).
        when().
            post("https://rickandmortyapi.com/graphql").
        then().
            assertThat().
            statusCode(200).
        and().
            body("data.character.name", equalTo(expectedName)).
        and().
            body("data.character.species", equalTo(expectedSpecies));
    }
}