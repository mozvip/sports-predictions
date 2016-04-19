// application.lib.js
// Classes for components to be re-used across the website
var dataStore = application.Class.extend({
	init: function(c) {
		var self = this,
			groupNames = null,
			groupTeamData = null,
			groupTableData = null,
			groupWinners = null;

		self.generateGroupsWithTeams(c.data, c.categorize);
	},
	generateGroupsWithTeams: function(d, c) {

		var self = this,
			groupGames = _.filter(d, function(a) {
				return a.team[c] !== null
			}),
			groupNames = [],
			groupTeamData = {};

		$(groupGames).each(function(i) {
			groupNames.push(groupGames[i].team[c]);
		});

		groupNames = _.uniq(groupNames).sort();

		this.groupNames = groupNames;

		$(groupNames).each(function(i) {

			var teamData = groupTeamData[groupNames[i]] = {};

			$(groupGames).each(function(j) {
				//teamData.push(groupGames[j].team);
				if (groupNames[i] == groupGames[j].team[c]) {
					var ref = groupGames[j].team.team_ref,
						team = groupGames[j].team,
						obj = {},
						teamObj = {};

					obj["Position"] = team.Position;
					obj["Name"] = team.name;
					obj["Played"] = team.Played;
					obj["Won"] = team.Won;
					obj["Drawn"] = team.Lost;
					obj["Lost"] = team.Played;
					obj["For"] = team.For;
					obj["Against"] = team.Against;
					obj["GD"] = team.For - team.Against;
					obj["Points"] = team.Points;
					obj["Flag"] = team.team_ref;

					teamObj[ref] = obj;
					$.extend(teamData, teamObj);

				}
			});

		});

		self.groupTeamData = groupTeamData;
		//console.log(groupTeamData)

		self.generateGroupTables();
	},
	generateGroupTables: function() {

		var self = this,
			groupTeamData = this.groupTeamData,
			groupTableData = {},
			groupWinners = {},
			count;

		for (var key in groupTeamData) {

			var tableData = groupTableData[key] = [];

			if (groupTeamData.hasOwnProperty(key)) {
				var obj = groupTeamData[key];
				//console.log(obj)
				for (var prop in obj) {
					if (obj.hasOwnProperty(prop)) {
						tableData.push(obj[prop])
					}
				}
			}
		}

		for (var key in groupTableData) {

			groupTableData[key].sort(function(a, b) {
				var response = 1;
				if (a.Points > b.Points) {
					response = -1;
				}
				// Sort in GD
				if (a.Points == b.Points) {
					if ((a.For - a.Against) == (b.For - b.Against)) {
						// Goal difference is the same so see who scored the most goals if possible
						if (a.For == b.For) {
							// Both teams scored the same goals in the group
							// Compare which team took the most points from the other by find the
							// match where they compete and checking the score for a winner

							$('.group' + key + ' .match').each(function() {
								var found = false;
								if (($('input.team-1', this).attr('name') == a.team_ref && $('input.team-2', this).attr('name') == b.team_ref) || ($('input.team-1', this).attr('name') == b.team_ref && $('input.team-2', this).attr('name') == a.team_ref)) {
									found = true;
								}
								if (found) {
									// Found team check for draw if not a draw check who one and they get the position
									if ($('input.team-1', this).val() == $('input.team-2', this).val()) {
										// DRAWING OF LOTS!!
									} else {
										if ($('input.team-1', this).attr('name') == a.team_ref) {
											if ($('input.team-1', this).val() > $('input.team-2', this).val()) {
												response = -1;
											}
										} else {
											if ($('input.team-2', this).val() > $('input.team-1', this).val()) {
												response = -1;
											}
										}
									}
								}
							});

						} else {
							if (a.For > b.For) {
								// Who scored the most goals?
								response = -1;
							}
						}
					} else {
						if ((a.For - a.Against) > (b.For - b.Against)) {
							// Goal difference exists so use this
							response = -1;
						}
					}
				}
				return response;
			});
		}

		count = 0;
		for (var key in groupTableData) {
			var groupName = this.groupNames[count];
			groupWinners[groupName] = [groupTableData[key][0]['Name'], groupTableData[key][1]['Name']];
			count++;
		}
		self.groupTableData = groupTableData;
		self.groupWinners = groupWinners;
	}
});

