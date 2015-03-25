package com.example.shiv.locationcoverage;

/**
 * Created by shivu on 3/22/2015.
 */
public class CoverageInfo {

    int m_signalStrengthLevel;
    String m_networkProviderName;

    public void setSignalStrengthLevel(int level) {
        m_signalStrengthLevel = level;
    }

    public void setNetworkProviderName(String name) {
        m_networkProviderName = name;
    }

    public int getSignalStrengthLevel() {
        return m_signalStrengthLevel;
    }

    public String getNetworkProviderName() { return m_networkProviderName;}

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
