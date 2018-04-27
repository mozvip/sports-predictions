package predictions.phases;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhaseManager implements Managed {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( PhaseManager.class );

	private List<Phase> phases;
	private Phase currentPhase = null;

    @Override
    public void start() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("phases.json");
        ObjectMapper mapper = new ObjectMapper();
        phases = mapper.readValue(input, new TypeReference<List<Phase>>(){});
        currentPhase = determineCurrentPhase();
    }

    @Override
    public void stop() throws Exception {

    }

	private Phase determineCurrentPhase() {
		Date now = new Date();
		Optional<Phase> optPhase = phases.stream().filter( phase -> phase.getPhaseEnd().after( now ) ).findFirst();
		return optPhase.orElse(null);
	}

	public Phase getCurrentPhase() {
		if (currentPhase == null || currentPhase.getPhaseEnd().before( new Date() )) {
			currentPhase = determineCurrentPhase();
		}
		return currentPhase;
	}

}
