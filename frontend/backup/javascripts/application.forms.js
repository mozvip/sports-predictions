// application.forms.js

var savePredictionForms = application.Class.extend({
	init: function() {
		var self = this;
		self.addEvents();
		
		$('.login-btn').hide();
		$('.logout-btn').hide();

		if (localStorage.getItem('pred2014AuthToken')) {
			
			$('.logout-btn').show();
			
			$.ajax({
				type: "GET",
				url: "/api/user/predictions",
				contentType: "application/json",
				dataType: "json",
				async: true,
				beforeSend: function (xhr){ 
			        xhr.setRequestHeader('Authorization', 'Basic ' + localStorage.getItem('pred2014AuthToken')); 
			    },					
				success: function(data) {
					self.updatePredictions(data.match_predictions_attributes);
				},
				error: function(error) {
					localStorage.removeItem('pred2014AuthToken');
					console.log(error.responseText);
				}
			});	
		}
		
		if (!localStorage.getItem('pred2014AuthToken')) {
			$('.login-btn').show();
		}
		
	},
	addEvents: function() {
		
		$('.rankings-btn').on('click', function(e) {
			$('#Forms').hide();
			$('.login-form').hide();
			$('#Pagination').hide();
			$('#Stages').hide();
			$('#rankings-wrapper').show();
		});

		$('.save-btn').on('click', function(e) {

			if (localStorage.getItem('pred2014AuthToken')) {
				
				var pArray = [],
					prediction = application.prediction.gamePredictions;
				
				for (var key in prediction) {
					pArray.push(prediction[key]);
				}				
				
				pObj = {
						"match_predictions_attributes": pArray
				};				
				
				$.ajax({
					type: "POST",
					url: "/api/user/save",
					contentType: "application/json",
					data: JSON.stringify(pObj), 
					async: true,
					beforeSend: function (xhr){ 
				        xhr.setRequestHeader('Authorization', 'Basic ' + localStorage.getItem('pred2014AuthToken')); 
				    },					
					success: function(data) {

					    new PNotify({
					        title: 'Pronostics sauvegard&eacute;s',
					        text: 'Vos pronostics ont &eacute;t&eacute; sauvegard&eacute;s.',
					        type: 'info',
					        buttons: {
					        	sticker: false
					        	}					        
					        });

					},
					error: function(error) {
						console.log(error.responseText)
					}
				});				
				
			} else {
				$('#Forms').show();
				$('.save-prediction-form').show();
				$('.login-form').hide();
				$('#Pagination').hide();
				$('#Stages').hide();
			}
			
		});

		$('.save-prediction-form .submit').on('click', this.processPrediction.bind(this));

		$('.logout-btn').on('click', function(e) {
			localStorage.removeItem('pred2014AuthToken');
			window.location = window.location;
		});

		$('.login-btn').on('click', function(e) {
			$('#Forms').show();
			$('#rankings-wrapper').hide();
			$('.save-prediction-form').hide();
			$('.login-form').show();
			$('#Pagination').hide();
			$('#Stages').hide();
		});
		
		$('.login-form .submit').on('click', this.processLogin.bind(this));

		$('.form .cancel').on('click', function(e) {
			e.preventDefault();
			$('#Forms').hide();
			$('.forms').hide();
			$('#Stages').show();
			document.getElementById('SubmitYourPrediction').reset();
			$('#Pagination').show();
		});

	},
	updatePredictions: function(predictions) {
		
		$('.login-btn').hide();
		$('.logout-btn').show();
		
		for (var i=0; i<predictions.length; i++) {
			var prediction = predictions[i];
			var parentDIV = $('.match.matchID-' + prediction.match_id);
			$('input.score.team-1', parentDIV).val(prediction.home_score);
			$('input.score.team-2', parentDIV).val(prediction.away_score);
			application.prediction.gamePredictions[prediction.match_id].home_score = prediction.home_score;
			application.prediction.gamePredictions[prediction.match_id].away_score = prediction.away_score;

			for ( var match_index=0; match_index < matches.length; match_index ++) {
				if (matches[ match_index ].match.id == prediction.match_id) {
					if (matches[ match_index ].match.GroupName != '') {
						application.groups[ matches[ match_index ].match.GroupName ].update({
							match: prediction.match_id,
							home_score: (prediction.home_score === null) ? null : prediction.home_score,
							home_team_id: prediction.home_team_id,
							away_score: (prediction.away_score === null) ? null : prediction.away_score,
							away_team_id: prediction.away_team_id
						});
					}
					break;
				}
			}

		}		
	},	
	processLogin : function(e) {
		
		var self = this,
			pObj,
			email = $('input[name="LoginEmail"]').val(),
			password = $('input[name="LoginPassword"]').val(),
			error = false;

		e.preventDefault();
	
		if (email === '') {
			$('input[name="LoginEmail"]').addClass('error');
			error = true;
		}
		if (password === '') {
			$('input[name="LoginPassword"]').addClass('error');
			error = true;
		}

		if (error === true) {
			return;
		} else {
			$('input.error').removeClass('error');
		}

		pObj = {
				"email": email,
				"password": password
		};
	
		this.executeLogin( pObj );

	},
	executeLogin: function( pObj ) {
		$.ajax({
			type: "POST",
			url: "/api/user/signin",
			data: pObj, 
			async: true,
			success: function(data) {
				if (data && data.authToken) {
					localStorage.setItem('pred2014AuthToken', data.authToken);
					window.location = window.location;
				} else {
				    new PNotify({
				        title: 'Erreur d\'authentification',
				        text: 'Votre email ou votre mot de passe est incorrect.',
				        type: 'error',
				        icon: 'ui-icon ui-icon-signal-diag'
				        });
				}
			},
			error: function(error) {
				localStorage.removeItem('pred2014AuthToken');
				console.log(error.responseText)
			}
		});		
	},
	processPrediction: function(e) {

		var self = this,
			prediction = application.prediction.gamePredictions,
			pArray = [],
			pObj,
			name = $('input[name="FullName"]').val(),
			email = $('input[name="EmailAddress"]').val(),
			password = $('input[name="Password"]').val(),
			confirmPassword = $('input[name="ConfirmPassword"]').val(),
			validEmail = false,
			error = false;

		e.preventDefault();

		if (name === '') {
			$('input[name="FullName"]').addClass('error');
			error = true;
		}
		if (email === '') {
			$('input[name="EmailAddress"]').addClass('error');
			error = true;
		}
		if (password === '') {
			$('input[name="Password"]').addClass('error');
			error = true;
		}
		if (confirmPassword === '' || confirmPassword != password) {
			$('input[name="ConfirmPassword"]').addClass('error');
			error = true;
		}

		validEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
		if(!validEmail){
			$('input[name="EmailAddress"]').addClass('error');
			error = true;
		}

		if (error === true) {
			return;
		} else {
			$('input.error').removeClass('error');
		}

		for (var key in prediction) {
			pArray.push(prediction[key]);
		}

		pObj = {
				"email": email,
				"name": name,
				"password": password,
				"match_predictions_attributes": pArray
		};

		this.postForm(pObj);

	},
	postForm: function(pObj) {

		$.ajax({
			type: "POST",
			url: "/api/user/create",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(pObj), 
			async: true,
			success: function(data) {
				if (data.authToken) {
					localStorage.setItem( 'pred2014AuthToken', data.authToken );
					window.location = window.location;
				} else {
				    new PNotify({
				        title: 'Impossible de cr&eacute;er le compte',
				        text: data.message,
				        type: 'error',
				        icon: 'ui-icon ui-icon-signal-diag',
				        buttons: {
				        	sticker: false
				        	}
				        });					
				}
			},
			error: function(error) {
				console.log(error.responseText)
			}
		});
	}

});