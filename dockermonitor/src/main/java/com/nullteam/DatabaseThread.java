package com.nullteam;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseThread extends  Thread {

    @Override
    public void run() {
        String imageId, repository, tag, timesUsed, size;
        String url = "jdbc:mysql://localhost:3306/dockerdb";
        String user = "root";
        String password = "nullteamtsipouroVolos123456789";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,user,password);
            File file = new File("images.csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = " ";
            String[] image;
            List<String> s = new ArrayList<>();
            
            while ((line = br.readLine()) != null) {
                image = line.split(",");
                imageId = image[0];
                repository = image[1];
                tag = image[2];
                timesUsed = image[3];
                size = image[4];



                if (imageId == ) {
                    String querryImage = "Insert into dockerimage(id,repository,tag,timesUsed,size) values("
                            + imageId + ',' + repository + ',' + tag + ',' + timesUsed + ',' + size + ')';
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(querryImage);
                }
            }
            br.close();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new RuntimeException(exception);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
