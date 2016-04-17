<#-- @ftlvariable name="" type="predictor.views.ChangePasswordView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">
	
	<script>
		function checkAvailability( email ) {
			$.get( "/api/user/availability?email=" + email, function( data ) {
  				if (data === 'false') {
  					swal('This email is already taken');
  				}
			});
		}
	</script>	

    <div class="container contents well">

		<form role="form" action="/api/user/create" method="POST">
		  <div class="form-group">
		    <label for="community" class="control-label">Community</label>
		    <p class="form-control-static">${community}</p>
		  </div>
		  <div class="form-group">
		    <label for="email" class="control-label">Email address</label>
		    <input type="email" class="form-control" name="email" required id="email" placeholder="Enter email" onchange="checkAvailability(this.value)">
		  </div>
		  <div class="form-group">
		    <label for="name" class="control-label">Name (optional)</label>
		    <input type="text" class="form-control" name="name" id="name" placeholder="Enter name (optional)">
		  </div>
		  <div class="form-group">
		  	<label for="password" class="control-label">Password</label>
		    <input type="password" class="form-control" name="password" required id="password" placeholder="Password">
		  </div>
		  <div class="form-group">
		  	<label for="confirm-password" class="control-label">Confirm Password</label>
		    <input type="password" class="form-control" required id="confirm-password" placeholder="Confirm password">
		  </div>
		  <button type="submit" class="btn btn-primary" id="createUserButton">Create User</button>
		</form>

    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
