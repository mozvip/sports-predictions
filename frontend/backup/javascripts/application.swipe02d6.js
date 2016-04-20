// application.swipe.js

var swipify = application.Class.extend({
	init: function() {
		var self = this;

		self.paginationPips();
		self.initSwipe();
		self.addEvents();
	},
	addEvents: function() {
		$('#Stages nav > ul > li').on('click', this.triggerSwipe.bind(this));
	},
	initSwipe: function() {

		application.mySwipe = new Swipe(document.getElementById('slider'), {
			startSlide: 0,
			speed: 500,
			auto: false,
			continuous: false,
			disableScroll: false,
			stopPropagation: false,
			callback: function(pos) {

				var t = application.mySwipe.getNumSlides();

				$('#Pips li').removeClass('active');
				$('#Pips li').eq(pos).addClass('active');

				$('#Stages nav > ul > li').removeClass('active');
				$('#Stages nav > ul > li').eq(pos).addClass('active');


			},
			transitionEnd: function(index, elem) {}
		});		
	},
	paginationPips: function() {

		var groupCount = $('#Stages nav > ul > li').length,
			items = [];

		$('<div id="Pagination" class="pagination" />').insertBefore('.bottom-actions');
		$('<button class="swipe-nav prev" onclick="application.mySwipe.prev()">Previous Group</button>').appendTo('#Pagination');
		$('<ul id="Pips" class="pips" />').appendTo('#Pagination');
		for (i = 0, len = groupCount; i < len; i++) {
			items.push('<li><a href="#">' + application.groupData.groupNames[i] + '</a></li>');
		}
		$('#Pips').append(items.join(''));
		$('#Pips li:first').addClass('active');
		$('<button class="swipe-nav next" onclick="application.mySwipe.next()">Next Group</button>').appendTo('#Pagination');
	},
	triggerSwipe: function(e) {

		var index = $(e.currentTarget).index(),
			items = [];

			e.preventDefault();

		application.mySwipe.slide(index, 500)

	},
});