<#-- @ftlvariable name="" type="predictor.views.UserListView" -->
<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">

  <body>

	<#include "navbar.ftl">

	<script>
		function deleteUser( email, community ) {
			$.ajax({
				type: "DELETE",
				url: "/api/user?email=" + email + "&community=" + community,
				contentType: "application/json",
				dataType: "json",
				async: true,
				beforeSend: function (xhr){ 
			        xhr.setRequestHeader('Authorization', 'Basic ' + localStorage.getItem('pred2014AuthToken')); 
			    },					
				success: function(data) {
					$('#row_' + email.replace('@', '_')).remove();
				},
				error: function(error) {
					localStorage.removeItem('pred2014AuthToken');
					console.log(error.responseText);
				}
			});		
		}
	</script>

    <div class="container">

		<table class="table">
		<#list users as user>
			<tr id="row_${user.email?replace("@", "_")}">
				<td>
					<button type="button" class="btn btn-default" onclick="if (confirm ('Are you sure you want to delete this user ?')) { deleteUser('${user.email}', '${user.community}');  };">
						<span class="glyphicon glyphicon-trash"></span>
					</button>
				</td>
				<td>
					<button type="button" class="btn btn-default" onclick="if (confirm ('Are you sure you want to delete this user ?')) { deleteUser('${user.email}', '${user.community}');  };">
						<span class="glyphicon glyphicon-heart"></span>
					</button>
				</td>
				<td>${user.name}</td>
				<td>${user.email}</td>
			</tr>
		</#list>
		</table>	


    </div> <!-- /container -->
    
    <#include "footer.ftl" >

  </body>
</html>
