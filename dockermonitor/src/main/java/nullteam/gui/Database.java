package nullteam.gui;
import com.nullteam.*;
import io.netty.channel.ConnectTimeoutException;

import java.sql.Connection;

public class Database {
    public Connection connection = ClientUpdater.connectToDatabase();

    public Connection getConnection() {
        return this.connection;
    }
}
