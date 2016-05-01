package predictions.phases;

import java.util.Date;

public class Phase {

	private String phaseName;
	private Date phaseEnd;
	private String welcomePage;

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public Date getPhaseEnd() {
		return phaseEnd;
	}

	public void setPhaseEnd(Date phaseEnd) {
		this.phaseEnd = phaseEnd;
	}

	public String getWelcomePage() {
		return welcomePage;
	}

	public void setWelcomePage(String welcomePage) {
		this.welcomePage = welcomePage;
	}

}
