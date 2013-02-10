package com.arcbees.github;

import org.junit.Test;

/**
 * TODO add steps
 * [] pull branch
 * [] add commit and get its sha ref
 * run test
 * [] check status
 */
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
        args[2] = "-sha=2e84e6446df300cd572930869c5ed2be8ee1f614";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-buildid=299";
        
        PullNotification.main(args);
    }
    
    @Test
    public void testOptionalPending() {
        String[] args = new String[8];
        args[0] = "-ro=branflake2267";
        args[1] = "-rn=Sandbox";
        args[2] = "-sha=2e84e6446df300cd572930869c5ed2be8ee1f614";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-status=pending";
        args[7] = "-skipcomment=true";
        
        PullNotification.main(args);
    }
    
    @Test
    public void testOptionalSuccess() {
        String[] args = new String[8];
        args[0] = "-ro=branflake2267";
        args[1] = "-rn=Sandbox";
        args[2] = "-sha=2e84e6446df300cd572930869c5ed2be8ee1f614";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-status=success";
        args[7] = "-skipcomment=false";
        
        PullNotification.main(args);
    }
    
    @Test
    public void testOptionalFail() {
        String[] args = new String[8];
        args[0] = "-ro=branflake2267";
        args[1] = "-rn=Sandbox";
        args[2] = "-sha=2e84e6446df300cd572930869c5ed2be8ee1f614";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-status=failed";
        args[7] = "-skipcomment=true";
        
        PullNotification.main(args);
    }
    
    @Test
    public void testOptionalError() {
        String[] args = new String[8];
        args[0] = "-ro=branflake2267";
        args[1] = "-rn=Sandbox";
        args[2] = "-sha=2e84e6446df300cd572930869c5ed2be8ee1f614";
        args[3] = "-github=github";
        args[4] = "-teamcity=teamcity-gonevertical";
        args[5] = "-returnurl=http://teamcity.gonevertical.org";
        args[6] = "-status=error";
        args[7] = "-skipcomment=true";
        
        PullNotification.main(args);
    }
}
