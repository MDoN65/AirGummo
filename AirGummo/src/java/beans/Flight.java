/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Mike
 */
@ManagedBean
@RequestScoped
public class Flight {

    /**
     * Creates a new instance of Flight
     */
    public Flight() {
        
    }
    private String flightId;
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
    private double ticketPrice;
    private String reasonCanceled;

    public Flight(String flightId, String airlineName, String departureCode, String arrivalCode, Date departureTime, Date arrivalTime, Double totalFlyTime, int flightStatus, int seatAvailableFirst, int seatAvailableBus, int seatAvailableEco, Double ticketPrice, String reasonCanceled) {
        this.flightId = flightId;
        this.airlineName = airlineName;
        this.departureCode = departureCode;
        this.arrivalCode = arrivalCode;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalFlyTime = totalFlyTime;
        this.flightStatus = flightStatus;
        this.seatAvailableFirst = seatAvailableFirst;
        this.seatAvailableBus = seatAvailableBus;
        this.seatAvailableEco = seatAvailableEco;
        this.ticketPrice = ticketPrice;
        this.reasonCanceled = reasonCanceled;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
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
