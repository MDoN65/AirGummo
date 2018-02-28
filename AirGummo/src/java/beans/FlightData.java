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
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.annotation.Resource;
import javax.sql.rowset.CachedRowSet;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Ryguy
 */
@ManagedBean
@RequestScoped
public class FlightData {
    private int flightId;
    private String airlineName;
    private String departureCode;
    private String arrivalCode;
    private Date departureTime;
    private Date arrivalTime;
    private Timestamp totalFlyTime;
    private int flightStatus;
    private int seatAvailableFirst;
    private int seatAvailableBus;
    private int seatAvailableEco;
    private double ticketPrice;
    private String reasonCanceled;
    
    private boolean isRendered = false;
    
    @Resource(name = "jdbc/__default")
    DataSource dataSource;
    
    public FlightData() {
        
    }
    
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
            PreparedStatement insertFlight = connection.prepareStatement(
                            "insert into flight(flightId, airlineName, departureCode, arrivalCode, departureTime, arrivalTime, "
                            + "flightStatus, seatAvailableFirst, seatAvailableBus, seatAvailableEco, ticketPrice, flightTime)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insertFlight.setString(1, f.getFlightId());
            insertFlight.setString(2, f.getAirlineName());
            insertFlight.setString(3, f.getDepartureCode());
            insertFlight.setString(4, f.getArrivalCode());
            
            java.sql.Timestamp sqlDateDept = new java.sql.Timestamp(f.getDepartureTime().getTime());
            java.sql.Timestamp sqlDateArr = new java.sql.Timestamp(f.getArrivalTime().getTime());
            
            long duration  = f.getArrivalTime().getTime() - f.getDepartureTime().getTime();           
            Time flyTime = new Time(duration);
            
            
            insertFlight.setTimestamp(5, sqlDateDept);
            insertFlight.setTimestamp(6, sqlDateArr);
            insertFlight.setInt(7, f.getFlightStatus());
            insertFlight.setInt(8, f.getSeatAvailableFirst());
            insertFlight.setInt(9, f.getSeatAvailableBus());
            insertFlight.setInt(10, f.getSeatAvailableEco());
            insertFlight.setDouble(11, f.getTicketPrice());
            insertFlight.setTime(12, flyTime);
            
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
            //updateFlight.setDate(5, f.getDepartureTime());
            //updateFlight.setDate(6, f.getArrivalTime());
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
    
    public void deleteFlight(Flight f) throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            PreparedStatement deleteFlight = connection.prepareStatement(
                            "DELETE FROM flight WHERE flightId = ?");
            deleteFlight.setString(1, f.getFlightId());
            int done = deleteFlight.executeUpdate();
            
            flights.clear();
        } finally {
            connection.close();
        }
    }
    
        public void cancelFlight(Flight f) throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            PreparedStatement cancelFlight = connection.prepareStatement(
                            "UPDATE flight SET flightStatus = 0, reasonCanceled = ? WHERE flightId = ?");
            cancelFlight.setString(1, f.getReasonCanceled());
            cancelFlight.setString(2, f.getFlightId());

            int done = cancelFlight.executeUpdate();          
            flights.clear();
        } finally {
            connection.close();
        }
    }
        
    public void selectFlights(Flight f) throws SQLException {
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
                            "IF tClass = 1" +
                                "THEN" +
                                "SELECT flightId, departureCode, arrivalCode, totalFlyTime, ticketPrice AS 'TicketPrice' FROM flight " +
                                "WHERE departureCode = depCode" +
                                "AND arrivalCode = arrCode" +
                                "AND (departureTime BETWEEN beginTime AND endTime)" +
                                "AND seatAvailableEco >= seatCount;" +
                            "ELSEIF tClass = 2" +
                                "THEN " +
                                "SELECT flightId, departureCode, arrivalCode, totalFlyTime, ticketPrice * 1.1 AS 'TicketPrice' FROM flight " +
                                "WHERE departureCode = depCode" +
                                "AND arrivalCode = arrCode" +
                                "AND (departureTime BETWEEN beginTime AND endTime)" +
                                "AND seatAvailableBus >= seatCount;" +
                            "ELSEIF tClass = 3" +
                                "THEN " +
                                "SELECT flightId, departureCode, arrivalCode, totalFlyTime, ticketPrice * 1.25 AS 'TicketPrice' FROM flight " +
                                "WHERE departureCode = depCode" +
                                "AND arrivalCode = arrCode" +
                                "AND (departureTime BETWEEN beginTime AND endTime)" +
                                "AND seatAvailableFirst >= seatCount;" +
                            "END IF");
            updateFlight.setString(1, f.getFlightId());
            updateFlight.setString(2, f.getAirlineName());
            updateFlight.setString(3, f.getDepartureCode());
            updateFlight.setString(4, f.getArrivalCode());
//            updateFlight.setDate(5, f.getDepartureTime());
//            updateFlight.setDate(6, f.getArrivalTime());
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

    public Timestamp getTotalFlyTime() {
        return totalFlyTime;
    }

    public void setTotalFlyTime(Timestamp totalFlyTime) {
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
    
    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getReasonCanceled() {
        return reasonCanceled;
    }

    public void setReasonCanceled(String reasonCanceled) {
        this.reasonCanceled = reasonCanceled;
    }
    
}
