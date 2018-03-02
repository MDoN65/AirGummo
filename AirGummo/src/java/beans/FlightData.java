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
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *
 * @author Ryguy
 */
@ManagedBean
@SessionScoped
public class FlightData {
    private int flightId;
    private String airlineName;
    private String departureCode;
    private String arrivalCode;
    private Date departureTime;
    private Date arrivalTime;
    private Time totalFlyTime;
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
    
    @PostConstruct
    public void init() {
        flights = getFlights();
    }
    
    private ArrayList<Flight> flights = new ArrayList();
    
    public ArrayList<Flight> getFlightById(String fId) throws SQLException {
        flights.clear();
        if(dataSource == null){
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            PreparedStatement getFlight = connection.prepareStatement(
                    "SELECT flightId, airlineName, departureCode, arrivalCode, departureTime, arrivalTime, flightTime, flightStatus,"
                            + "seatAvailableFirst, seatAvailableBus, seatAvailableEco, ticketPrice FROM flight "
                            + "WHERE flightId = ?");
            getFlight.setString(1, fId);
            CachedRowSet rowSet = new CachedRowSetImpl();
            rowSet.populate(getFlight.executeQuery());
            while (rowSet.next()) {
                String fid = rowSet.getString(1);
                String airlineName = rowSet.getString(2);
                String depCode = rowSet.getString(3);
                String arrCode = rowSet.getString(4);
                Date depTime = rowSet.getDate(5);
                Date arrTime = rowSet.getDate(6);
                Time totalFlyTime =  rowSet.getTime(7);
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
            long durationHour = duration / 1000 / 60 / 60;
            long durationMin = (duration / 1000 / 60) - (durationHour * 60) ;
            String time = String.format("%02d:%02d:%02d", durationHour, durationMin, 00);
      
            insertFlight.setTimestamp(5, sqlDateDept);
            insertFlight.setTimestamp(6, sqlDateArr);
            insertFlight.setInt(7, f.getFlightStatus());
            insertFlight.setInt(8, f.getSeatAvailableFirst());
            insertFlight.setInt(9, f.getSeatAvailableBus());
            insertFlight.setInt(10, f.getSeatAvailableEco());
            insertFlight.setDouble(11, f.getTicketPrice());
            insertFlight.setString(12, time);
            
            int done = insertFlight.executeUpdate();
            
            flights.clear();
        } finally {
            connection.close();
        }

    }
    
    public void editFlight(Flight f) throws SQLException, Exception {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            if (f.getFlightStatus() != 1) {
            throw new Exception("Cannot update flight that is not open");
        }
            
            PreparedStatement updateFlight = connection.prepareStatement(
                            "UPDATE flight SET departureTime = ?, arrivalTime = ?, "
                                    + "flightTime = ?, seatAvailableFirst = ?, seatAvailableBus = ?, "
                                    + "seatAvailableEco = ?, ticketPrice = ?"
                                    + " WHERE flightId = ?");
            
            java.sql.Timestamp sqlDateDept = new java.sql.Timestamp(f.getDepartureTime().getTime());
            java.sql.Timestamp sqlDateArr = new java.sql.Timestamp(f.getArrivalTime().getTime());
            
            long duration  = f.getArrivalTime().getTime() - f.getDepartureTime().getTime();       
            long durationHour = duration / 1000 / 60 / 60;
            long durationMin = (duration / 1000 / 60) - (durationHour * 60) ;
            String time = String.format("%02d:%02d:%02d", durationHour, durationMin, 00);
            
            updateFlight.setTimestamp(1, sqlDateDept);
            updateFlight.setTimestamp(2, sqlDateArr);
            updateFlight.setString(3, time);
            updateFlight.setInt(4, f.getSeatAvailableFirst());
            updateFlight.setInt(5, f.getSeatAvailableBus());
            updateFlight.setInt(6, f.getSeatAvailableEco());
            updateFlight.setDouble(7, f.getTicketPrice());
            updateFlight.setString(8, f.getFlightId());
            
            int done = updateFlight.executeUpdate();            
            flights.clear();
        } catch (Exception ex) {
            
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
            PreparedStatement deleteFlight = connection.prepareStatement("DELETE FROM flight WHERE flightId = ?");
            deleteFlight.setString(1, f.getFlightId());
            int done = deleteFlight.executeUpdate();
            flights.clear();
        } finally {
            connection.close();
        }
    }
    
