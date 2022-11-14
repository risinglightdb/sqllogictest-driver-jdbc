package com.risingwave.sqllogictest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class App
{
    private static String handleOne(Statement stmt, String sql) throws SQLException {
        var builder = new StringBuilder();
        if (stmt.execute(sql)) {
            try (ResultSet rs = stmt.getResultSet()) {
                var colCnt = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    for (var i = 1; i <= colCnt; i++) {
                        if (i != 1) {
                            builder.append(' ');
                        }
                        var v = rs.getString(i);
                        if (v == null) {
                            builder.append("NULL");
                        } else if (v.isEmpty()) {
                            builder.append("(empty)");
                        } else {
                            builder.append(v);
                        }
                    }
                    builder.append('\n');
                }
            }
        }
        return builder.toString();
    }

    private static void mainLoop(Connection conn) throws Exception {
        var parser = new JsonStreamParser(new InputStreamReader(System.in));
        try (var stmt = conn.createStatement()) {
            while (parser.hasNext()) {
                var ele = parser.next();
                var sql = ele.getAsJsonObject().get("sql").getAsString();
                try {
                    var result = handleOne(stmt, sql);
                    var resultJson = new JsonObject();
                    resultJson.addProperty("result", result);
                    System.out.println(resultJson.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    var resultJson = new JsonObject();
                    resultJson.addProperty("err", e.getMessage());
                    System.out.println(resultJson.toString());
                }
            }
        }
    }

    public static void main(String[] args)
    {
        var url = args[0];;
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
