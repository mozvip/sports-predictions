<#-- @ftlvariable name="" type="predictor.views.ChangePasswordView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">

    <div class="container contents">

		<form role="form" action="#" method="POST">
		  <div class="form-group">
		    <label for="email" class="control-label">Adresse mail</label>
		    <input type="email" class="form-control" required name="email" id="email" placeholder="Enter email">
		  </div>
		  <div class="button-group">
		  	<button type="submit" class="btn btn-primary">Submit</button>
			</div>
		</form>
		

    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
