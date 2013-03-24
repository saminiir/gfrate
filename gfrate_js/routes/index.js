var passport = require('passport')
	, login = require('connect-ensure-login')
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
