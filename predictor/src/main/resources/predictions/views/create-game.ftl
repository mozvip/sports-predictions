<#-- @ftlvariable name="" type="predictor.views.CreateGameView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">
	
	<script>
		function createUser( email, password ) {
			$.ajax({
				type: "POST",
				url: "/api/change-password",
				data: {"email":email, "password":password},
				async: true,
				beforeSend: function (xhr){ 
			        xhr.setRequestHeader('Authorization', 'Basic ' + localStorage.getItem('pred2014AuthToken')); 
			    },					
				success: function(data) {
				
				},
				error: function(error) {
					localStorage.removeItem('pred2014AuthToken');
					console.log(error.responseText);
				}
			});		
		}
	</script>	

    <div class="container contents well">

		<form role="form" onSubmit="changePassword($('#email').val(), $('#password').val());">
		  <div class="form-group">
		    <label for="community" class="control-label">Community</label>
		    <p class="form-control-static">${community}</p>
		  </div>
		  <div class="form-group">
		    <label for="email" class="control-label">Email address</label>
		    <input type="email" class="form-control" required id="email" placeholder="Enter email">
		  </div>
		  <div class="form-group">
		    <label for="name" class="control-label">Name (optional)</label>
		    <input type="text" class="form-control" id="name" placeholder="Enter name (optional)">
		  </div>
		  <div class="form-group">
		  	<label for="password" class="control-label">Password</label>
		    <input type="password" class="form-control" required id="password" placeholder="Password">
		  </div>
		  <div class="form-group">
		  	<label for="confirm-password" class="control-label">Confirm Password</label>
		    <input type="password" class="form-control" required id="confirm-password" placeholder="Confirm password">
		  </div>
		  <div class="checkbox">
			<label><input type="checkbox" value="">Administrator</label>
		  </div>		  
		  <button type="submit" class="btn btn-primary">Submit</button>
		</form>

    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
