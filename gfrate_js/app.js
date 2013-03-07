var oauthorize = require('oauthorize');
var express = require('express');
var passport = require('passport');

var app = express();
var server = oauthorize.createServer();

app.get('/', function(request, response) {
  response.send('Hello World!');
});

app.post('/request_token',
  passport.authenticate('consumer', { session: false }),
  server.requestToken(function(client, callbackURL, done) {
    var token = utils.uid(8)
      , secret = utils.uid(32)

    var t = new RequestToken(token, secret, client.id, callbackURL);
    t.save(function(err) {
      if (err) { return done(err); }
      return done(null, token, secret);
    });
  }));

var port = process.env.PORT || 5000;
app.listen(port, function() {
  console.log("Listening on " + port);
});
