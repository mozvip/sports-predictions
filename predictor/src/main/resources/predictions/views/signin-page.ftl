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
		  <div class="form-group">
		  	<label for="password" class="control-label">Mot de passe</label>
		    <input type="password" class="form-control" required name="password" id="password" placeholder="Mot de passe">
		  </div>
		  <div class="button-group">
		  	<button type="submit" class="btn btn-primary">Submit</button>
			<a href="/api/user/forget-password" class="btn btn-warning">J'ai oublié mon mot de passe!</a>
			</div>
		</form>
		

    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
