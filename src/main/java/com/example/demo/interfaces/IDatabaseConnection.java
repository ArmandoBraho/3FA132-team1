package com.example.demo.interfaces;

import java.util.Properties;

public interface IDatabaseConnection {
    // why this : https://www.phind.com/search?cache=v7rx2kjxrwx51l5htgk3mq70
    IDatabaseConnection openConnection(Properties properties);
    void createAllTables();
    void truncateAllTables();
    void removeAllTables();
    void closeConnection();
}