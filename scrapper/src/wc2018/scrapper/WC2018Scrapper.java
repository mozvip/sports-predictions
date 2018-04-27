package wc2018.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WC2018Scrapper {

    public void scrapMatches() throws IOException {

        Document doc = Jsoup.connect("http://www.fifa.com/worldcup/matches/").get();

        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

        List<Match> matches = new ArrayList<>();

        Elements fixtures = doc.select(".fixture");
        for (Element fixture : fixtures) {

            String dataId = fixture.attr("data-id");

            String dateStr = fixture.select(".fixture").text();

            System.out.println(fixture);

        }
        System.out.println( mapper.writeValueAsString( matches ) );
    }

    public static void main(String[] argv) throws IOException {
        WC2018Scrapper scrapper = new WC2018Scrapper();
        scrapper.scrapMatches();
    }

}
