var express = require('express');
var app = express();
const fs = require('fs');


app.use('/bower_components', express.static(__dirname + '/bower_components'));
app.use('/css', express.static(__dirname + '/css'));
app.use('/images', express.static(__dirname + '/images'));
app.use('/scripts_angular', express.static(__dirname + '/scripts_angular'));
app.use('/views', express.static(__dirname + '/views'));


app.get('/', function(req, res) {
	fs.readFile("index.html", 'utf8', function(err, text){
		console.log("RENDER INDEX.HTML");
        res.send(text);
    });
});

app.get('/games.json', function(req, res) {
	fs.readFile("games.json", 'utf8', function(err, text){
		console.log("RENDER GAMES.JSON");
        res.send(text);
    });
});

app.listen(8080, '127.0.0.1');
console.log('Serveur Predictor EURO 2016 Front-End : START');