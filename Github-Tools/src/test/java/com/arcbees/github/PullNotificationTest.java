package com.arcbees.github;

import org.junit.Test;

public class PullNotificationTest {
    @Test
    public void testHelp() {
        String[] args = new String[1];
        args[0] = "-help";
        
        PullNotification.main(args);
    }
    
    @Test
    public void testMandatoryAuto() {
        String[] args = new String[7];
        args[0] = "-ro=branflake2267";
        args[1] = "-rn=Sandbox";
        args[2] = "-sha=b5b3362f0a4d8c3bc05ba0170ea72c3f7dbdd620";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-buildid=299";
        
        PullNotification.main(args);
    }
    
    @Test
    public void testOptionalPending() {
        String[] args = new String[7];
        args[0] = "-ro=branflake2267";
        args[1] = "-rn=Sandbox";
        args[2] = "-sha=b5b3362f0a4d8c3bc05ba0170ea72c3f7dbdd620";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-status=pending";
        
        
        PullNotification.main(args);
    }
}
