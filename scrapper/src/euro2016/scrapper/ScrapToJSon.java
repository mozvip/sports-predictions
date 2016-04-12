package euro2016.scrapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

public class ScrapToJSon {
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
//		System.setProperty("http.proxyHost", "fr-proxy");
//		System.setProperty("http.proxyPort", "3128");		
//		
		Document doc = Jsoup.connect("http://www.lequipe.fr/Football/Euro/Saison-2016/calendrier-resultats.html").get();
		
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		
		List<Match> matches = new ArrayList<Match>();
		
		Elements sections = doc.select("section.mainDate");
		for (Element section : sections) {
			
			Elements rows = section.select("tr");
			
			String day = section.attr("data-date");
			
			for (Element row : rows) {
				
				Match match = new Match();
				
				String time = row.select(".heurematch").text();

				String startDate = day + "T" + time;
				match.setDateTime( startDate );

				match.setHomeTeam( row.select(".domicile").text() );
				match.setAwayTeam( row.select(".exterieur").text() );
				match.setGroup( row.select("span strong").text() );
				
				match.setStadium( row.select(".lieu").text() );
				
				match.setMatchNum( matches.size() );
				
				matches.add( match );
				
//				match.setStadium( section.select(".mu-i-stadium").text() );
//				match.setVenue( section.select(".mu-i-venue").text() );
//				
//				
//				String matchNumStr = section.select(".mu-i-matchnum").text();
//				Pattern pattern = Pattern.compile("Match (\\d+)");
//				Matcher matcher = pattern.matcher(matchNumStr);
//				if (matcher.matches()) {
//					
//					int matchNum = Integer.parseInt( matcher.group(1) ) ;
//					
//					match.setMatchNum( matchNum );
//
//					if (!matchNums.contains(matchNum)) {
//						
//						matchNums.add( matchNum );
//					}
//				}
				
			}
		}		
		System.out.println( mapper.writeValueAsString( matches ) );
	}

}