        public void cancelFlight(Flight f) throws SQLException, Exception {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            if (f.getFlightStatus() != 1) {
            throw new Exception("Cannot cancel this flight because its flight status is not open");
        }
            
            PreparedStatement cancelFlight = connection.prepareStatement("UPDATE flight SET flightStatus = 5, reasonCanceled = ? WHERE flightId = ?");
            cancelFlight.setString(1, f.getReasonCanceled());
            cancelFlight.setString(2, f.getFlightId());

            int done = cancelFlight.executeUpdate();          
            flights.clear();
        } catch (Exception ex) {

        } finally {
            connection.close();
        }
    }
        
    public ArrayList<Flight> selectFlights(Flight f) throws SQLException {
        flights.clear();
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        Connection connection = dataSource.getConnection();
        if (dataSource == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        ArrayList<Flight> alliance = new ArrayList<Flight>();
        try {         
            PreparedStatement selectFlight = connection.prepareStatement(
                 "SELECT * FROM flight INNER JOIN alliance on flight.flightId = alliance.flightNumber " +
"                WHERE alliance.allianceCode = (SELECT allianceCode FROM alliance INNER " +
"                JOIN flight on flight.flightId = alliance.flightNumber WHERE alliance.flightNumber = " +
"               (SELECT flight.flightId FROM flight where flight.departureCode = ?)) AND " +
"               (SELECT allianceCode FROM alliance INNER JOIN flight on flight.flightId = alliance.flightNumber " +
"                WHERE alliance.flightNumber = (SELECT flight.flightId FROM flight where flight.arrivalCode = ?)) " +
"                AND (SELECT allianceCode FROM alliance INNER JOIN flight on flight.flightId = alliance.flightNumber " +
"                WHERE alliance.flightNumber = (SELECT flight.flightId FROM flight where DATE(flight.departureTime)" +
"                = ?)) ORDER BY alliance.stepNumber");
            selectFlight.setString(1, f.getDepartureCode());
            selectFlight.setString(2, f.getArrivalCode());
            
            java.sql.Timestamp sqlDateDept = new java.sql.Timestamp(f.getDepartureTime().getTime());     
            
            selectFlight.setTimestamp(3, sqlDateDept);
            
            CachedRowSet rowSet = new CachedRowSetImpl();
            rowSet.populate(selectFlight.executeQuery());
            
            while (rowSet.next()) {
                String fid = rowSet.getString(1);
                String airlineName = rowSet.getString(2);
                String depCode = rowSet.getString(3);
                String arrCode = rowSet.getString(4);
                Date depTime = rowSet.getDate(5);
                Date arrTime = rowSet.getDate(6);
                Time totalFlyTime =  rowSet.getTime(7);
                int flightStatus = rowSet.getInt(8);
                int SAF = rowSet.getInt(9);
                int SAB = rowSet.getInt(10);
                int SAE = rowSet.getInt(11);
                Double ticketPrice = rowSet.getDouble(12);
                Flight f1 = new Flight(fid, airlineName, depCode, arrCode, depTime, arrTime, totalFlyTime, flightStatus, SAF, SAB, SAE, ticketPrice, "");
                alliance.add(f1);
            }
        }  finally {
            connection.close();
        }
        boolean endit = false;
        for(Flight ff : alliance) {
            if(!flights.isEmpty()) {              
                if(!endit) {
                    if(ff.getArrivalCode().equals(f.getArrivalCode())) {
                        endit = true;
                        flights.add(ff);
                    } else {
                        flights.add(ff);
                    }
                }
            } else if(ff.getDepartureCode().equals(f.getDepartureCode()) && !ff.getArrivalCode().equals(f.getArrivalCode())) {
                flights.add(ff);
            } else if(ff.getDepartureCode().equals(f.getDepartureCode()) && ff.getArrivalCode().equals(f.getArrivalCode())) {
                flights.add(ff);
                endit = true;
            }
        }
        
        isRendered = true;
        return flights;
    }
    public ArrayList getFlights() {
        return flights;
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

    public Time getTotalFlyTime() {
        return totalFlyTime;
    }

    public void setTotalFlyTime(Time totalFlyTime) {
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