var prediction = application.Class.extend({
	init: function(c) {

		var self = this,
			gamePredictions;

		if (c.data) {
			gamePredictions = {}
			self.generateGames(c.data);
		} else {
			//self.gamePredictions = c;
		}

		self.addEvents();
	},
	addEvents: function() {

		var self = this,
			handleInputChange = self.handleInputChange.bind(this);

		$(document).on('keyup', '.match input', handleInputChange)

	},
	generateGames: function(d) {

		var self = this,
			gameIDs = {};

		$(d).each(function(i) {

			var obj = {},
				match = d[i].match;

			obj["match_id"] = d[i].match.id;
			obj["home_score"] = d[i].match.home_score;
			obj["away_score"] = d[i].match.away_score;
			obj["home_team_id"] = d[i].match.home_team_id;
			obj["away_team_id"] = d[i].match.away_team_id;

			gameIDs[d[i].match.id] = obj;
		});

		self.gamePredictions = gameIDs;
		//console.log(self.gamePredictions)
	},
	handleInputChange: function(e) {

		var isNum = /^\d+$/.test(e.target.value);
		if (!isNum) {
			return
		}

		var match = $(e.currentTarget).attr('data-match') || e.currentTarget.id,
			readOnly = $(e.currentTarget).attr('readonly'),
			parent = '.matchID-' + match,
			home_score = $('.team-1 input', parent).val(),
			home_team_id = $('.team-1 input', parent).attr('name'),
			away_score = $('.team-2 input', parent).val(),
			away_team_id = $('.team-2 input', parent).attr('name'),
			event = e.originalEvent;

		if (readOnly) {
			return;
		}

		this.gamePredictions[match]['home_score'] = (home_score) ? parseInt(home_score) : 0;
		this.gamePredictions[match]['away_score'] = (away_score) ? parseInt(away_score) : 0;
		this.gamePredictions[match]['home_team_id'] = (home_team_id) ? home_team_id : 0;
		this.gamePredictions[match]['away_team_id'] = (away_team_id) ? away_team_id : 0;

	}
});

var savedPrediction = application.Class.extend({
	init: function(data) {

		var self = this;

		this.generateGames(data);

	},
	generateGames: function(d) {

		var self = this,
			games = d['prediction']['match_predictions'];

		$(games).each(function(i) {
			var t1 = games[i].home_team_id,
				t1score = games[i].home_score,
				t2 = games[i].away_team_id,
				t2score = games[i].away_score,
				p = '.matchID-' + games[i].match_id;
			$('input[name="' + t1 + '"]', p).val(t1score).trigger('keyup');
			$('input[name="' + t2 + '"]', p).val(t2score).trigger('keyup');
			$('input', p).removeClass('score-editable').attr('readonly', 'readonly')
			$(p).addClass('match-complete');

		});

		$('#Content header h1 .predictor').text(d['prediction']['title']);

	}

});

