package com.rokid.cloudappclient.httpdns;

public interface DegradationFilter {
    boolean shouldDegradeHttpDNS(String hostName);
}
