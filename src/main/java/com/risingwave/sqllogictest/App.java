package com.risingwave.sqllogictest;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.Gson;

public class App
{
    private static ArrayList<ArrayList<String>> handleOne(Statement stmt, String sql) throws SQLException {
        var result = new ArrayList<ArrayList<String>>();
        if (stmt.execute(sql)) {
            try (ResultSet rs = stmt.getResultSet()) {
                var colCnt = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    var arr = new ArrayList<String>();
                    for (var i = 1; i <= colCnt; i++) {
                        var v = rs.getString(i);
                        if (v == null) {
                            arr.add("NULL");
                        } else if (v.isEmpty()) {
                            arr.add("(empty)");
                        } else {
                            arr.add(v);
                        }
                    }
                    result.add(arr);
                }
            }
        }
        return result;
    }

    private static void mainLoop(Connection conn) throws Exception {
        var parser = new JsonStreamParser(new InputStreamReader(System.in));
        var gson = new Gson();
        try (var stmt = conn.createStatement()) {
            while (parser.hasNext()) {
                var ele = parser.next();
                var sql = ele.getAsJsonObject().get("sql").getAsString();
                try {
                    var result = handleOne(stmt, sql);
                    var resultJson = new JsonObject();
                    resultJson.add("result", gson.toJsonTree(result));
                    System.out.println(resultJson);
                } catch (SQLException e) {
                    e.printStackTrace();
                    var resultJson = new JsonObject();
                    resultJson.addProperty("err", e.getMessage());
                    System.out.println(resultJson);
                }
            }
        }
    }

    public static void main(String[] args)
    {
        var url = args[0];
        var props = new Properties();
        if (args.length > 1) {
            props.setProperty("user", args[1]);
        }
        try (Connection conn = DriverManager.getConnection(url, props)) {
            mainLoop(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