var group = application.Class.extend({
	init: function(c) {
		var self = this;
		self.groupName = c.name;
		self.element = $(c.container);
		self.tableElement = $('table', self.element);
		self.inputs = $('input.score', self.element);
		self.editableInputs = $('input.score-editable', self.element);
		self.html = {
			tableHead: '<tr><th>&nbsp;</th><th>P</th><th>W</th><th>D</th><th>L</th><th>F</th><th>A</th><th>GD</th><th>PTS</th></tr>'
		},
		self.render(c);
		self.inputs.on('keyup', self.handleInputChange.bind(this));
		self.predicted = {};

	},
	render: function() {
		var self = this,
			tableHTML = self.html.tableHead,
			teams = application.groupData.groupTableData[self.groupName];

		$(teams).each(function(i) {
			tableHTML += '<tr>';
			/*			tableHTML += '<td class="name"><a href="' + teams[i].Name + '" target="_parent" class="team-flag-' + teams[i].Flag + '">' + teams[i].Name + '</a></td>'; */
			tableHTML += '<td class="name"><span class="team-flag-' + teams[i].Flag + '">' + teams[i].Name + '</span></td>';
			tableHTML += '<td>' + teams[i].Played + '</td>';
			tableHTML += '<td>' + teams[i].Won + '</td>';
			tableHTML += '<td>' + teams[i].Drawn + '</td>';
			tableHTML += '<td>' + teams[i].Lost + '</td>';
			tableHTML += '<td>' + teams[i].For + '</td>';
			tableHTML += '<td>' + teams[i].Against + '</td>';
			tableHTML += '<td>' + (teams[i].For - teams[i].Against) + '</td>';
			tableHTML += '<td>' + teams[i].Points + '</td>';
			tableHTML += '</tr>';
		});
		self.tableElement.html(tableHTML);
		$('tr:odd', self.tableElement).addClass('alt');
	},
	handleInputChange: function(e) {

		var isNum = /^\d+$/.test(e.target.value);
		if (!isNum) {
			return
		}

		var match = $(e.currentTarget).attr('data-match'),
			parent = '.matchID-' + match,
			home_score = $('input.team-1', parent).val() || null,
			home_team_id = $('input.team-1', parent).attr('name'),
			away_score = $('input.team-2', parent).val() || null,
			away_team_id = $('input.team-2', parent).attr('name');

		this.update({
			match: parseInt(match),
			home_score: (home_score === null) ? null : parseInt(home_score),
			home_team_id: home_team_id,
			away_score: (away_score === null) ? null : parseInt(away_score),
			away_team_id: away_team_id
		})
	},
	update: function(o) {
		var self = this,
			group = application.groupData.groupTeamData[self.groupName], //should this be
			predicted = self.predicted,
			hScore = o.home_score,
			aScore = o.away_score,
			homeTeam = o.home_team_id,
			awayTeam = o.away_team_id,
			homeTeamArgs = {},
			awayTeamArgs = {},
			result;

		//Match exists, remove the old data before adding new
		//
		if (predicted[o.match] !== undefined) {
			var lastScores = predicted[o.match]["scores"],
				operator = 'decrement';

			args(lastScores[0], lastScores[1]);
			scores(homeTeam, homeTeamArgs, operator);
			scores(awayTeam, awayTeamArgs, operator);
			application.groupData.generateGroupTables();
			delete self.predicted[o.match];
		}

		//new data

		if (hScore !== null && aScore !== null) {
			self.predicted[o.match] = {
				"scores": [hScore, aScore]
			}
			args(hScore, aScore);
			scores(homeTeam, homeTeamArgs);
			scores(awayTeam, awayTeamArgs);
			application.groupData.generateGroupTables();
		}

		self.render();
		application.knockout.refresh();

		function args(hScore, aScore) {

			if (hScore == aScore) {
				result = 'draw';
				homeTeamArgs = {
					"Against": aScore,
					"Drawn": 1,
					"For": hScore,
					"Played": 1,
					"Points": 1
				};
				awayTeamArgs = {
					"Against": hScore,
					"Drawn": 1,
					"For": aScore,
					"Played": 1,
					"Points": 1
				};
			} else {
				if (hScore > aScore) {
					homeTeamArgs = {
						"Against": aScore,
						"Won": 1,
						"For": hScore,
						"Played": 1,
						"Points": 3
					};
					awayTeamArgs = {
						"Against": hScore,
						"Lost": 1,
						"For": aScore,
						"Played": 1,
						"Points": 0
					};
				} else {
					homeTeamArgs = {
						"Against": aScore,
						"Lost": 1,
						"For": hScore,
						"Played": 1,
						"Points": 0
					};
					awayTeamArgs = {
						"Against": hScore,
						"Won": 1,
						"For": aScore,
						"Played": 1,
						"Points": 3
					};
				}
			}
		}

		function scores(teamID, obj, operator) {
			//console.log(group[teamID])
			//console.log(application.groupData.groupTeamData[self.groupName][teamID])
			for (var prop in obj) {
				var current = group[teamID][prop],
					latest = obj[prop];
				if (operator === 'decrement') {
					group[teamID][prop] = current - latest;
				} else {
					group[teamID][prop] = current + latest;
				}

			}
			//console.log(group[teamID])
		}

	}
});

