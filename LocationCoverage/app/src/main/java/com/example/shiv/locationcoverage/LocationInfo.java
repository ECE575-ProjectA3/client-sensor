package com.example.shiv.locationcoverage;

/**
 * Created by shivu on 3/22/2015.
 */
public class LocationInfo {
    double m_longitude;
    double m_latitude;

    public void setLongitude(double longitude) {
        m_longitude = longitude;
    }

    public void setLatitude(double latitude) {
        m_latitude = latitude;
    }

    public double getLongitude() {
        return m_longitude;
    }

    public double getLatitude() {
        return m_latitude;
    }

    public boolean isChnaged(LocationInfo rhs) {
        if (rhs == null) {
            return true;
        }
        if (m_longitude != rhs.m_longitude || m_latitude != rhs.m_latitude) {
            return true;
        }
        return false;
    }
}
