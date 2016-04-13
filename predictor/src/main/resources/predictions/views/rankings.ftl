<#-- @ftlvariable name="" type="predictor.views.RankingsView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">

    <div class="container contents">
    
    	<h2>${community}</h2>

		<table class="table">
			<thead>
				<td>Nom</td>
				<td>Score</td>
			</thread>
		<#list users as user>
			<tr id="row_${user.email?replace("@", "_")}">
				<td>${user.name}</td>
				<td>${user.currentScore}</td>
			</tr>
		</#list>
		</table>	


    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
