package practice4;

import practice1.CRC16;
import practice1.Product;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLTest {
    private Connection con;

    //CREATE:
    public void initialization(){
        try{
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite::memory:" /*+ name*/);
            PreparedStatement st = con.prepareStatement("create table if not exists 'product' (" +
                    "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "'name' text," +
                    "'price' double," +
                    " 'amount' double"+
                    ");  ");
            st.executeUpdate();
        }
        catch(ClassNotFoundException e){
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        }catch (SQLException e){
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }     
    }

    //INSERT:
    public Product insertProduct(Product product){
        try{
            PreparedStatement statement = con.prepareStatement("INSERT INTO product(name,price,amount) VALUES (?,?,?)");
            //statement.setInt(1, 1);
            //statement.setInt(1, product.getId());
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setDouble(3, product.getAmount());

            statement.executeUpdate();
            ResultSet resultSet=  statement.getGeneratedKeys();
            product.setId(resultSet.getInt("last_insert_rowid()"));
            statement.close();
            return product;
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Can`t insert product",e);
        }
    }
    //READ:
    public List<Product> getAll(){
        List<Product> products=new ArrayList<>();
        try{
            Statement st = con.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM product");

            while (resultSet.next()) {
                products.add(convertProduct(resultSet));
            }
            resultSet.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return products;
    }

    //UPDATE:
    public void updateProductByValuesAndId(Product product, Product change) throws InvalidParameterException {
        if(product.getId()==null){
            throw new InvalidParameterException("updateProductValuesAndId: inserted product without id");
        }
        String sql= "UPDATE product " +
                " SET name= '" + change.getName() + "' , "+
                " price=" + change.getPrice() + " , " +
                " amount=" + change.getAmount() +
                " WHERE id=" + product.getId() + " AND " +
                " name= '" + product.getName() + "' AND " +
                " price=" + product.getPrice() + " AND " +
                " amount=" + product.getAmount() + ";";

        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateProductByValues(Product product, Product change) throws InvalidParameterException {
        String sql= "UPDATE product " +
                " SET name= '" + change.getName() + "' , "+
                " price=" + change.getPrice() + " , " +
                " amount=" + change.getAmount() +
                " WHERE " +
                " name= '" + product.getName() + "' AND " +
                " price=" + product.getPrice() + " AND " +
                " amount=" + product.getAmount() + ";";

        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }






    //DELETE:
    public void deleteProductByValuesAndId(Product product) throws InvalidParameterException {
        if(product.getId()==null){
            throw new InvalidParameterException("deleteProductValuesAndId: inserted product without id");
        }
        String sql= "DELETE from product " +
                " WHERE id="+product.getId() + " AND " +
                " name= '" + product.getName() + "' AND " +
                " price=" + product.getPrice() + " AND " +
                " amount=" + product.getAmount() + ";";

            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    }

    public void deleteProductByValues(Product product) throws InvalidParameterException {
        String sql= "DELETE from product " +
                " WHERE " +
                " name= '" + product.getName() + "' AND " +
                " price=" + product.getPrice() + " AND " +
                " amount=" + product.getAmount() + ";";

        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteAll(){
        try (PreparedStatement statement = con.prepareStatement("delete from product")) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (PreparedStatement statement = con.prepareStatement(" DELETE FROM SQLITE_SEQUENCE WHERE name='product'; ")) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    //LIST BY CRITERIA:
    public List<Product> getAllByCriteria(ProductCriteria productCriteria){
        StringBuilder sb = new StringBuilder();

        List<String> criterias= new ArrayList();
        if(productCriteria.getName()!=null) { criterias.add(" name like '%" + productCriteria.getName() + "%' ");}
        if(productCriteria.getPriceFrom()!=null){ criterias.add(" price >=" + productCriteria.getPriceFrom());}
        if(productCriteria.getPriceTill()!=null){ criterias.add(" price <=" + productCriteria.getPriceTill());}
        if(productCriteria.getAmountFrom()!=null){ criterias.add(" amount >=" + productCriteria.getAmountFrom());}
        if(productCriteria.getAmountTill()!=null){ criterias.add(" amount <=" + productCriteria.getAmountTill());}
        String where = String.join(" and", criterias);

        List<Product> products=new ArrayList<>();
        try{
            Statement st = con.createStatement();
            String sql = criterias.isEmpty()
                    ? "SELECT * FROM product"
                    : "SELECT * FROM product where" + where;

            ResultSet resultSet = st.executeQuery(sql + ";");

            while (resultSet.next()) {
                products.add(convertProduct(resultSet));
            }
            resultSet.close();
            st.close();
        }catch(SQLException e){
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
        }
        return products;
    }

    private static Product convertProduct(ResultSet resultSet) throws SQLException {
        return  new Product(resultSet.getInt("id"),resultSet.getString("name"),
                resultSet.getDouble("price"),resultSet.getDouble("amount"));
    }
    
    public static void main(String[] args){
        /*SQLTest sqlTest = new SQLTest();
        sqlTest.initialization();
        sqlTest.insertProduct(new Product("prod1",10,152.3));
        sqlTest.insertProduct(new Product("prod2",20.1,100));
        sqlTest.insertProduct(new Product("other",20.1,30.4));

        ProductCriteria criteria = new ProductCriteria();
        criteria.setName("prod");
        System.out.println(sqlTest.getAllByCriteria(criteria));
        criteria.setPriceFrom(20.0);
        System.out.println(sqlTest.getAllByCriteria(criteria));

        System.out.println(sqlTest.getAllByCriteria(new ProductCriteria()));*/
    }
}
