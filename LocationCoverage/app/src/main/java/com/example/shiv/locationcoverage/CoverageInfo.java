package com.example.shiv.locationcoverage;

/**
 * Created by shivu on 3/22/2015.
 */
public class CoverageInfo {

    int m_signalStrengthLevel;

    public void setSignalStrengthLevel(int level) {
        m_signalStrengthLevel = level;
    }

    public int getSignalStrengthLevel() {
        return m_signalStrengthLevel;
    }

    public boolean isChanged(CoverageInfo rhs) {
        if (rhs == null) {
            return true;
        }
        if (m_signalStrengthLevel != rhs.m_signalStrengthLevel) {
            return true;
        }
        return false;
    }
}
