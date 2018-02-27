/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.sql.rowset.CachedRowSet;
import java.time.format.DateTimeFormatter;
import java.sql.Date;

/**
 *
 * @author Ryguy
 */
public class FlightData {
    private int flightId;
    private String airlineName;
    private String departureCode;
    private String arrivalCode;
    private Date departureTime;
    private Date arrivalTime;
    private Double totalFlyTime;
    private int flightStatus;
    private int seatAvailableFirst;
    private int seatAvailableBus;
    private int seatAvailableEco;
    private String reasonCanceled;
    
    private boolean isRendered = false;
    
    @Resource(name = "jdbc/_default")
    DataSource dataSource;
    
    private ArrayList<Flight> flights = new ArrayList();
    
    public ArrayList<Flight> getFlightById(String fId) throws SQLException {
        if(dataSource == null){
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            PreparedStatement getFlight = connection.prepareStatement(
                    "SELECT flightId, airlineName, departureCode, arrivalCode, departureTime, arrivalTime, totalFlyTime, flightStatus"
                            + "seatAvailableFirst, seatAvailableBus, seatAvailableEco, ticketPrice FROM flight "
                            + "WHERE flightId = ?");
            getFlight.setString(1, fId);
            CachedRowSet rowSet = new CachedRowSetImpl();
            rowSet.populate(getFlight.executeQuery());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            while (rowSet.next()) {
                String fid = rowSet.getString(1);
                String airlineName = rowSet.getString(2);
                String depCode = rowSet.getString(3);
                String arrCode = rowSet.getString(4);
                //LocalDateTime depTime = LocalDateTime.parse(rowSet.getString(5), formatter);
                //LocalDateTime arrTime = LocalDateTime.parse(rowSet.getString(6), formatter);
                Date depTime = rowSet.getDate(5);
                Date arrTime = rowSet.getDate(6);
                Double totalFlyTime =  rowSet.getDouble(7);
                int flightStatus = rowSet.getInt(8);
                int SAF = rowSet.getInt(9);
                int SAB = rowSet.getInt(10);
                int SAE = rowSet.getInt(11);
                Double ticketPrice = rowSet.getDouble(12);
                Flight f = new Flight(fid, airlineName, depCode, arrCode, depTime, arrTime, totalFlyTime, flightStatus, SAF, SAB, SAE, ticketPrice, "");
                flights.add(f);
            }
        }  finally {
            connection.close();
        }
        
        isRendered = true;
        return flights;
    }
    
    public void addFlight(Flight f) throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            PreparedStatement insertFlight = connection.prepareStatement(
                            "insert into flight(flightId, airlineName, departureCode, arrivalCode, departureTime, arrivalTime, "
                            + "totalFlyTime, flightStatus, seatAvailableFirst, seatAvailableBus, seatAvailableEco, ticketPrice)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insertFlight.setString(1, f.getFlightId());
            insertFlight.setString(2, f.getAirlineName());
            insertFlight.setString(3, f.getDepartureCode());
            insertFlight.setString(4, f.getArrivalCode());
            insertFlight.setDate(5, f.getDepartureTime());
            insertFlight.setDate(6, f.getArrivalTime());
            insertFlight.setDouble(7, f.getTotalFlyTime());
            insertFlight.setInt(8, f.getFlightStatus());
            insertFlight.setInt(9, f.getSeatAvailableFirst());
            insertFlight.setInt(10, f.getSeatAvailableBus());
            insertFlight.setInt(11, f.getSeatAvailableEco());
            insertFlight.setDouble(12, f.getTicketPrice());
            
            int done = insertFlight.executeUpdate();
            
            flights.clear();
        } finally {
            connection.close();
        }

    }
    
    public void editFlight(Flight f) throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            PreparedStatement updateFlight = connection.prepareStatement(
                            "UPDATE flight SET flightId = ?, airlineName = ?, departureCode = ?, arrivalCode = ?"
                                    + " departureTime = ?, arrivalTime = ?, "
                                    + "totalFlyTime = ?, flightStatus = ?, seatAvailableFirst = ?, seatAvailableBus = ?, "
                                    + "seatAvailableEco = ?, ticketPrice = tPrice"
                                    + " WHERE flightId = ?");
            updateFlight.setString(1, f.getFlightId());
            updateFlight.setString(2, f.getAirlineName());
            updateFlight.setString(3, f.getDepartureCode());
            updateFlight.setString(4, f.getArrivalCode());
            updateFlight.setDate(5, f.getDepartureTime());
            updateFlight.setDate(6, f.getArrivalTime());
            updateFlight.setDouble(7, f.getTotalFlyTime());
            updateFlight.setInt(8, f.getFlightStatus());
            updateFlight.setInt(9, f.getSeatAvailableFirst());
            updateFlight.setInt(10, f.getSeatAvailableBus());
            updateFlight.setInt(11, f.getSeatAvailableEco());
            updateFlight.setDouble(12, f.getTicketPrice());
            updateFlight.setString(13, f.getFlightId());
            
            int done = updateFlight.executeUpdate();
            
            flights.clear();
        } finally {
            connection.close();
        }
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getDepartureCode() {
        return departureCode;
    }

    public void setDepartureCode(String departureCode) {
        this.departureCode = departureCode;
    }

    public String getArrivalCode() {
        return arrivalCode;
    }

    public void setArrivalCode(String arrivalCode) {
        this.arrivalCode = arrivalCode;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Double getTotalFlyTime() {
        return totalFlyTime;
    }

    public void setTotalFlyTime(Double totalFlyTime) {
        this.totalFlyTime = totalFlyTime;
    }

    public int getFlightStatus() {
        return flightStatus;
    }

    public void setFlightStatus(int flightStatus) {
        this.flightStatus = flightStatus;
    }

    public int getSeatAvailableFirst() {
        return seatAvailableFirst;
    }

    public void setSeatAvailableFirst(int seatAvailableFirst) {
        this.seatAvailableFirst = seatAvailableFirst;
    }

    public int getSeatAvailableBus() {
        return seatAvailableBus;
    }

    public void setSeatAvailableBus(int seatAvailableBus) {
        this.seatAvailableBus = seatAvailableBus;
    }

    public int getSeatAvailableEco() {
        return seatAvailableEco;
    }

    public void setSeatAvailableEco(int seatAvailableEco) {
        this.seatAvailableEco = seatAvailableEco;
    }

    public String getReasonCanceled() {
        return reasonCanceled;
    }

    public void setReasonCanceled(String reasonCanceled) {
        this.reasonCanceled = reasonCanceled;
    }
    
}
