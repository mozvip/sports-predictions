package wc2018.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WC2018Scrapper {

    public String getName(Element nameElement) throws IOException {
        Elements name = nameElement.select("[itemprop=name]");
        String teamName = name.select("a").isEmpty() ? name.text() : name.select("a").text();
        Elements teamFlag = nameElement.select(".flagicon img");
        if (!teamFlag.isEmpty()) {
            String flagSet = teamFlag.first().attr("srcset");
            Path flagsDirectory = Paths.get("flags");
            Files.createDirectories(flagsDirectory);
        }
        return teamName;
    }

    public void scrapMatches() throws IOException {

        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/2018_FIFA_World_Cup").get();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        List<Match> matches = new ArrayList<>();

        Elements fixtures = doc.select("[itemtype=http://schema.org/SportsEvent");
        for (Element fixture : fixtures) {
            /*
            <div itemscope="itemscope" itemtype="http://schema.org/SportsEvent" style="clear: both; overflow: auto;">
            <div class="mobile-float-reset" style="float: left; width: 15%; padding: 2px 0; overflow: auto;"><time style="display: block; overflow: auto;"><span class="mobile-float-reset" style="display: block; float: right;">16&nbsp;June&nbsp;2018<span style="display:none">&nbsp;(<span class="bday dtstart published updated">2018-06-16</span>)</span></span><span class="mobile-float-reset" style="display: block; clear: right; float: right;">13:00 <a href="/wiki/Moscow_Time" title="Moscow Time">MSK</a> (<a href="/wiki/UTC%2B03:00" title="UTC+03:00">UTC+3</a>)</span></time></div>
            <table style="float: left; width: 61%; table-layout: fixed; text-align: center;">
            <tbody><tr itemprop="name" style="vertical-align: top;">
                <th style="width: 39%; text-align: right;" itemprop="homeTeam" itemscope="itemscope" itemtype="http://schema.org/SportsTeam">
                <span itemprop="name"><a href="/wiki/France_national_football_team" title="France national football team">France</a><span class="flagicon">&nbsp;<img alt="" src="//upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/23px-Flag_of_France.svg.png" width="23" height="15" class="thumbborder" srcset="//upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/35px-Flag_of_France.svg.png 1.5x, //upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/45px-Flag_of_France.svg.png 2x" data-file-width="900" data-file-height="600"></span></span></th>
            <th style="width: 22%;"><a href="/wiki/2018_FIFA_World_Cup_Group_C#France_vs_Australia" title="2018 FIFA World Cup Group C">Match 5</a></th>
            <th style="width: 39%; text-align: left;" itemprop="awayTeam" itemscope="itemscope" itemtype="http://schema.org/SportsTeam"><span itemprop="name"><span style="white-space:nowrap"><span class="flagicon"><img alt="" src="//upload.wikimedia.org/wikipedia/en/thumb/b/b9/Flag_of_Australia.svg/23px-Flag_of_Australia.svg.png" width="23" height="12" class="thumbborder" srcset="//upload.wikimedia.org/wikipedia/en/thumb/b/b9/Flag_of_Australia.svg/35px-Flag_of_Australia.svg.png 1.5x, //upload.wikimedia.org/wikipedia/en/thumb/b/b9/Flag_of_Australia.svg/46px-Flag_of_Australia.svg.png 2x" data-file-width="1280" data-file-height="640">&nbsp;</span><a href="/wiki/Australia_national_soccer_team" title="Australia national soccer team">Australia</a></span></span></th>
            </tr>
            <tr style="vertical-align: top; font-size: 85%;">
            <td style="text-align: right;"></td>
            <td><a rel="nofollow" class="external text" href="https://www.fifa.com/worldcup/matches/round=275073/match=300331533/report.html">Report</a></td>
            <td style="text-align: left;"></td>
            </tr>
            </tbody></table>
            <div class="mobile-float-reset" style="float: left; font-size: 85%; width: 24%; padding: 2px 0;">
            <div itemprop="location" itemscope="itemscope" itemtype="http://schema.org/Place"><span itemprop="name address"><a href="/wiki/Kazan_Arena" title="Kazan Arena">Kazan Arena</a>, <a href="/wiki/Kazan" title="Kazan">Kazan</a></span></div>
            </div>
            </div>
            */

            Element dateTime = fixture.select("time").first();
            String date = dateTime.select(".dtstart").text();
            LocalDate localDate = LocalDate.parse(date);

            String time = dateTime.select("span").last().text();
            Matcher timeMatcher = Pattern.compile("(\\d+:\\d+) (\\w+) \\((.*)\\)").matcher(time);
            LocalTime localTime = null;
            ZoneId zone = null;
            if (timeMatcher.matches()) {
                localTime = LocalTime.parse(timeMatcher.group(1));
                zone = ZoneId.of(timeMatcher.group(3));
            }

            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, zone);

            Element name = fixture.select("[itemprop=name]").first();

            System.out.println("Parsing " + name.text() );

            Element nameElement = name.select("th").get(1);
            String matchName = nameElement.text();

            String group = null;
            Elements links = nameElement.select("a");
            if (!(links).isEmpty()) {
                String groupText = links.first().attr("title");
                Matcher matcher = Pattern.compile("2018 FIFA World Cup (.+)").matcher(groupText);
                if (matcher.matches()) {
                    group = matcher.group(1);
                    if (group.startsWith("Group ")) {
                        group = group.substring(6);
                    } else {
                        group = null;
                    }
                }
            }


            Integer id = null;
            Matcher matcher = Pattern.compile("Match (\\d+)").matcher(matchName);
            if (matcher.matches()) {
                id = Integer.parseInt( matcher.group(1) );
            }

            Element homeTeam = name.select("[itemprop=hometeam]").first();
            String homeTeamName = getName(homeTeam);
            Elements homeFlag = homeTeam.select(".flagicon img");
            if (!homeFlag.isEmpty()) {
                String homeFlagSet = homeFlag.first().attr("srcset");
            }

            Element awayTeam = name.select("[itemprop=awayteam]").first();
            String awayTeamName = getName(awayTeam);
            Elements awayFlag = awayTeam.select(".flagicon img");
            if (!awayFlag.isEmpty()) {
                String awayFlagSet = awayFlag.first().attr("srcset");
            }

            String location = fixture.select("[itemprop=location]").text();

            // String dateTimeStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mmZ").format(zonedDateTime);

            Match match;

            Pattern winnersMatchPattern = Pattern.compile("Winners Match (\\d+)");
            Matcher homeMatcher = winnersMatchPattern.matcher(homeTeamName);
            Matcher awayMatcher = winnersMatchPattern.matcher(awayTeamName);
            if (homeMatcher.matches() && awayMatcher.matches()) {
                match = new Match(id, zonedDateTime, group, location, Integer.parseInt(homeMatcher.group(1)), Integer.parseInt(awayMatcher.group(1)));
            } else {
                match = new Match(id, zonedDateTime, group, location, homeTeamName, awayTeamName);
            }

            System.out.println(match.toString());

            matches.add(match);

        }
        Path path = Paths.get("games.json");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            bufferedWriter.write(mapper.writeValueAsString( matches ));
        }
    }

    public static void main(String[] argv) throws IOException {
        WC2018Scrapper scrapper = new WC2018Scrapper();
        scrapper.scrapMatches();
    }

}
