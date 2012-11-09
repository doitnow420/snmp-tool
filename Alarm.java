public interface Alarm {

	// buzzer on 
	public abstract boolean buzzerOn();

	// buzzer off
	public abstract boolean buzzerOff();

	// red LED on
	public abstract boolean redOn();

	// red LED off
	public abstract boolean redOff();

	// yellow LED on
	public abstract boolean yellowOn();

	// yellow LED off
	public abstract boolean yellowOff();

	// green LED on 
	public abstract boolean greenOn();

	// green LED off
	public abstract boolean greenOff();

	// turn off everything.
	public abstract boolean setOffAll();

	// set alarm at once
	public abstract boolean setAlarm(boolean red, boolean yellow,
			boolean green, boolean buzzer);

}