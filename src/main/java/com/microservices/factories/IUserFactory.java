package com.microservices.factories;

import com.microservices.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IUserFactory {
    User fromResultSet(ResultSet rs) throws SQLException;
}
