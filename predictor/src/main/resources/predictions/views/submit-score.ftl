<#-- @ftlvariable name="" type="predictor.views.SubmitScoreView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">
	
    <div class="container contents well">
    
    	<form action="/api/score/submit" method="POST">
		  <div class="form-group">
		    <label for="email">Match</label>
		    <select class="form-control" name="matchId" id="selectMatch">
<#list games as game>
				<option value="${game.matchNum}">${game.homeTeam} - ${game.awayTeam}</option>
</#list>		    
		    </select>
		  </div>
		  <div class="form-group">
		    <label for="password">Home Score</label>
		    <input type="number" class="form-control" name="homeScore" required size="2" maxlength="2" />
		  </div>
		  <div class="form-group">
		    <label for="password">Away Score</label>
		    <input type="number" class="form-control" name="awayScore" required size="2" maxlength="2" />
		  </div>
		  <button type="submit" class="btn btn-primary">Submit Score</button>
		</form>

    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
