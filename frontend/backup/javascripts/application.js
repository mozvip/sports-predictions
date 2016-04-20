$(document).ready(function() {
	
    $('#rankings').dataTable( {
        "ajax": "/api/user/list",
        "bSort": false,

        "columns": [
            { "data": "name" },
            { "data": "currentScore" }
        ]
    } );
    $('#rankings-wrapper').hide();
	
	PNotify.prototype.options.styling = "jqueryui";

	application.ui = {

		_init: function() {
			this._genericEvents();
			this.initMobileLayout();
			this.predictor();
			this.initSwipe();
		},
		_genericEvents: function() {
			var self = this,
				handleKeyUp = self.handleKeyUp.bind(this);

			$(document).on('keyup', '.match input', handleKeyUp);

			$(window).resize(function() {
				if (this.resizeTo) clearTimeout(this.resizeTo);
				this.resizeTo = setTimeout(function() {
					$(this).trigger('WINDOW:RESIZEEND');
				}, 200);
			})

			$(window).on('WINDOW:RESIZEEND', this.onWindowResize.bind(this));
			$(window).trigger('resize');

			$('.group').each(function(e) {
				var $input = $(this).find('input:last');
				var TABKEY = 9;
				$input.keydown(function(e) {
					if (e.which == TABKEY) {
						e.preventDefault();
						return false;
					}
				});
			});

		},
		initMobileLayout: function() {

			var c = '', d;
			$('.finals .match').each(function(i) {
				c = i % 2 === 0 ? 'left' : 'right';
				$(this).addClass(c)
			});

			$('.finals .f-c-4 .right').removeClass('right');
			$('.finals .f-c-4 .left').removeClass('left');

			$('.finals .f-c-4 .match .team-1').addClass('left');
			$('.finals .f-c-4 .match .team-2').addClass('right');

			$('.finals .f-c-1 .match').eq(2).addClass('bottom-row');
			$('.finals .f-c-1 .match').eq(3).addClass('bottom-row');

			$('.finals .f-c-7 .match').eq(2).addClass('bottom-row');
			$('.finals .f-c-7 .match').eq(3).addClass('bottom-row');

			$('.finals .f-c-6 .match').eq(0).addClass('bottom-row');
			$('.finals .f-c-6 .match').eq(1).addClass('bottom-row');

			$('.finals .f-c-2 h4.left span').text('I');

			var i = 1, j=5;

			$('.f-c-1 .match.left .match-number').each(function(a, b){
				$(this).text(i);
				i++;
			})

			$('.f-c-1 .match.right .match-number').each(function(a, b){
				$(this).text(j);
				j++;
			})

			$('.f-c-7 .match.left .match-number').each(function(a, b){
				$(this).text(i);
				i++;
			})

			$('.f-c-7 .match.right .match-number').each(function(a, b){
				$(this).text(j);
				j++;
			})

			$('.f-c-2 .match.left .match-number').text('A')
			$('.f-c-2 .match.right .match-number').text('B')

			$('.f-c-6 .match.left .match-number').text('C')
			$('.f-c-6 .match.right .match-number').text('D')

			$('.f-c-3 .match.left .match-number').text('I')
			$('.f-c-5 .match.right .match-number').text('II')



		},
		initSwipe: function() {

			application.swipify = new swipify();

		},
		onWindowResize: function() {

			var self = this;

			if (Modernizr.mq('(max-width: 43.75em)')) {

				if (self.curr === 'small') {
					return;
				} else {
					self.curr = 'small';
				}

				$(".f-c-7").insertAfter(".f-c-1");
				$(".f-c-6").insertAfter(".f-c-2");
				$(".f-c-5").insertAfter(".f-c-3");

				bumpUI();

			} else {
				if (self.curr === 'lge') {
					return;
				} else {
					self.curr = 'lge';
				}

				$(".f-c-5").insertAfter(".f-c-4");
				$(".f-c-6").insertAfter(".f-c-5");
				$(".f-c-7").insertAfter(".f-c-6");

				bumpUI();
			}

			function bumpUI() {
				$('#Finals *').addClass('uibump');
				setTimeout(function() {
					$('#Finals *').removeClass('uibump');
				}, 50);
			}

		},

		predictor: function() {

			var predictions = window.predictionData || undefined;

			application.groupData = new dataStore({
				type: 'json',
				data: teams,
				categorize: 'GroupName'
			});

			application.groupNames = application.groupData.groupNames;

			application.groups = {};

			$(application.groupNames).each(function(i) {
				application.groups[application.groupNames[i]] = new group({
					container: '.group-' + application.groupNames[i],
					name: application.groupNames[i]
				});
			});


			$('.save-btn').show();

			application.prediction = new prediction({
				data: matches
			});

			application.knockout = new knockout();

			application.savepredictionforms = new savePredictionForms();

		},
		handleKeyUp: function(e) {

			var value = e.target.value;
			e.target.value = value.replace(/[^0-9\.]/g, '');

		}
	};
	/*
	 * Run the application interface once all code and DOM is loaded
	 */
	application.ui._init();
	
	if(typeof page_init == 'function') {
		page_init();
	}
});