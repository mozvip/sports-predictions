package predictions;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

public class DatabaseInterface {
	
	private static DBI dbi;
	
    private static final DatabaseInterface instance;
    
    static {
        try {
            instance = new DatabaseInterface();
        } catch (Exception e) {
            throw new RuntimeException("Darn, an error occurred!", e);
        }
    }
 
    public static DatabaseInterface getInstance() {
        return instance;
    }
 
    private DatabaseInterface() {
		DataSource ds = JdbcConnectionPool.create("jdbc:h2:predictordb","predictor","predictor");
		
		dbi = new DBI(ds);		
	}
	
	public static DBI getDBI() {
		return dbi;
	}

}
