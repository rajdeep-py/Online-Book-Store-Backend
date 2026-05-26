package com.bookstore.dao;

import com.bookstore.model.BusinessCharges;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BusinessChargesDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public List<BusinessCharges> getAll() throws SQLException {
        List<BusinessCharges> charges = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("charges.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    charges.add(mapCharges(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return charges;
    }

    public BusinessCharges getById(int chargesId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("charges.getById"))) {
            ps.setInt(1, chargesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCharges(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public int create(BusinessCharges charges) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("charges.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, charges.getPlatformFee());
            ps.setBigDecimal(2, charges.getDeliveryFee());
            ps.setBigDecimal(3, charges.getTaxesPercent());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return 0;
    }

    public void update(BusinessCharges charges) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("charges.update"))) {
            ps.setBigDecimal(1, charges.getPlatformFee());
            ps.setBigDecimal(2, charges.getDeliveryFee());
            ps.setBigDecimal(3, charges.getTaxesPercent());
            ps.setInt(4, charges.getChargesId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void delete(int chargesId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("charges.delete"))) {
            ps.setInt(1, chargesId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private BusinessCharges mapCharges(ResultSet rs) throws SQLException {
        BusinessCharges charges = new BusinessCharges();
        charges.setChargesId(rs.getInt("charges_id"));
        charges.setPlatformFee(rs.getBigDecimal("platform_fee"));
        charges.setDeliveryFee(rs.getBigDecimal("delivery_fee"));
        charges.setTaxesPercent(rs.getBigDecimal("taxes_percent"));
        charges.setCreatedAt(rs.getTimestamp("created_at"));
        charges.setUpdatedAt(rs.getTimestamp("updated_at"));
        return charges;
    }
}
