import java.util.logging.Logger;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class AlarmPatlite implements Alarm {
	private String ipAddress;
	private String port;

	private String community;
	private String mib = "1.3.6.1.4.1.20440.4.1";
	private boolean statsRed, statsYellow, statsGreen, statsBuzzer;
	private OID OidCtrlRed, OidCtrlYellow, OidCtrlGreen, OidCtrlBuzzer, OidClear;
	private OID OidTimerRed, OidTimerYellow, OidTimerGreen, OidTimerBuzzer;
	
	private CommunityTarget comtarget;
	private PDU pdu;
	private int snmpVersion = SnmpConstants.version2c;
	private Snmp snmp;
	private DefaultUdpTransportMapping transport;
	
	private final int OFF = 1;
	private final int ON = 2;
	
	Logger logger_ = Logger.getLogger("AlarmPatlite");

	// constructor with IP address
	public AlarmPatlite( String ip ) {
		ipAddress = ip;
		port = "161";
		community = "private";
		
		OidCtrlRed =     new OID(mib + ".5.1.2.1.2.1");
		OidTimerRed =    new OID(mib + ".5.1.2.1.3.1");
		OidCtrlYellow =  new OID(mib + ".5.1.2.1.2.2");
		OidTimerYellow = new OID(mib + ".5.1.2.1.3.2");
		OidCtrlGreen =   new OID(mib + ".5.1.2.1.2.3");
		OidTimerGreen =  new OID(mib + ".5.1.2.1.3.3");
		OidCtrlBuzzer =  new OID(mib + ".5.1.2.1.2.6");
		OidTimerBuzzer = new OID(mib + ".5.1.2.1.3.6");
		OidClear =       new OID(mib + ".5.1.3.0");

	    // Create Target Address object
	    comtarget = new CommunityTarget();
	    comtarget.setCommunity(new OctetString(community));
	    comtarget.setVersion(snmpVersion);
	    comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
	    comtarget.setRetries(2);
	    comtarget.setTimeout(1000);

	    // Create the PDU object
	    pdu = new PDU();

	    try {
	    	transport = new DefaultUdpTransportMapping();
	    	snmp = new Snmp(transport);
	    	transport.listen();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }

		statsRed = false;
		statsYellow = false;
		statsGreen = false;
		statsBuzzer = false;

		this.setOffAll();
	}

	// constructor with ip address and community name
	public AlarmPatlite( String ip, String comm ) {
		ipAddress = ip;
		port = "161";
		community = comm;
		
		OidCtrlRed =     new OID(mib + ".5.1.2.1.2.1");
		OidTimerRed =    new OID(mib + ".5.1.2.1.3.1");
		OidCtrlYellow =  new OID(mib + ".5.1.2.1.2.2");
		OidTimerYellow = new OID(mib + ".5.1.2.1.3.2");
		OidCtrlGreen =   new OID(mib + ".5.1.2.1.2.3");
		OidTimerGreen =  new OID(mib + ".5.1.2.1.3.3");
		OidCtrlBuzzer =  new OID(mib + ".5.1.2.1.2.6");
		OidTimerBuzzer = new OID(mib + ".5.1.2.1.3.6");
		OidClear =       new OID(mib + ".5.1.3.0");

	    // Create Target Address object
	    comtarget = new CommunityTarget();
	    comtarget.setCommunity(new OctetString(community));
	    comtarget.setVersion(snmpVersion);
	    comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
	    comtarget.setRetries(2);
	    comtarget.setTimeout(1000);

	    // Create the PDU object
	    pdu = new PDU();

	    try {
	    	transport = new DefaultUdpTransportMapping();
	    	snmp = new Snmp(transport);
	    	transport.listen();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }

		statsRed = false;
		statsYellow = false;
		statsGreen = false;
		statsBuzzer = false;

		this.setOffAll();
	}

	// buzzer on
	@Override
	public boolean buzzerOn() {

		if ( statsBuzzer ) {
			return true;
		}

		VariableBinding[] vbs = {new VariableBinding(OidCtrlBuzzer, new Integer32(ON)), 
								 new VariableBinding(OidTimerBuzzer, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
	    	statsBuzzer = true;
	    	return true;
	    }
	    return false;
	}


	// buzzer off
	@Override
	public boolean buzzerOff() {

		if ( ! statsBuzzer ) {
			return true;
		}

		VariableBinding[] vbs = {new VariableBinding(OidCtrlBuzzer, new Integer32(OFF)), 
								 new VariableBinding(OidTimerBuzzer, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) {
			statsBuzzer = false;
			return true;
		}
		return false;
	}

	// red LED on 
	/* (非 Javadoc)
	 * @see Alarm#redOn()
	 */
	@Override
	public boolean redOn() {
		
		if ( statsRed ) {
			return true;
		}
		
		VariableBinding[] vbs = {new VariableBinding(OidCtrlRed, new Integer32(ON)), 
								 new VariableBinding(OidTimerRed, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
	    	statsRed = true;
	    	return true;
	    }
	    return false;
	}
	
	// red LED off
	/* (非 Javadoc)
	 * @see Alarm#redOff()
	 */
	@Override
	public boolean redOff() {
		if ( ! statsRed ) {
			return true;
		}
		
		VariableBinding[] vbs = {new VariableBinding(OidCtrlRed, new Integer32(OFF)), 
								 new VariableBinding(OidTimerRed, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
	    	statsRed = false;
	    	return true;
	    }
	    return false;
	}

	// yellow LED on 
	/* (非 Javadoc)
	 * @see Alarm#yellowOn()
	 */
	@Override
	public boolean yellowOn() {
		if ( statsYellow ) {
			return true;
		}

		VariableBinding[] vbs = {new VariableBinding(OidCtrlYellow, new Integer32(ON)), 
								 new VariableBinding(OidTimerYellow, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
	    	statsYellow = true;
	    	return true;
	    }
	    return false;
	}

	// yellow LED off
	/* (非 Javadoc)
	 * @see Alarm#yellowOff()
	 */
	@Override
	public boolean yellowOff() {
		if ( ! statsYellow ) {
			return true;
		}
		
		VariableBinding[] vbs = {new VariableBinding(OidCtrlYellow, new Integer32(OFF)), 
								 new VariableBinding(OidTimerYellow, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
	    	statsYellow = false;
	    	return true;
	    }
	    return false;
	}

	// green LED on 
	@Override
	public boolean greenOn() {
		if ( statsGreen ) {
			return true;
		}
		
		VariableBinding[] vbs = {new VariableBinding(OidCtrlGreen, new Integer32(ON)), 
								 new VariableBinding(OidTimerGreen, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
	    	statsGreen = true;
	    	return true;
	    }
	    return false;
	}


	// green LED off
	@Override
	public boolean greenOff() {
		if ( ! statsGreen ) {
			return true;
		}
		VariableBinding[] vbs = {new VariableBinding(OidCtrlGreen, new Integer32(OFF)), 
								 new VariableBinding(OidTimerGreen, new Integer32(0))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
			statsGreen = false;
			return true;
		}
		return false;
	}

	// turn off everything.
	@Override
	public boolean setOffAll() {
		
		VariableBinding[] vbs = {new VariableBinding(OidClear, new Integer32(1))};
		pdu.setType(PDU.SET);
		pdu.addAll(vbs);
		
	    if ( this.sendSNMP() ) { 
			statsRed = false;
			statsYellow = false;
			statsGreen = false;
			statsBuzzer = false;
			return true;
		}
		return false;
	}

	// set alarm at once
	@Override
	public boolean setAlarm( boolean red, boolean yellow, boolean green, boolean buzzer ) {

		if( statsRed == red && statsYellow == yellow && statsGreen == green && statsBuzzer == buzzer ) {
			// status unchanged. just return
			return true;
		} else {
			VariableBinding[] vbs = { new VariableBinding(OidCtrlRed, new Integer32( (red)?ON:OFF )), 
									  new VariableBinding(OidTimerRed, new Integer32(0)),
									  new VariableBinding(OidCtrlYellow, new Integer32( (yellow)?ON:OFF )), 
									  new VariableBinding(OidTimerYellow, new Integer32(0)),
									  new VariableBinding(OidCtrlGreen, new Integer32( (green)?ON:OFF )), 
									  new VariableBinding(OidTimerGreen, new Integer32(0)),
									  new VariableBinding(OidCtrlBuzzer, new Integer32( (buzzer)?ON:OFF )), 
									  new VariableBinding(OidTimerBuzzer, new Integer32(0)),
									};
			pdu.setType(PDU.SET);
			pdu.addAll(vbs);
			
		    if ( this.sendSNMP() ) { 
				statsRed = red;
				statsYellow = yellow;
				statsGreen = green;
				statsBuzzer = buzzer;
				return true;
			}
		}
		return false;
	}

	// send snmp message
	private boolean sendSNMP() {
		boolean status = false;
		
		logger_.fine("Request:");
	    try {
	    	ResponseEvent response = snmp.set(pdu, comtarget);

	    	// Process Agent Response
	    	if (response != null)
	    	{
	    		logger_.fine("Response:");
	    		PDU responsePDU = response.getResponse();

	    		if (responsePDU != null)
	    		{
	    			int errorStatus = responsePDU.getErrorStatus();
	    			int errorIndex = responsePDU.getErrorIndex();
	    			String errorStatusText = responsePDU.getErrorStatusText();

	    			if (errorStatus == PDU.noError)
	    			{
	    				logger_.fine("Snmp Set Response = " + responsePDU.getVariableBindings());
	    				status = true;
	    			}
	    			else
	    			{
	    				logger_.fine("Error: Request Failed");
	    				logger_.fine("Error Status = " + errorStatus);
	    				logger_.fine("Error Index = " + errorIndex);
	    				logger_.fine("Error Status Text = " + errorStatusText);
	    			}
	    		}
	    		else
	    		{
	    			logger_.fine("Error: Response PDU is null");
	    		}
	    	}
	    	else
	    	{
	    		logger_.fine("Error: Agent Timeout... ");
	    	}

	    	pdu.clear();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return status;
	}

	public static final void main(String[] args) throws Exception {
		AlarmPatlite patlite = new AlarmPatlite( "172.22.68.18");

		int sleeptime = 100;
		
		System.out.println("Initialize Alarm");
		patlite.setOffAll();
		System.out.println("Trying green on");
		patlite.greenOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying yellow on");
		patlite.yellowOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying red on");
		patlite.redOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}

		System.out.println("Trying buzzer on");
		patlite.buzzerOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying green off");
		patlite.greenOff();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying yellow off");
		patlite.yellowOff();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying red off");
		patlite.redOff();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying buzzer off");
		patlite.buzzerOff();

	}
}
