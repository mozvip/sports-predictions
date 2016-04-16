<#-- @ftlvariable name="" type="predictor.views.ChangePasswordView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">
	
	<script>
		function changePassword( email, password ) {
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
		  	<label for="password" class="control-label">New Password</label>
		    <input type="password" class="form-control" required id="password" placeholder="Password">
		  </div>
		  <button type="submit" class="btn btn-primary">Submit</button>
		</form>

    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
