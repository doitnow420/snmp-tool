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

public class AlarmKeiko implements Alarm {
	private String ipAddress;
	private String port;

	private String community;
	private String mib = "1.3.6.1.4.1.1333.1.2.1.1";
	private boolean statsRed, statsYellow, statsGreen, statsBuzzer;
	private OID OidCtrlRed, OidCtrlYellow, OidCtrlGreen, OidCtrlBuzzer, OidACOP;

	private CommunityTarget comtarget;
	private PDU pdu;
	private int snmpVersion = SnmpConstants.version2c;
	private Snmp snmp;
	private DefaultUdpTransportMapping transport;

	private final int OFF = 2;
	private final int ON = 1;

	static Logger logger_ = Logger.getLogger("AlarmPatlite");

	// constructor with IP address
	public AlarmKeiko( String ip ) {
		ipAddress = ip;
		port = "161";
		community = "private";

//		logger_.addHandler(new StreamHandler() {
//			{
//				setOutputStream(System.out);
//				setLevel(Level.ALL);
//			}
//		});
//		logger_.setUseParentHandlers(false);
//		logger_.setLevel(Level.ALL);

		OidCtrlRed =     new OID(mib + ".17.0");
		OidCtrlYellow =  new OID(mib + ".18.0");
		OidCtrlGreen =   new OID(mib + ".19.0");
		OidCtrlBuzzer =  new OID(mib + ".20.0");
		OidACOP =        new OID(mib + ".25.0");

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
	public AlarmKeiko( String ip, String comm ) {
		ipAddress = ip;
		port = "161";
		community = comm;

		OidCtrlRed =     new OID(mib + ".17.0");
		OidCtrlYellow =  new OID(mib + ".18.0");
		OidCtrlGreen =   new OID(mib + ".19.0");
		OidCtrlBuzzer =  new OID(mib + ".20.0");
		OidACOP =        new OID(mib + ".25.0");

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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlBuzzer, new Integer32(ON))};
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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlBuzzer, new Integer32(OFF))};
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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlRed, new Integer32(ON))};
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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlRed, new Integer32(OFF))};
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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlYellow, new Integer32(ON))};
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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlYellow, new Integer32(OFF))};
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

		VariableBinding[] vbs = {new VariableBinding(OidCtrlGreen, new Integer32(ON))};
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
		VariableBinding[] vbs = {new VariableBinding(OidCtrlGreen, new Integer32(OFF))};
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

		VariableBinding[] vbs = {new VariableBinding(OidACOP, new OctetString("00000000"))};
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
									  new VariableBinding(OidCtrlYellow, new Integer32( (yellow)?ON:OFF )),
									  new VariableBinding(OidCtrlGreen, new Integer32( (green)?ON:OFF )),
									  new VariableBinding(OidCtrlBuzzer, new Integer32( (buzzer)?ON:OFF )),
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
		AlarmKeiko keiko = new AlarmKeiko( "172.22.68.16");

		int sleeptime = 1000;

		System.out.println("Initialize Alarm");
		keiko.setOffAll();
		System.out.println("Trying green on");
		keiko.greenOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying yellow on");
		keiko.yellowOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying red on");
		keiko.redOn();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}

//		System.out.println("Trying buzzer on");
//		keiko.buzzerOn();
//		try {
//			Thread.sleep(sleeptime);
//		} catch ( InterruptedException e ) {
//			Thread.currentThread().interrupt();
//		}
		System.out.println("Trying green off");
		keiko.greenOff();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying yellow off");
		keiko.yellowOff();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying red off");
		keiko.redOff();
		try {
			Thread.sleep(sleeptime);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
		}
		System.out.println("Trying buzzer off");
		keiko.buzzerOff();
		
		keiko.setAlarm(false,  false,  true, false);

	}
}
