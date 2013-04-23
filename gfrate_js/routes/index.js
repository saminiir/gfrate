var passport = require('passport')
	, login = require('connect-ensure-login')
  , url = require('url')
/*
 * GET home page.
 */

exports.index = function(req, res){
  res.render('index', { title: 'RateMyPartner' });
};

exports.loginForm = function(req, res){
  res.render('login', { title: 'Login' });
};

exports.loginValidation = passport.authenticate('local',
			  { successReturnToOrRedirect: '/',
			    failureRedirect: '/login' });

exports.logout = function(req, res) {
  req.logout();
  res.redirect('/');
}

exports.verified =[
  login.ensureLoggedIn(), function(req, res){
  var url_parts = url.parse(req.url, true);
  var query = url_parts.query;
  console.log(query);
  if ( typeof query.oauth_token !== 'undefined' && query.oauth_token !== null )
  {
     if ( typeof query.oauth_verifier !== 'undefined' && query.oauth_verifier !== null )
     {
        res.render('verified', { title: 'Verified', locals: { oauth_token: query.oauth_token, oauth_verifier: query.oauth_verifier}});
     }
  } else {
     req.logout();
     res.redirect('/');
  }
}
]