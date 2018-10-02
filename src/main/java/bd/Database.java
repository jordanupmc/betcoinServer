package bd;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private Database(){
    }

    public static Connection getConnection() throws URISyntaxException, SQLException {
        //URI dbUri = new URI(System.getenv("DATABASE_URL"));
        URI dbUri = new URI("postgres://ntzmikexhujzmd:a4ef7dd8a60e2e952477f2706492458bcdabfe44ede05ed8714a59fabc19a72e@ec2-54-225-68-133.compute-1.amazonaws.com:5432/ds85knecevckl"
        );
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath()+"?sslmode=require";

        return DriverManager.getConnection(dbUrl, username, password);
    }

}
