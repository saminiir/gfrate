var oauthorize = require('oauthorize')
  , express = require('express')
  , passport = require('passport')
  , routes = require('./routes')
  , path = require('path');

var app = express();
app.configure(function(){
    app.use(express.logger('dev'));
    app.use(express.favicon());
    app.use(express.cookieParser());
    app.use(express.bodyParser());
    app.use(express.methodOverride());
    app.set('views', __dirname + '/views');
    app.set('view engine', 'jade');
    app.use(express.session({ secret: 'secret' }));
    app.use(passport.initialize());
    app.use(passport.session());
    app.use(app.router);
    app.use(express.static(path.join(__dirname, 'public')));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});


var server = oauthorize.createServer();

require('./auth');

app.get('/', routes.index);
app.get('/login', routes.loginForm);
app.post('/login', routes.loginValidation);

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
