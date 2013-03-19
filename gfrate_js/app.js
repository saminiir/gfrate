var express = require('express')
  , passport = require('passport')
  , routes = require('./routes')
  , oauth = require('./oauth')
  , path = require('path');

debugger;

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

require('./auth');

app.get('/', routes.index);
app.get('/login', routes.loginForm);
app.post('/login', routes.loginValidation);
app.get('/logout', routes.logout);

app.get('/authorize', oauth.userAuthorization);
app.post('/authorize/decision', oauth.userDecision);

app.post('/oauth/request_token', oauth.requestToken);
app.get('/oauth/request_token', oauth.requestToken);
app.post('/oauth/access_token', oauth.accessToken);

var port = process.env.PORT || 5000;
app.listen(port, function() {
  console.log("Listening on " + port);
});

module.exports = app;