var knockout = application.Class.extend({
	init: function() {

		var self = this,
			koIndex;

		self.element = $('#Finals');
		self.tableElement = $('table', self.element);
		self.inputs = $('input.score', self.element);
		self.matches = $('.match', self.element);

		self.generateKoIndex();
		self.populateLast16();
		self.addEvents();

		self.matches.each(function() {
			var t1 = $('.team-1 .team-name', $(this)).text(),
				t2 = $('.team-2 .team-name', $(this)).text();

			$('.team-1 .score', $(this)).attr('data-defaultname', t1);
			$('.team-2 .score', $(this)).attr('data-defaultname', t1);
		})
	},
	addEvents: function() {

		var self = this;
		self.inputs.on('keyup', self.handleInputChange.bind(self));

	},
	generateKoIndex: function() {

		var self = this,
			koIndex = [];

		ko = _.filter(matches, function(a) {
			return a.match["RoundNumber"] > 1
		});

		if (ko.length === 16) {

			$(ko).each(function(i) {
				koIndex.push(ko[i]['match']['id'])
			});

			koIndex = _.sortBy(koIndex, function(num) {
				return num;
			});

			this.koIndex = koIndex;
		}
	},
	refresh: function() {
		this.populateLast16();
	},
	populateLast16: function() {

		var self = this,
			pattern = [
				['A1', 'B2'],
				['C1', 'D2'],
				['E1', 'F2'],
				['G1', 'H2'],
				['B1', 'A2'],
				['D1', 'C2'],
				['F1', 'E2'],
				['H1', 'G2']
			],
			winners = application.groupData.groupWinners,
			rounds = self.koIndex;

		for (var i = 0, len = pattern.length; i < len; i++) {

			var teamID = 1;

			for (var j = 0, slen = pattern[i].length; j < slen; j++) {
				var winnerOf = pattern[i][j];
				group = winnerOf.charAt(0),
				position = winnerOf.charAt(1)
				teamName = winners[group][position - 1];

				$('.matchID-' + rounds[i] + ' .teamID-' + teamID + ' .team-name').text(teamName);
				$('.matchID-' + rounds[i] + ' .teamID-' + teamID + ' .score').attr('data-teamname', teamName);
				teamID++;

			}
		}
	},
	handleInputChange: function(e) {

		var self = this,
			match = $(e.currentTarget).attr('data-match'),
			readOnly = $(e.currentTarget).attr('readonly'),
			parent = '.matchID-' + match,
			hScore = $('.team-1 input', parent).val() || null,
			hName = $('.team-1 .team-name', parent).text(),
			aScore = $('.team-2 input', parent).val() || null,
			aName = $('.team-2 .team-name', parent).text();

		this.update({
			match: parseInt(match),
			hScore: (hScore === null) ? null : parseInt(hScore),
			hName: hName,
			aScore: (aScore === null) ? null : parseInt(aScore),
			aName: aName
		})

	},
	update: function(o) {

		var self = this,
			match = o.match,
			hScore = o.hScore,
			hName = o.hName,
			aScore = o.aScore,
			aName = o.aName,
			winner = undefined,
			rounds = self.koIndex,
			indexOfCurrent = rounds.indexOf(match),
			order = (indexOfCurrent % 2 == 0) ? 1 : 2,
			nextIndex = self.whereNext(indexOfCurrent)
			nextIndexOfMatchWinner = nextIndex.winner,
			nextIndexOfMatchLoser = nextIndex.loser;

		if (hScore > aScore && aScore !== null) {
			winner = hName;
			loser = aName;
		} else if (aScore > hScore && hScore !== null) {
			winner = aName;
			loser = hName;
		}

		if (hScore == null && aScore == null) {
			var dName = $('.matchID-' + rounds[nextIndexOfMatchWinner] + ' .teamID-' + order + ' .score').attr('data-defaultname');
			$('.matchID-' + rounds[nextIndexOfMatchWinner] + ' .teamID-' + order + ' .team-name').text(dName).trigger('keyup');
		}

		//only win or lose in the finals
		if (winner !== undefined) {

			if (nextIndexOfMatchLoser) {

				var t1parent = '.matchID-' + rounds[12],
					t1hScore = $('.team-1 .score', t1parent).val() || null,
					t1hName = $('.team-1 .team-name', t1parent).text(),
					t1aScore = $('.team-2 input', t1parent).val() || null,
					t1aName = $('.team-2 .team-name', t1parent).text(),
					t2parent = '.matchID-' + rounds[13],
					t2hScore = $('.team-1 input', t2parent).val() || null,
					t2hName = $('.team-1 .team-name', t2parent).text(),
					t2aScore = $('.team-2 input', t2parent).val() || null,
					t2aName = $('.team-2 .team-name', t2parent).text(),
					t1loser, t1loserName, t2loser, t2loserName, winner, lose;

				if (t1hScore > t1aScore && t1aScore !== null) {
					t1loser = t1aScore;
					t1loserName = t1aName;
				} else if (t1aScore > t1hScore) {
					t1loser = t1hScore;
					t1loserName = t1hName;
				}

				if (t1loserName !== undefined) {
					$('.matchID-' + rounds[14] + ' .teamID-1 .team-name').text(t1loserName);
					$('.matchID-' + rounds[14] + ' .teamID-1 .score').attr('data-teamname', t1loserName);
				}

				if (t2hScore > t2aScore && t2hScore !== null) {
					t2loser = t2aScore;
					t2loserName = t2aName;
				} else if (t2aScore > t2hScore) {
					t2loser = t2hScore;
					t2loserName = t2hName;
				}

				if (t2loserName !== undefined) {
					$('.matchID-' + rounds[14] + ' .teamID-2 .team-name').text(t2loserName);
					$('.matchID-' + rounds[14] + ' .teamID-2 .score').attr('data-teamname', t2loserName);
				}

			}

		}

		$('.matchID-' + rounds[nextIndexOfMatchWinner] + ' .teamID-' + order + ' .team-name').text(winner);
		$('.matchID-' + rounds[nextIndexOfMatchWinner] + ' .teamID-' + order + ' .score').attr('data-teamname', winner).trigger('keyup');

	},
	whereNext: function(indexOfCurrent) {

		var indexOfNext = undefined,
			winner = undefined,
			loser = undefined;

		switch (indexOfCurrent) {
			case 0:
			case 1:
				winner = 8;
				break;
			case 2:
			case 3:
				winner = 9;
				break;
			case 4:
			case 5:
				winner = 10;
				break;
			case 6:
			case 7:
				winner = 11;
				break;
			case 8:
			case 9:
				winner = 12;
				break;
			case 10:
			case 11:
				winner = 13;
				break;
			case 12:
				winner = 15;
				loser = 14;
				break;
			case 13:
				winner = 15;
				loser = 14;
				break;
			default:
		}
		indexOfNext = {
			winner: winner,
			loser: loser
		}
		return indexOfNext;
	}
});