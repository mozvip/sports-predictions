package predictions.phases;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhaseManager {
	
	private final static Logger logger = LoggerFactory.getLogger( PhaseManager.class );
	private List<Phase> phases;
	private Phase currentPhase = null;
	
	private static class SingletonHolder {		
		private final static PhaseManager instance = new PhaseManager();
	}	
	
	private PhaseManager() {
		init();
	}

	public static PhaseManager getInstance() {
		return SingletonHolder.instance;
	}
	
	private void init() {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("phases.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			phases = mapper.readValue(input, new TypeReference<List<Phase>>(){});
			currentPhase = determineCurrentPhase();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}		
	}

	private synchronized Phase determineCurrentPhase() {
		Date now = new Date();
		Optional<Phase> optPhase = phases.stream().filter( phase -> phase.getPhaseEnd().after( now ) ).findFirst();
		if (optPhase.isPresent()) {
			return optPhase.get();
		}
		return null;
	}
	
	public Phase getCurrentPhase() {
		if (currentPhase == null || currentPhase.getPhaseEnd().before( new Date() )) {
			currentPhase = determineCurrentPhase();
		}
		return currentPhase;
	}

}
