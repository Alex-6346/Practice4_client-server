import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import practice1.Product;
import practice4.ProductCriteria;
import practice4.SQLTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class Practice4Test {

    private static final SQLTest sqlTest = new SQLTest();

    @BeforeAll
    static void initDb(){
        sqlTest.initialization();
    }

    @BeforeEach
    void insertDb(){
        Product prod1=sqlTest.insertProduct(new Product("prod1",9.1,4.2));
        Product prod2=sqlTest.insertProduct(new Product("prod2",20.3,8.5));
        Product prod3=sqlTest.insertProduct(new Product("other",30.3,18.5));
        Product otherProd1=sqlTest.insertProduct(new Product("prod1",9.1,4.2));
    }

    @AfterEach
    void cleanUp(){
        sqlTest.deleteAll();
    }

    //INSERT:
    @Test
    void shouldInsertProduct()
    {

        List<Product> productList= sqlTest.getAll();

        assertThat(productList)
                .extracting(Product::getName,Product::getPrice)
                .containsExactly(
                        tuple("prod1",9.1),
                        tuple("prod2",20.3),
                        tuple("other",30.3),
                        tuple("prod1",9.1) //twin of prod1
                );


    }

    //DELETE:
    @Test
    void shouldDeleteProductByValuesAndId() //
    {
        //sqlTest.deleteProductValuesAndId(new Product()); will throw an ID Exception
        sqlTest.deleteProductByValuesAndId(new Product(1,"prod1",9.1,4.2));
        List<Product> productList= sqlTest.getAll();

        assertThat(productList)
                .extracting(Product::getName,Product::getPrice)
                .containsExactly(
                        //tuple("prod1",9.1),
                        tuple("prod2",20.3),
                        tuple("other",30.3),
                        tuple("prod1",9.1)//twin of prod1 but other id
                );
    }

    @Test
    void shouldDeleteProductByValues()
    {
        sqlTest.deleteProductByValues(new Product("prod1",9.1,4.2));
        sqlTest.deleteProductByValues(new Product("prod4",49.1,44.2));
        sqlTest.deleteProductByValues(new Product()); //deletes nothing because nothing matched in the query

        List<Product> productList= sqlTest.getAll();

        assertThat(productList)
                .extracting(Product::getName,Product::getPrice)
                .containsExactly(
                        // tuple("prod1",9.1),
                        tuple("prod2",20.3),
                        tuple("other",30.3)
                        // tuple("prod1",9.1) - both twins are deleted
                );
    }

    //UPDATE:
    @Test
    void shouldUpdateProductByValuesAndId()
    {
        sqlTest.updateProductByValuesAndId(new Product(1,"prod1",9.1,4.2), new Product("SUPER_PROD", 100000.0, 1));
        List<Product> productList= sqlTest.getAll();

        assertThat(productList)
                .extracting(Product::getName,Product::getPrice)
                .containsExactly(
                        tuple("SUPER_PROD", 100000.0),
                        tuple("prod2",20.3),
                        tuple("other",30.3),
                        tuple("prod1",9.1)//twin of prod1 but other id
                );
    }

    @Test
    void shouldUpdateProductByValues()
    {
        sqlTest.updateProductByValues(new Product("prod1",9.1,4.2), new Product("SUPER_PROD", 100000.0, 1));

        List<Product> productList= sqlTest.getAll();

        assertThat(productList)
                .extracting(Product::getName,Product::getPrice)
                .containsExactly(
                        tuple("SUPER_PROD", 100000.0),
                        tuple("prod2",20.3),
                        tuple("other",30.3),
                        tuple("SUPER_PROD", 100000.0)
                );
    }






    @ParameterizedTest
    @MethodSource("filterArgumentsProvider")
    void shouldSelectByFilter(ProductCriteria criteria, List<Product> expected)
    {
        List<Product>products=sqlTest.getAllByCriteria(criteria);
        assertThat(products).containsExactlyInAnyOrderElementsOf(expected);
    }

    private static Stream<Arguments> filterArgumentsProvider()
    {
        return Stream.of(
                Arguments.of(
                        new ProductCriteria("prod",null,null,null,null),
                        List.of(
                                new Product(1,"prod1",9.1,4.2),
                                new Product(2,"prod2",20.3,8.5),
                                new Product(4,"prod1",9.1,4.2)
                        )
                ),
                Arguments.of(
                        new ProductCriteria(null,null,20.0,null,null),
                        List.of(
                                new Product(1,"prod1",9.1,4.2),
                                new Product(4,"prod1",9.1,4.2)
                        )
                ),
                Arguments.of(
                        new ProductCriteria(null,10.0,null,null,null),
                        List.of(
                                new Product(2,"prod2",20.3,8.5),
                                new Product(3,"other",30.3,18.5)
                        )
                )
        );

    }


}
